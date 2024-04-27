package com.mobaker.mobilecomputing

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseError
import com.mobaker.mobilecomputing.callback.TaskFirebaseCallback
import com.mobaker.mobilecomputing.databinding.ActivityManagerBinding
import com.mobaker.mobilecomputing.databinding.ActivityStatisticsBinding
import com.mobaker.mobilecomputing.models.Task
import com.mobaker.mobilecomputing.services.ITaskService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class StatisticsActivity : ComponentActivity() {
    private lateinit var binding: ActivityStatisticsBinding

    @Inject
    lateinit var taskService: ITaskService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycleView.layoutManager = LinearLayoutManager(this)
        taskService.getByIsCompleted(true, object: TaskFirebaseCallback {
            override fun onDataReceived(tasks: List<Task>) {
                val adapter = StatsAdaptor( tasks.toCollection(ArrayList()),this@StatisticsActivity)
                binding.recycleView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@StatisticsActivity, error.message, Toast.LENGTH_LONG).show()
            }

            override fun onError(error: String) {
                Toast.makeText(this@StatisticsActivity, error, Toast.LENGTH_LONG).show()
            }


        })

    }




    private class StatsAdaptor(  private var dataSet: ArrayList<Task>,
        private var context: Context
    ) : RecyclerView.Adapter<StatsAdaptor.ViewHolder>(){

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val taskIdTv: TextView
            val taskNameTv: TextView
            val taskDescriptionTv: TextView
            val completedByTv: TextView

            init {
                taskIdTv = view.findViewById(R.id.taskIdTv)
                taskNameTv = view.findViewById(R.id.taskNameTv)
                taskDescriptionTv = view.findViewById(R.id.taskDescriptionTv)
                completedByTv = view.findViewById(R.id.completedByTv)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.task, parent, false)

            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
           return dataSet.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.taskIdTv.text = dataSet[position].id
            holder.taskNameTv.text = dataSet[position].name
            holder.taskDescriptionTv.text = dataSet[position].description
            holder.completedByTv.text = dataSet[position].completedBy
        }

    }

}

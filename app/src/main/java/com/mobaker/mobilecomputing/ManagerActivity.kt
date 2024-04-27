package com.mobaker.mobilecomputing



import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.mobaker.mobilecomputing.databinding.ActivityManagerBinding
import com.mobaker.mobilecomputing.models.Task
import com.mobaker.mobilecomputing.services.ITaskService
import com.psut.farmjo.util.SharedPreferencesUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManagerBinding

    @Inject
    lateinit var taskService: ITaskService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.submitBtn.setOnClickListener {
            addTask()
        }
        binding.managerStatsBtn.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }
        binding.managerLogoutBtn.setOnClickListener {
            SharedPreferencesUtil.remove(this)
            val intent = Intent(this, MainActivity::class.java)
            taskService.reset()
            startActivity(intent)
            finish()
        }
    }


    private fun addTask() {
        if (LocationUtil.isLocationPermissionGranted(this)) {
            // Permission is granted, get current location
            LocationUtil.getCurrentLocation(this) { location ->
                val task = Task(
                    null,
                    binding.nameEditTxt.text.toString(),
                    binding.descrpitionEt.text.toString(),
                    location.longitude,
                    location.latitude,
                    false,
                    null
                )
                taskService.add(task)
                binding.nameEditTxt.text.clear()
                binding.descrpitionEt.text.clear()
                Toast.makeText(this, "Task was added successfully!", Toast.LENGTH_LONG).show()
            }
        } else {
            // Location permission has not been granted yet, request it
            LocationUtil.requestLocationPermission(this)
            Toast.makeText(this, "Please enable location permission!!", Toast.LENGTH_LONG).show()
        }
    }

}

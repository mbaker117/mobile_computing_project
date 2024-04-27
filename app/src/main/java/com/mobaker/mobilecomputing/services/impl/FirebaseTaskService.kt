package com.mobaker.mobilecomputing.services.impl

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.mobaker.mobilecomputing.models.Task
import com.mobaker.mobilecomputing.services.ITaskService
import com.mobaker.mobilecomputing.callback.TaskFirebaseCallback
import java.util.LinkedList
import javax.inject.Inject

class FirebaseTaskService  @Inject constructor(): ITaskService {
    private var database = Firebase.database.reference.child("tasks")
    private var listenerList = LinkedList<ValueEventListener>()

    override fun add(task: Task) {
        val key = database.push().key
        task.id = key
        if (key != null) {
            database.child(key).setValue(task)
        }
    }

    override fun getAll(callback: TaskFirebaseCallback) {
        val eventListener = getValueEventListener(callback)
        database.addValueEventListener(eventListener)
        listenerList.add(eventListener)

    }

    override fun getById(id: String, callback: TaskFirebaseCallback) {
        val eventListener = getValueEventListener(callback)

        database.orderByKey().equalTo(id)
            .addListenerForSingleValueEvent(eventListener)
        listenerList.add(eventListener)

    }

    override fun getByIsCompleted(isCompleted: Boolean, callback: TaskFirebaseCallback) {
        val eventListener = getValueEventListener(callback)
        database.orderByChild("completed").equalTo(isCompleted)
            .addValueEventListener(eventListener)
        listenerList.add(eventListener)
    }

    private fun getValueEventListener(callback: TaskFirebaseCallback) =
        object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                handleData(dataSnapshot, callback)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback.onCancelled(databaseError)
            }
        }

    private fun handleData(
        dataSnapshot: DataSnapshot,
        callback: TaskFirebaseCallback
    ) {
        val taskList = mutableListOf<Task>()

        for (taskSnapshot in dataSnapshot.children) {
            val task = taskSnapshot.getValue(Task::class.java)
            task?.let {
                taskList.add(it)
            }
        }

        callback.onDataReceived(taskList)
    }

    override fun update(task: Task) {
        database.child("${task.id}").setValue(task)
    }

    override fun reset() {
        for(event in listenerList){
            database.removeEventListener(event)
        }
    }

}
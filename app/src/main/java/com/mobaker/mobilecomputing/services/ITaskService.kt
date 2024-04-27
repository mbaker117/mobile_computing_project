package com.mobaker.mobilecomputing.services

import com.google.firebase.database.ValueEventListener
import com.mobaker.mobilecomputing.callback.TaskFirebaseCallback
import com.mobaker.mobilecomputing.models.Task

interface ITaskService {


    fun add(task: Task)
    fun getAll(callback: TaskFirebaseCallback)
    fun getById(id: String,callback: TaskFirebaseCallback)
    fun getByIsCompleted(isCompleted: Boolean, callback: TaskFirebaseCallback)
    fun update(task: Task)
    fun reset()
}
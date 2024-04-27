package com.mobaker.mobilecomputing.callback

import com.google.firebase.database.DatabaseError
import com.mobaker.mobilecomputing.models.Task
import com.mobaker.mobilecomputing.models.User

interface TaskFirebaseCallback {
    fun onDataReceived(tasks: List<Task>)
    fun onCancelled(error: DatabaseError)
    fun onError(error: String)
}
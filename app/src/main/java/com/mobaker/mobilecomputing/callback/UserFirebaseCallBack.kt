package com.mobaker.mobilecomputing.callback

import com.google.firebase.database.DatabaseError
import com.mobaker.mobilecomputing.models.User

interface UserFirebaseCallback {
    fun onDataReceived(user: User)
    fun onCancelled(error: DatabaseError)
    fun onError(error: String)
}
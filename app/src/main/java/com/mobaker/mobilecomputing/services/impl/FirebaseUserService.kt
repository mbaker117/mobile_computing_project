package com.mobaker.mobilecomputing.services.impl

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.mobaker.mobilecomputing.models.User
import com.mobaker.mobilecomputing.callback.UserFirebaseCallback
import com.mobaker.mobilecomputing.services.IUserService
import javax.inject.Inject

class FirebaseUserService @Inject constructor() : IUserService {
    private val database = Firebase.database.reference.child("users")

    override fun getUserByUsername(username: String, callback: UserFirebaseCallback) {
        database.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.children.firstOrNull()?.getValue(User::class.java)
                        callback.onDataReceived(user!!)
                        return
                    }
                    callback.onError("user doesn't exists")
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.onCancelled(error)
                }
            })

    }

    override fun getUserByUsernameAndPassword(
        username: String, password: String, callback: UserFirebaseCallback
    ) {
        database.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.children.firstOrNull()
                            ?.getValue(User::class.java)?.password == password
                    ) {
                        val user = snapshot.children.firstOrNull()?.getValue(User::class.java)
                        callback.onDataReceived(user!!)
                        return
                    }
                    callback.onError("Invalid username or password")
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.onCancelled(error)
                }
            })

    }

    override fun addUser(user: User, callback: UserFirebaseCallback) {
        database.orderByChild("username").equalTo(user.username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Username already exists
                        callback.onError("Username already exists")
                        return
                    }

                    val userId = database.push().key ?: return
                    database.child(userId).setValue(user).addOnSuccessListener {
                        callback.onDataReceived(user)
                    }.addOnFailureListener { e ->
                        callback.onError(e.message ?: "Failed to sign up")
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    callback.onCancelled(databaseError)
                }
            })
    }


}
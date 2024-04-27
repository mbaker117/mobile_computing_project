package com.mobaker.mobilecomputing.services

import com.mobaker.mobilecomputing.callback.UserFirebaseCallback
import com.mobaker.mobilecomputing.models.User


interface IUserService {

   fun getUserByUsername(username:String, callback: UserFirebaseCallback)
   fun getUserByUsernameAndPassword(username:String, password:String,callback: UserFirebaseCallback)
   fun addUser(user:User, callback: UserFirebaseCallback)
}
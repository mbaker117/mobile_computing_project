package com.mobaker.mobilecomputing.models

import com.mobaker.mobilecomputing.enums.UserRole

data class User(val username:String?, val password:String?, val userRole: UserRole?, val name: String?){

    constructor():this(null, null, null, null)
}

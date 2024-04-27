package com.mobaker.mobilecomputing.models

data class Task(
    var id: String?, var name: String?, var description: String?, var longitude: Double?, var latitude: Double?, var isCompleted:Boolean, var completedBy: String?
) {

    constructor() : this(null, null, null, null, null, false, null)
}

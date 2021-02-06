package com.erikriosetiawan.mynotesapp.entity

data class Note(
    var id: Int = 0,
    var title: String,
    var description: String? = null,
    var date: String? = null
)

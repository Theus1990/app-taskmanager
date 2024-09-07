package com.example.taskmanager

data class Task(
    var id: Int = 0,
    var name: String,
    var description: String,
    var date: String,
    var imagePath: String = ""
)

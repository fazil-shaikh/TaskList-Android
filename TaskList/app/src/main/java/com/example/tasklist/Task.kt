package com.example.tasklist

data class Task(var tId : Int,
                var tTitle : String,
                var tStatus : Boolean,
                var tDueDate : String,
                var tNotes : String) {
}
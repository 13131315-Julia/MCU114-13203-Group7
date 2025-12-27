package com.example.endofterm

import java.io.Serializable
import java.util.Date

data class Expense(
    val id: Int,
    val date: String,
    val category: String,
    val amount: Double,
    val notes: String
) : Serializable
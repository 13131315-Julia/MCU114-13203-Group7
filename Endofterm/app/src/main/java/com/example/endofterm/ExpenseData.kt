package com.example.endofterm

object ExpenseData {
    private val expenses = mutableListOf<Expense>()

    fun addExpense(expense: Expense) {
        expenses.add(0, expense) // 新資料加在最前面
    }

    fun getAllExpenses(): List<Expense> {
        return expenses.toList()
    }

    fun getTotalAmount(): Double {
        return expenses.sumOf { it.amount }
    }

    fun getExpensesByCategory(category: String): List<Expense> {
        return expenses.filter { it.category == category }
    }
}
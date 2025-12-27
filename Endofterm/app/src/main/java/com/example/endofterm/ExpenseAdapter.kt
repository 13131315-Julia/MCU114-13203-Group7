package com.example.endofterm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private val expenseList: List<Expense>,
    private val onItemClick: (Expense) -> Unit = {}
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)
        val tvNotes: TextView = itemView.findViewById(R.id.tv_notes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]
        holder.tvDate.text = expense.date
        holder.tvCategory.text = expense.category
        holder.tvAmount.text = "$${"%.2f".format(expense.amount)}"
        holder.tvNotes.text = expense.notes

        // 點擊事件
        holder.itemView.setOnClickListener {
            onItemClick(expense)
        }
    }

    override fun getItemCount(): Int = expenseList.size
}
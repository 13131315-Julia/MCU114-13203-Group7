package com.example.endofterm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.endofterm.databinding.LayRecsBinding

class RecsFrag : Fragment() {
    private var _binding: LayRecsBinding? = null
    private val binding get() = _binding!!

    private val expenseList = mutableListOf<Expense>()
    private val filteredList = mutableListOf<Expense>()
    private lateinit var expenseAdapter: ExpenseAdapter

    private var selectedCategory = "全部" // 預設顯示全部
    private val filterOptions = arrayOf("全部", "餐飲", "交通", "娛樂", "購物", "房租", "水電費", "電話費", "醫療", "教育", "其他")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayRecsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 設置 RecyclerView
        setupRecyclerView()

        // 設置篩選按鈕
        setupFilterButton()

        // 載入資料
        loadData()
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter(filteredList) { expense ->
            // 點擊項目顯示詳情
            showExpenseDetail(expense)
        }
        binding.recyclerViewRecords.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = expenseAdapter
        }
    }

    private fun setupFilterButton() {
        binding.btnFilter.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun showFilterDialog() {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("選擇分類")

        builder.setItems(filterOptions) { dialog, which ->
            val selected = filterOptions[which]
            selectedCategory = selected
            binding.btnFilter.text = "篩選: $selected"
            filterExpenses(selected)
            dialog.dismiss()
        }

        builder.setNegativeButton("取消", null)
        builder.show()
    }

    private fun showExpenseDetail(expense: Expense) {
        val message = """
            日期：${expense.date}
            項目：${expense.category}
            金額：$${"%.2f".format(expense.amount)}
            備註：${expense.notes}
        """.trimIndent()

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("消費詳情")
            .setMessage(message)
            .setPositiveButton("確定", null)
            .show()
    }

    private fun filterExpenses(category: String) {
        filteredList.clear()

        if (category == "全部") {
            filteredList.addAll(expenseList)
        } else {
            // 篩選指定分類
            filteredList.addAll(expenseList.filter { it.category == category })
        }

        // 更新顯示
        updateDisplay()
    }

    private fun loadData() {
        expenseList.clear()
        expenseList.addAll(ExpenseData.getAllExpenses())

        // 初始顯示全部
        filterExpenses(selectedCategory)
    }

    private fun updateDisplay() {
        expenseAdapter.notifyDataSetChanged()

        // 顯示統計資訊
        val total = filteredList.sumOf { it.amount }
        val count = filteredList.size

        // 確保 textView 存在
        if (binding.tvTitle != null) {
            binding.tvTitle.text = "消費紀錄 (${count}筆, 共$${"%.2f".format(total)})"
        }

        // 如果沒有資料，顯示提示
        if (filteredList.isEmpty()) {
            if (binding.tvEmpty != null) {
                binding.tvEmpty.visibility = View.VISIBLE
            }
            // 也可以更新標題顯示無資料
            if (binding.tvTitle != null) {
                binding.tvTitle.text = "消費紀錄 (無資料)"
            }
        } else {
            if (binding.tvEmpty != null) {
                binding.tvEmpty.visibility = View.GONE
            }
        }
    }

    // 重新整理資料
    fun refreshData() {
        loadData()
    }

    override fun onResume() {
        super.onResume()
        // 每次回到頁面時重新整理資料
        refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
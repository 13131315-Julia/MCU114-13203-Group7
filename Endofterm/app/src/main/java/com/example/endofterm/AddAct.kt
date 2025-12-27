package com.example.endofterm

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.endofterm.databinding.LayAddBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddAct : AppCompatActivity() {

    private lateinit var binding: LayAddBinding
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // 固定項目列表
    private val categoryList = arrayOf(
        "餐飲",
        "交通",
        "娛樂",
        "購物",
        "房租",
        "水電費",
        "電話費",
        "醫療",
        "教育",
        "其他"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "新增消費"

        // 設置初始日期為今天
        updateDateDisplay()

        // 設置日期選擇按鈕
        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        // 設置下拉選擇器
        setupSpinner()

        // 儲存按鈕
        binding.btnSave.setOnClickListener {
            saveExpense()
        }

        // 取消按鈕
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun updateDateDisplay() {
        binding.tvDate.text = dateFormat.format(calendar.time)
    }

    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                updateDateDisplay()
            },
            year,
            month,
            day
        ).show()
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categoryList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun saveExpense() {
        val date = binding.tvDate.text.toString()
        val category = binding.spinnerCategory.selectedItem.toString()
        val amount = binding.etAmount.text.toString()
        val notes = binding.etNotes.text.toString()

        if (amount.isEmpty()) {
            Toast.makeText(this, "請輸入金額", Toast.LENGTH_SHORT).show()
            return
        }

        // 創建消費物件
        val expense = Expense(
            id = (0..1000).random(), // 簡單的隨機 ID
            date = date,
            category = category,
            amount = amount.toDouble(),
            notes = notes.ifEmpty { "無" }
        )

        // 儲存到全域變數或資料庫（這裡先用簡單方式）
        ExpenseData.addExpense(expense)

        Toast.makeText(this, "儲存成功！", Toast.LENGTH_SHORT).show()

        // 關閉頁面，返回總覽
        finish()
    }
}
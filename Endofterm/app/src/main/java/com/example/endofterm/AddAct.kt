package com.example.endofterm

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.endofterm.databinding.LayAddBinding // 注意：Binding 名稱已變更

class AddAct : AppCompatActivity() { // 類別名稱變更為 AddAct

    private lateinit var binding: LayAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "新增消費"

        binding.btnSave.setOnClickListener {
            saveExpense()
        }

        binding.btnCancel.setOnClickListener {
            finish() // 取消：直接關閉 Activity
        }
    }

    private fun saveExpense() {
        val amount = binding.etAmount.text.toString()
        val item = binding.etItem.text.toString()

        if (amount.isEmpty() || item.isEmpty()) {
            Toast.makeText(this, "金額和項目不能為空", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: 執行資料庫儲存邏輯

        Toast.makeText(this, "儲存成功！金額：$amount, 項目：$item", Toast.LENGTH_LONG).show()
        finish()
    }
}
package com.example.endofterm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.endofterm.databinding.LayRegisterFormBinding // 假設佈局檔案名為 lay_register_form.xml

class RegisterFrag : Fragment() {

    private var _binding: LayRegisterFormBinding? = null
    // 假設您有一個名為 lay_register_form.xml 的佈局，其中包含 etUsername, etPassword, btnRegister
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 載入 LayRegisterFormBinding
        _binding = LayRegisterFormBinding.inflate(inflater, container, false)

        // 設定註冊按鈕的點擊事件
        binding.btnRegister.setOnClickListener {
            performRegistration()
        }

        return binding.root
    }

    private fun performRegistration() {
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()

        if (username.isBlank() || password.isBlank()) {
            Toast.makeText(context, "帳號和密碼不能為空", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: 執行實際的註冊和資料庫儲存邏輯

        // 成功註冊的模擬反饋
        Toast.makeText(context, "註冊成功！帳號：$username", Toast.LENGTH_LONG).show()

        // 註冊成功後，可以選擇讓 ViewPager2 滑動回登入頁面 (索引 0)
        // (activity as? MainAct)?.binding?.loginViewPager?.currentItem = 0
        // 但由於 ViewPager2 允許使用者自行滑動，通常不用程式碼強制切換
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
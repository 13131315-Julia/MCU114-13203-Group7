package com.example.endofterm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.endofterm.databinding.LayRegisterFormBinding

class RegisterFrag : Fragment() {

    private var _binding: LayRegisterFormBinding? = null
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

        // 設定返回登入按鈕的點擊事件
        binding.btnBackToLogin.setOnClickListener {
            goToLogin()
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

        // 註冊成功後，返回到登入頁面
        goToLogin()
    }

    private fun goToLogin() {
        // 返回登入頁面（通過 ViewPager 切換到索引 0）
        val mainAct = activity as? MainAct
        mainAct?.binding?.loginViewPager?.currentItem = 0

        // 清空註冊欄位（如果用戶返回後再註冊）
        if (binding.etUsername.text != null) {
            binding.etUsername.text?.clear()
        }
        if (binding.etPassword.text != null) {
            binding.etPassword.text?.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
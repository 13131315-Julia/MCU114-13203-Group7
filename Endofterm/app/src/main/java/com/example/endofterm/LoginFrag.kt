package com.example.endofterm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.endofterm.databinding.LayLoginFormBinding

class LoginFrag : Fragment() {

    private var _binding: LayLoginFormBinding? = null
    private val binding get() = _binding!!

    // 移除登入驗證資訊，因為不再需要檢查帳號密碼

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 載入 LayLoginFormBinding
        _binding = LayLoginFormBinding.inflate(inflater, container, false)

        // 設定登入按鈕的點擊事件
        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        return binding.root
    }

    private fun performLogin() {
        // 獲取輸入的帳號密碼 (即使不檢查，獲取輸入仍可能是為了後續顯示)
        val username = binding.etUsername.text.toString()
        // val password = binding.etPassword.text.toString() // 密碼可以省略不取

        // ⚠️ 關鍵修改：直接跳轉，不需要進行 if 判斷和驗證
        Toast.makeText(context, "自動登入成功！", Toast.LENGTH_SHORT).show()

        // 跳轉到主畫面 HomeAct
        val intent = Intent(activity, HomeAct::class.java)
        startActivity(intent)
        activity?.finish() // 關閉 MainAct

        // 舊的驗證邏輯已被移除
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
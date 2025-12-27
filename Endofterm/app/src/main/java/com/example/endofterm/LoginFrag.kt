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

    // 固定帳號密碼：123 (使用駝峰命名法)
    private val fixedUsername = "123"
    private val fixedPassword = "123"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LayLoginFormBinding.inflate(inflater, container, false)

        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        binding.btnGoToRegister.setOnClickListener {
            goToRegister()
        }

        return binding.root
    }

    private fun performLogin() {
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "請輸入帳號和密碼", Toast.LENGTH_SHORT).show()
            return
        }

        if (username == fixedUsername && password == fixedPassword) {
            Toast.makeText(context, "登入成功！", Toast.LENGTH_SHORT).show()

            val intent = Intent(activity, HomeAct::class.java)
            startActivity(intent)
            activity?.finish()
        } else {
            Toast.makeText(context, "帳號或密碼錯誤", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToRegister() {
        val mainAct = activity as? MainAct
        mainAct?.binding?.loginViewPager?.currentItem = 1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
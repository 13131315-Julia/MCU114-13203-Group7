package com.example.endofterm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.endofterm.databinding.LayMainBinding

class MainAct : AppCompatActivity() {

    private lateinit var _binding: LayMainBinding
    val binding: LayMainBinding
        get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = LayMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        setupLoginViewPager()
    }

    private fun setupLoginViewPager() {
        // 現在只包含登入和註冊頁面
        val fragmentList = arrayListOf<Fragment>(
            LoginFrag(),       // 第一頁：登入表單 (索引 0)
            RegisterFrag()     // 第二頁：註冊表單 (索引 1)
        )

        // 使用 PageAdap 來綁定 ViewPager2
        val adapter = PageAdap(fragmentList, this)
        binding.loginViewPager.adapter = adapter

        // 禁用 ViewPager 滑動功能（移除滑動）
        binding.loginViewPager.isUserInputEnabled = false
    }
}
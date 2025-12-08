package com.example.endofterm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.endofterm.databinding.LayHomeBinding

class HomeAct : AppCompatActivity() {

    private lateinit var binding: LayHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 載入佈局並設置 View Binding
        binding = LayHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupBottomNavigation()
    }

    // 設置 ViewPager2 (滑動功能的核心)
    private fun setupViewPager() {
        // 定義三個頁面 (Fragment)
        val fragmentList = arrayListOf<Fragment>(
            DashFrag(),    // 總覽 (索引 0)
            RecsFrag(),    // 紀錄 (索引 1)
            SetFrag()      // 設定 (索引 2)
        )

        // 創建適配器並綁定到 ViewPager
        val adapter = PageAdap(fragmentList, this)
        binding.viewPager.adapter = adapter

        // 監聽 ViewPager 的滑動事件，同步更新 BottomNavigationView 的選中狀態
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // 根據滑動到的頁面位置，找到對應的 Menu Item ID
                val itemId = when (position) {
                    0 -> R.id.nav_dash
                    1 -> R.id.nav_recs
                    2 -> R.id.nav_set
                    else -> R.id.nav_dash
                }

                // 更新底部導航欄的選中狀態
                binding.bottomNavigationView.selectedItemId = itemId
            }
        })
    }

    // 設置底部導航欄
    private fun setupBottomNavigation() {
        // 監聽 BottomNavigationView 的點擊事件，同步更新 ViewPager 的頁面
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dash -> binding.viewPager.currentItem = 0
                R.id.nav_recs -> binding.viewPager.currentItem = 1
                R.id.nav_set -> binding.viewPager.currentItem = 2
                R.id.nav_add -> {
                    // 新增消費：跳轉到獨立 Activity
                    startActivity(Intent(this, AddAct::class.java))
                    // 返回 false 表示 ViewPager 不會切換頁面，選中狀態停留在當前頁
                    return@setOnItemSelectedListener false
                }
                else -> return@setOnItemSelectedListener false
            }
            // 返回 true 表示成功處理切換事件
            return@setOnItemSelectedListener true
        }
    }
}
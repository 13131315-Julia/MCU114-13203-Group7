package com.example.endofterm

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PageAdap(
    private val fragments: ArrayList<Fragment>, // 接收 Fragment 列表
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    // 告訴 ViewPager2 總共有幾個頁面
    override fun getItemCount(): Int {
        return fragments.size
    }

    // 告訴 ViewPager2 在指定位置要顯示哪個 Fragment
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
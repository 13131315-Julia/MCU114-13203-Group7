package com.example.endofterm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.endofterm.databinding.LaySetBinding  // 修正這裡

class SetFrag : Fragment() {
    private var _binding: LaySetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LaySetBinding.inflate(inflater, container, false)  // 修正這裡
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 只設置登出按鈕的點擊事件
        binding.itemLogout.setOnClickListener {
            performLogout()
        }

        // 其他項目不設置點擊事件
    }

    private fun performLogout() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("登出確認")
            .setMessage("確定要登出嗎？")
            .setPositiveButton("確定") { dialog, which ->
                executeLogout()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun executeLogout() {
        Toast.makeText(context, "已成功登出", Toast.LENGTH_SHORT).show()

        val intent = Intent(activity, MainAct::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
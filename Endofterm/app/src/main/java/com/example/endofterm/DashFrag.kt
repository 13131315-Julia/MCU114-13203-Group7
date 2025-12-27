package com.example.endofterm

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.example.endofterm.databinding.LayDashBinding
import java.text.SimpleDateFormat
import java.util.*

class DashFrag : Fragment() {

    private var _binding: LayDashBinding? = null
    private val binding get() = _binding!!

    private var selectedRange = "本週"

    private val chartColors = listOf(
        "#FF6B6B", "#4ECDC4", "#FFD166", "#06D6A0", "#118AB2",
        "#EF476F", "#7209B7", "#F15BB5", "#00BBF9", "#FF9E00"
    )

    // 確保這裡的字串與您存入資料庫/ExpenseData 的類別完全一致
    private val fixedCategories = listOf(
        "餐飲", "購物", "交通", "娛樂", "醫療",
        "水電費", "房租", "教育", "旅遊", "其他"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayDashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTimeRangeSpinner()
        updateDashboard()
    }

    private fun setupTimeRangeSpinner() {
        val timeRanges = arrayOf("本週", "本月", "本年", "全部")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, timeRanges)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerTimeRange.adapter = adapter
        binding.spinnerTimeRange.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedRange = timeRanges[position]
                updateDashboard()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateDashboard() {
        val filteredExpenses = getFilteredExpenses()
        val totalAmount = filteredExpenses.sumOf { it.amount }

        binding.tvTotalAmount.text = getString(R.string.format_currency, totalAmount)

        updatePieChart(filteredExpenses)
        updateDataStatistics(filteredExpenses)
    }

    private fun getFilteredExpenses(): List<Expense> {
        val allExpenses = ExpenseData.getAllExpenses()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return allExpenses.filter { expense ->
            try {
                val date = dateFormat.parse(expense.date) ?: return@filter false
                val expenseCal = Calendar.getInstance().apply { time = date }

                when (selectedRange) {
                    "本週" -> expenseCal.get(Calendar.WEEK_OF_YEAR) == calendar.get(Calendar.WEEK_OF_YEAR) &&
                            expenseCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                    "本月" -> expenseCal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                            expenseCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                    "本年" -> expenseCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                    else -> true
                }
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun updatePieChart(expenses: List<Expense>) {
        val categoryMap = expenses.groupBy { it.category }
            .mapValues { it.value.sumOf { exp -> exp.amount } }

        binding.pieChartContainer.removeAllViews()
        binding.legendContainer.removeAllViews()

        if (categoryMap.isEmpty()) {
            val tvEmpty = TextView(requireContext()).apply {
                text = getString(R.string.dashboard_empty_state)
                gravity = Gravity.CENTER
                setPadding(0, 40, 0, 40)
            }
            binding.pieChartContainer.addView(tvEmpty)
            return
        }

        val totalAmount = categoryMap.values.sum()
        val sortedCategories = categoryMap.toList().sortedByDescending { it.second }

        sortedCategories.forEachIndexed { index, (category, amount) ->
            val percentage = if (totalAmount > 0) ((amount / totalAmount) * 100).toInt() else 0

            val itemContainer = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 0, 0, 20)
            }

            val labelLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            labelLayout.addView(TextView(requireContext()).apply {
                text = category
                layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
                setTextColor("#333333".toColorInt())
            })

            labelLayout.addView(TextView(requireContext()).apply {
                text = getString(R.string.format_percentage, percentage)
                setPadding(16, 0, 16, 0)
                setTextColor(Color.GRAY)
            })

            labelLayout.addView(TextView(requireContext()).apply {
                text = getString(R.string.format_currency, amount)
                setTextColor("#333333".toColorInt())
            })

            val progressBar = ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal).apply {
                max = 100
                progress = percentage
                val color = chartColors[index % chartColors.size].toColorInt()
                progressDrawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_IN)
            }

            itemContainer.addView(labelLayout)
            itemContainer.addView(progressBar)
            binding.pieChartContainer.addView(itemContainer)
            addLegendItem(category, index)
        }
    }

    private fun addLegendItem(category: String, index: Int) {
        val legendItem = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(12, 4, 12, 4)
        }
        val colorView = View(requireContext()).apply {
            setBackgroundColor(chartColors[index % chartColors.size].toColorInt())
            layoutParams = LinearLayout.LayoutParams(25, 25).apply { marginEnd = 8 }
        }
        legendItem.addView(colorView)
        legendItem.addView(TextView(requireContext()).apply {
            text = category
            textSize = 12f
        })
        binding.legendContainer.addView(legendItem)
    }

    private fun updateDataStatistics(expenses: List<Expense>) {
        // 優化點：使用 .trim() 確保分類名稱比對正確
        val categoryAmounts = fixedCategories.associateWith { cat ->
            expenses.filter { it.category.trim() == cat.trim() }.sumOf { it.amount }
        }

        val tableLayout = TableLayout(requireContext()).apply {
            isStretchAllColumns = true
        }

        for (i in 0 until 5) {
            val row = TableRow(requireContext())
            row.addView(createStatCell(fixedCategories[i], categoryAmounts[fixedCategories[i]] ?: 0.0))
            row.addView(createStatCell(fixedCategories[i + 5], categoryAmounts[fixedCategories[i + 5]] ?: 0.0))
            tableLayout.addView(row)
        }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
        }

        container.addView(TextView(requireContext()).apply {
            text = getString(R.string.dashboard_stats_title, selectedRange)
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 20)
        })
        container.addView(tableLayout)

        val totalAmountSum = expenses.sumOf { it.amount }
        val totalFormatted = getString(R.string.format_currency, totalAmountSum)

        container.addView(TextView(requireContext()).apply {
            text = getString(R.string.dashboard_summary_footer, totalFormatted, expenses.size)
            gravity = Gravity.CENTER
            setPadding(0, 32, 0, 16)
        })

        // 修正點：確保將統計內容正確放入 UI 容器中
        binding.statisticsWrapper.apply {
            removeAllViews()
            addView(container)
        }
    }

    private fun createStatCell(name: String, amount: Double): View {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 15, 20, 15)

            addView(TextView(requireContext()).apply {
                text = name
                layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
                setTextColor("#444444".toColorInt())
            })
            addView(TextView(requireContext()).apply {
                text = getString(R.string.format_currency, amount)
                setTextColor(if (amount > 0) "#000000".toColorInt() else "#AAAAAA".toColorInt())
            })
        }
    }

    override fun onResume() {
        super.onResume()
        updateDashboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
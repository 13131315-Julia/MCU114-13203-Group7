package com.example.endofterm

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
        "#073B4C", "#7209B7", "#F72585", "#3A86FF", "#6A994E"
    )

    private val fixedCategories = listOf(
        "餐飲", "交通", "娛樂", "購物", "房租",
        "水電費", "電話費", "醫療", "教育", "其他"
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

        // 設置圓餅圖點擊監聽器
        binding.pieChartView.onSliceClickListener = { index, item ->
            Toast.makeText(requireContext(), "${item.label}: $${String.format("%.2f", item.value)}", Toast.LENGTH_SHORT).show()
        }

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

        binding.tvTotalAmount.text = String.format("$%.2f", totalAmount)
        binding.tvExpenseCount.text = "${filteredExpenses.size}筆消費"

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
        // 按照分類聚合消費金額
        val categoryMap = expenses.groupBy { it.category }
            .mapValues { it.value.sumOf { exp -> exp.amount }.toFloat() }

        binding.legendContainer.removeAllViews()

        if (categoryMap.isEmpty()) {
            binding.pieChartView.clearData()
            return
        }

        // 將數據轉換為 PieChartView 需要的格式
        val pieData = categoryMap.toList().map { (category, amount) ->
            category to amount
        }

        binding.pieChartView.setData(pieData)

        // 創建圖例
        createLegend(categoryMap)
    }

    private fun createLegend(categoryMap: Map<String, Float>) {
        val totalAmount = categoryMap.values.sum()

        categoryMap.toList().sortedByDescending { it.second }.forEachIndexed { index, (category, amount) ->
            val percentage = if (totalAmount > 0) ((amount / totalAmount) * 100).toInt() else 0

            val legendItem = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 4, 0, 4)
            }

            // 顏色方塊
            val colorView = View(requireContext()).apply {
                setBackgroundColor(chartColors[index % chartColors.size].toColorInt())
                layoutParams = LinearLayout.LayoutParams(25, 25).apply {
                    marginEnd = 12
                }
            }

            // 分類名稱和金額
            val textLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
            }

            textLayout.addView(TextView(requireContext()).apply {
                text = category
                textSize = 14f
                setTextColor("#333333".toColorInt())
                layoutParams = LinearLayout.LayoutParams(0, -2, 0.6f)
            })

            textLayout.addView(TextView(requireContext()).apply {
                text = "$percentage%"
                textSize = 14f
                setTextColor(Color.GRAY)
                layoutParams = LinearLayout.LayoutParams(0, -2, 0.2f)
                gravity = Gravity.END
            })

            textLayout.addView(TextView(requireContext()).apply {
                text = String.format("$%.2f", amount)
                textSize = 14f
                setTextColor("#333333".toColorInt())
                layoutParams = LinearLayout.LayoutParams(0, -2, 0.4f)
                gravity = Gravity.END
            })

            legendItem.addView(colorView)
            legendItem.addView(textLayout)
            binding.legendContainer.addView(legendItem)
        }
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
            text = String.format("📈 數據統計 (%s)", selectedRange)
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 20)
        })

        container.addView(tableLayout)

        val totalAmountSum = expenses.sumOf { it.amount }
        val totalFormatted = String.format("$%.2f", totalAmountSum)

        container.addView(TextView(requireContext()).apply {
            text = String.format("💰 總消費：%s  |  📝 總筆數：%d筆", totalFormatted, expenses.size)
            gravity = Gravity.CENTER
            setPadding(0, 32, 0, 16)
            textSize = 14f
        })

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
                textSize = 14f
            })

            addView(TextView(requireContext()).apply {
                text = String.format("$%.2f", amount)
                textSize = 14f
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
}1
package com.example.lab15

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private var items: ArrayList<String> = ArrayList()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var dbrw: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 取得資料庫實體
        dbrw = MyDBHelper(this).writableDatabase
        // 宣告Adapter並連結ListView
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        findViewById<ListView>(R.id.listView).adapter = adapter
        // 設定監聽器
        setListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbrw.close() // 關閉資料庫
    }

    // 設定監聽器
    private fun setListener() {
        val edBrand = findViewById<EditText>(R.id.edBrand)
        val edYear = findViewById<EditText>(R.id.edYear)
        val edPrice = findViewById<EditText>(R.id.edPrice)

        findViewById<Button>(R.id.btnInsert).setOnClickListener {
            // 判斷是否有填入所有欄位
            if (edBrand.length() < 1 || edYear.length() < 1 || edPrice.length() < 1)
                showToast("所有欄位請勿留空")
            else
                try {
                    // 新增一筆車輛紀錄於carTable資料表
                    dbrw.execSQL(
                        "INSERT INTO carTable(brand, year, price) VALUES(?,?,?)",
                        arrayOf(
                            edBrand.text.toString(),
                            edYear.text.toString().toInt(),
                            edPrice.text.toString().toInt()
                        )
                    )
                    showToast("新增: ${edBrand.text}, 年份: ${edYear.text}, 價格: ${edPrice.text}")
                    cleanEditText()
                } catch (e: Exception) {
                    showToast("新增失敗: $e")
                }
        }

        findViewById<Button>(R.id.btnUpdate).setOnClickListener {
            // 判斷是否有填入所有欄位
            if (edBrand.length() < 1 || edYear.length() < 1 || edPrice.length() < 1)
                showToast("所有欄位請勿留空")
            else
                try {
                    // 更新相同廠牌和年份的車輛價格
                    dbrw.execSQL(
                        "UPDATE carTable SET price = ? WHERE brand LIKE ? AND year = ?",
                        arrayOf(
                            edPrice.text.toString().toInt(),
                            edBrand.text.toString(),
                            edYear.text.toString().toInt()
                        )
                    )
                    showToast("更新: ${edBrand.text}, 年份: ${edYear.text}, 新價格: ${edPrice.text}")
                    cleanEditText()
                } catch (e: Exception) {
                    showToast("更新失敗: $e")
                }
        }

        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            // 判斷是否有填入廠牌和年份
            if (edBrand.length() < 1 || edYear.length() < 1)
                showToast("廠牌和年份請勿留空")
            else
                try {
                    // 從carTable資料表刪除相同廠牌和年份的車輛
                    dbrw.execSQL(
                        "DELETE FROM carTable WHERE brand LIKE ? AND year = ?",
                        arrayOf(edBrand.text.toString(), edYear.text.toString().toInt())
                    )
                    showToast("刪除: ${edBrand.text}, 年份: ${edYear.text}")
                    cleanEditText()
                } catch (e: Exception) {
                    showToast("刪除失敗: $e")
                }
        }

        findViewById<Button>(R.id.btnQuery).setOnClickListener {
            val queryString = if (edBrand.length() < 1)
                "SELECT * FROM carTable"
            else
                "SELECT * FROM carTable WHERE brand LIKE '%${edBrand.text}%'"

            val c = dbrw.rawQuery(queryString, null)
            c.moveToFirst()
            items.clear()
            showToast("共有 ${c.count} 筆車輛資料")
            for (i in 0 until c.count) {
                // 加入新資料
                items.add("廠牌: ${c.getString(0)} | 年份: ${c.getInt(1)} | 價格: ${c.getInt(2)}萬")
                c.moveToNext()
            }
            adapter.notifyDataSetChanged()
            c.close()
        }
    }

    // 建立showToast方法顯示Toast訊息
    private fun showToast(text: String) =
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    // 清空輸入的欄位
    private fun cleanEditText() {
        findViewById<EditText>(R.id.edBrand).setText("")
        findViewById<EditText>(R.id.edYear).setText("")
        findViewById<EditText>(R.id.edPrice).setText("")
    }
}
package com.example.lab15

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(
    context: Context,
    name: String = DB_NAME,
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = VERSION
) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        private const val DB_NAME = "carDatabase"
        private const val VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 建立車輛資料表，包含廠牌、年份、價格
        db.execSQL("CREATE TABLE carTable(brand text NOT NULL, year integer NOT NULL, price integer NOT NULL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS carTable")
        onCreate(db)
    }
}
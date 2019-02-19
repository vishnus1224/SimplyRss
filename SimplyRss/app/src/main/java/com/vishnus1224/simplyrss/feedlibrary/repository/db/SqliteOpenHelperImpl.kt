package com.vishnus1224.simplyrss.feedlibrary.repository.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DATABASE_NAME = "simplyRssSqliteDb"
private const val DATABASE_VERSION = 1

internal class SqliteOpenHelperImpl(
    context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        SqliteFeedDatabase.createTables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Update not supported as of now")
    }
}
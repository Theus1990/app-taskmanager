package com.example.taskmanager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tasks.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_DESCRIPTION TEXT, " +
                "$COLUMN_DATE TEXT)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addTask(task: Task): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, task.name)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_DATE, task.date)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        with(cursor) {
            while (moveToNext()) {
                val task = Task(
                    id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                    name = getString(getColumnIndexOrThrow(COLUMN_NAME)),
                    description = getString(getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    date = getString(getColumnIndexOrThrow(COLUMN_DATE))
                )
                tasks.add(task)
            }
        }
        cursor.close()
        return tasks
    }

    fun updateTask(task: Task) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, task.name)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_DATE, task.date)
        }
        db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(task.id.toString()))
    }

    fun deleteTask(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }
}

package com.example.taskmanager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class TaskDetailActivity : AppCompatActivity() {

    private lateinit var taskImageView: ImageView
    private var taskId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        taskImageView = findViewById(R.id.task_image)

        val taskName = intent.getStringExtra("task_name")
        val taskDescription = intent.getStringExtra("task_description")
        val taskDate = intent.getStringExtra("task_date")
        val taskImagePath = intent.getStringExtra("task_image_path")
        taskId = intent.getIntExtra("task_id", -1)


        findViewById<EditText>(R.id.edit_text_task_name).setText(taskName)
        findViewById<EditText>(R.id.edit_text_task_description).setText(taskDescription)
        findViewById<EditText>(R.id.edit_text_task_date).setText(taskDate)


        if (taskImagePath != null) {
            taskImageView.setImageURI(Uri.parse(taskImagePath))
        }


        findViewById<Button>(R.id.button_save).setOnClickListener {
            val updatedName = findViewById<EditText>(R.id.edit_text_task_name).text.toString()
            val updatedDescription = findViewById<EditText>(R.id.edit_text_task_description).text.toString()
            val updatedDate = findViewById<EditText>(R.id.edit_text_task_date).text.toString()

            val resultIntent = Intent()
            resultIntent.putExtra("updated_task_name", updatedName)
            resultIntent.putExtra("updated_task_description", updatedDescription)
            resultIntent.putExtra("updated_task_date", updatedDate)
            resultIntent.putExtra("task_id", taskId)

            setResult(RESULT_OK, resultIntent)
            finish()
        }


        findViewById<Button>(R.id.button_delete).setOnClickListener {
            val deleteIntent = Intent()
            deleteIntent.putExtra("task_id", taskId)
            setResult(RESULT_FIRST_USER, deleteIntent)
            finish()
        }
    }
}

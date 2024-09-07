package com.example.taskmanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast

class TaskListFragment : Fragment() {

    private lateinit var dbHelper: SQLiteHelper
    private lateinit var taskAdapter: ArrayAdapter<String>
    private var tasks = mutableListOf<Task>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)
        val listView = view.findViewById<ListView>(R.id.list_view_tasks)

        dbHelper = SQLiteHelper(requireContext())
        tasks = dbHelper.getAllTasks() as MutableList<Task>

        taskAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, tasks.map { formatTask(it) })
        listView.adapter = taskAdapter

        // Adicionar nova tarefa
        view.findViewById<View>(R.id.button_add_task).setOnClickListener {
            val intent = Intent(requireContext(), AddTaskActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_TASK)
        }

        // Editar ou deletar uma tarefa
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedTask = tasks[position]
            val intent = Intent(requireContext(), TaskDetailActivity::class.java).apply {
                putExtra("task_id", selectedTask.id)
                putExtra("task_name", selectedTask.name)
                putExtra("task_description", selectedTask.description)
                putExtra("task_date", selectedTask.date)
            }
            startActivityForResult(intent, REQUEST_CODE_EDIT_TASK)
        }

        return view
    }

    private fun formatTask(task: Task): String {
        return "${task.name} - ${task.description} - ${task.date}"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_TASK && resultCode == Activity.RESULT_OK) {
            val taskName = data?.getStringExtra("EXTRA_TASK_NAME")
            val taskDescription = data?.getStringExtra("EXTRA_TASK_DESCRIPTION")
            val taskDate = data?.getStringExtra("EXTRA_TASK_DATE")
            if (taskName != null && taskDescription != null && taskDate != null) {
                val newTask = Task(0, taskName, taskDescription, taskDate)  // ID é 0, será gerado pelo banco
                val taskId = dbHelper.addTask(newTask)
                newTask.id = taskId.toInt()  // Atribuir o ID gerado à tarefa
                tasks.add(newTask)
                updateTaskList()
            } else {
                Toast.makeText(requireContext(), "Task not added", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == REQUEST_CODE_EDIT_TASK && resultCode == Activity.RESULT_OK) {
            val updatedTaskName = data?.getStringExtra("updated_task_name")
            val updatedTaskDescription = data?.getStringExtra("updated_task_description")
            val updatedTaskDate = data?.getStringExtra("updated_task_date")
            val taskId = data?.getIntExtra("task_id", -1) ?: -1

            if (updatedTaskName != null && taskId != -1) {
                val updatedTask = tasks.find { it.id == taskId }
                updatedTask?.let {
                    it.name = updatedTaskName
                    it.description = updatedTaskDescription ?: ""
                    it.date = updatedTaskDate ?: ""
                    dbHelper.updateTask(it)
                    updateTaskList()
                }
            }
        } else if (requestCode == REQUEST_CODE_EDIT_TASK && resultCode == Activity.RESULT_FIRST_USER) {
            val taskId = data?.getIntExtra("task_id", -1) ?: -1
            if (taskId != -1) {
                dbHelper.deleteTask(taskId)
                tasks.removeIf { it.id == taskId }
                updateTaskList()
            }
        }
    }

    private fun updateTaskList() {
        taskAdapter.clear()
        taskAdapter.addAll(tasks.map { formatTask(it) })
        taskAdapter.notifyDataSetChanged()
    }

    companion object {
        const val REQUEST_CODE_ADD_TASK = 1
        const val REQUEST_CODE_EDIT_TASK = 2
    }
}

package com.example.lyalyapomidortodo.view.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.lyalyapomidortodo.R
import com.example.lyalyapomidortodo.data.local.entities.Task

class CategoryListAdapter(private var tasks: List<Task>) :
    RecyclerView.Adapter<CategoryListAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        println("TaskAdapter Отображаем задачу: ${task.title}")
        holder.bind(task)
    }

    override fun getItemCount(): Int = tasks.size

    // Метод для обновления списка задач
    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskCheckBox: CheckBox = itemView.findViewById(R.id.taskCheckBox)

        fun bind(task: Task) {
            taskCheckBox.text = task.title
            taskCheckBox.isChecked = task.completed
        }
    }
}

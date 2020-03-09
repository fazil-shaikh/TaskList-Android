package com.example.tasklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_task.view.*

class TaskAdapter(val taskList : ArrayList<Task>, var clickListener: OnTaskItemClickListener) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun getItemCount() = taskList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val rootView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        (holder).bind(taskList[position],position, clickListener)
    }

    // initializes the data in the item view
    class TaskViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun bind(task: Task, position: Int, clickListener: OnTaskItemClickListener) {
            itemView.taskTitle.text = task.tTitle
            itemView.taskDueDate.text = task.tDueDate
            itemView.taskStatus.isChecked = task.tStatus

            itemView.setOnClickListener { clickListener.onItemClick(task, position) }

            itemView.taskStatus.setOnClickListener { view ->
                task.tStatus = !task.tStatus
//                if(task.tStatus)
//                    itemView.cardView.setCardBackgroundColor(Color.GRAY)
//                else
//                    itemView.cardView.setCardBackgroundColor(Color.WHITE)
            }
        }
    }

    interface OnTaskItemClickListener{
        fun onItemClick(task: Task, position: Int)
    }
}
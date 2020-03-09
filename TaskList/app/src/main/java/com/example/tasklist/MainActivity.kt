package com.example.tasklist

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity(), TaskAdapter.OnTaskItemClickListener {

    // variables for different request codes
    companion object {
        val EDIT_TASK_REQUEST: Int = 0
        val ADD_TASK_REQUEST: Int = 1
    }

    private var taskList = ArrayList<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // sample tasks for display
        val task1 = Task(0,"Call mom", false, "01-10-2020", "task 1")
        val task2 = Task(1,"Go to the library", false, "02-11-2020", "task 2")
        val task3 = Task(2,"Do homework", false, "03-12-2020", "task 3")

        taskList.add(task1)
        taskList.add(task2)
        taskList.add(task3)

        // sort the data after adding the sample tasks
        sortData()

        // set the layout manager and adapter
        task_list.layoutManager = LinearLayoutManager(this)
        task_list.adapter = TaskAdapter(taskList, this)

        // open task detail activity when + is pressed and send the necessary data along
        fab.setOnClickListener { _ ->
            val newTaskIntent = Intent(this, TaskDetailActivity::class.java)
            newTaskIntent.putExtra("TASK_ID", taskList.size)
            newTaskIntent.putExtra("requestCode", ADD_TASK_REQUEST)
            startActivityForResult(newTaskIntent, ADD_TASK_REQUEST)
        }

        // handles the swipe left to delete action
        val itemTouchCallBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                viewHolder1: RecyclerView.ViewHolder): Boolean { return false }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int){
                val position = viewHolder.adapterPosition
                taskList.removeAt(position)
                task_list.adapter?.notifyItemRemoved(position)

                // if there are no tasks remaining show the "no task found" message
                if (taskList.size < 1)
                    tempMessage.visibility = View.VISIBLE
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchCallBack)
        itemTouchHelper.attachToRecyclerView(task_list)
    }


    // open task detail activity when the task is clicked and send the necessary data along
    override fun onItemClick(item: Task, position: Int) {
        val taskDetailIntent = Intent(this, TaskDetailActivity::class.java).apply {
            putExtra("TASK_ID", position)
            putExtra("TASK_TITLE", item.tTitle)
            putExtra("TASK_STATUS", item.tStatus)
            putExtra("TASK_DATE", item.tDueDate)
            putExtra("TASK_NOTES", item.tNotes)
            putExtra("requestCode", EDIT_TASK_REQUEST)
        }
        startActivityForResult(taskDetailIntent, EDIT_TASK_REQUEST)
    }

    // handles both adding a new task and editing an existing task requests
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            val taskId = data.getIntExtra("id", -1)
            val taskTitle = data.getStringExtra("title") ?: ""
            val taskStatus = data.getBooleanExtra("status", false)
            val taskDate = data.getStringExtra("dueDate") ?: ""
            val taskNotes = data.getStringExtra("notes") ?: ""

            val task = Task(taskId, taskTitle, taskStatus, taskDate, taskNotes)

            if (data.getIntExtra("requestCode", -1) == EDIT_TASK_REQUEST){
                taskList[taskId] = task
                sortData()
                task_list.adapter?.notifyDataSetChanged()
            } else{
                taskList.add(task)
                sortData()
                task_list.adapter?.notifyDataSetChanged()
                // if there is at least one task don't show the "no task found" message
                if (taskList.size > 0)
                    tempMessage.visibility = View.GONE
            }
//            if(taskStatus)
//                task_list[taskId].cardView.setCardBackgroundColor(Color.GRAY)
//            else
//                task_list[taskId].cardView.setCardBackgroundColor(Color.WHITE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    // function to order the tasklist by due date first then completion
    private fun sortData(){
        taskList.sortBy { SimpleDateFormat("MM-dd-yyyy").parse(it.tDueDate)}
        taskList.sortBy { it.tStatus }
    }
}

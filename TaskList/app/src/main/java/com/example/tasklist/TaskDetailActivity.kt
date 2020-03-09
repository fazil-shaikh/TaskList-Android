package com.example.tasklist

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_task_detail.*
import java.text.SimpleDateFormat
import java.util.*

class TaskDetailActivity : AppCompatActivity() {

    private var id: Int = -1
    private var requestCode: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        // gets the request code to distinguish create new task vs edit task
        requestCode = intent.getIntExtra("requestCode", -1)

        // retrieve the input values
        id = intent.getIntExtra("TASK_ID", -1)
        titleText.setText(intent.getStringExtra("TASK_TITLE"))
        status.isChecked = intent.getBooleanExtra("TASK_STATUS", false)
        notes.setText(intent.getStringExtra("TASK_NOTES"))

        // only fill in the header and date text when editing a task
        if (requestCode == MainActivity.EDIT_TASK_REQUEST) {
            header.text = titleText.text
            dueDate.text = intent.getStringExtra("TASK_DATE")

            // ensures the due date button is never blank
            if (dueDate.text.isEmpty()) {
                dueDate.text = "Pick Date"
            }
        }

        // opens up the date dialog box and updates the button text to the date selected
        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val sdf = SimpleDateFormat("MM-dd-yyyy", Locale.US)
            dueDate.text = sdf.format(cal.time)
        }

        // listener for the due date calender dialog box
        dueDate.setOnClickListener {
            DatePickerDialog(this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    // pressing save returns all the inputted values back to the main activity
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.save -> {
                // cannot save a task without a title
                if (validate()) {
                    val returnIntent = Intent().apply {
                        putExtra("id", id)
                        putExtra("title", titleText.text.toString())
                        putExtra("status", status.isChecked)
                        // if no due date is selected then don't send it
                        if (dueDate.text != "Pick Date")
                            putExtra("dueDate", dueDate.text.toString())
                        putExtra("notes", notes.text.toString())
                        putExtra("requestCode", requestCode)
                    }
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()

                        // shows confirmation messages for editing vs adding
                    if (requestCode == MainActivity.EDIT_TASK_REQUEST)
                        Toast.makeText(applicationContext, "Updated", Toast.LENGTH_LONG).show()
                    else
                        Toast.makeText(applicationContext, "Saved", Toast.LENGTH_LONG).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.task_detail_menu, menu)
        return true
    }

    // function to validate the task title
    private fun validate() : Boolean {
        if(titleText.text.toString().isEmpty()) {
            titleText.error = "Task title should not be blank"
            return false
        }
        return true
    }
}

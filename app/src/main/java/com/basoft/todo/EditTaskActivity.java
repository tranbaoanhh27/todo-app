package com.basoft.todo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import java.util.Calendar;

public class EditTaskActivity extends AppCompatActivity {

    private EditText titleEdit, deadlineEdit;
    private AppCompatButton saveButton;
    private ImageButton backButton;
    private TaskTodo task;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        findViews();
        getDataFromIntent();
        setViewsContent();
        setViewsListener();
    }

    @Override
    public void onBackPressed() {
        askForSaveAndExit();
    }

    private void setViewsListener() {
        deadlineEdit.setOnClickListener(v -> showDateTimePicker());
        saveButton.setOnClickListener(v -> saveAndFinish());
        backButton.setOnClickListener(v -> askForSaveAndExit());
    }

    private void saveAndFinish() {
        task.setTitle(titleEdit.getText().toString());
        DataManager dataManager = DataManager.getInstance();
        dataManager.updateTask(EditTaskActivity.this, position, task);
        finish();
    }

    private void askForSaveAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditTaskActivity.this);
        DialogInterface.OnClickListener positiveListener = (dialog, which) -> saveAndFinish();
        DialogInterface.OnClickListener negativeListener = (dialog, which) -> finish();

        builder.setTitle(R.string.save_changes)
                .setMessage(R.string.do_you_want_to_save_changes_before_leaving)
                .setPositiveButton(R.string.yes, positiveListener)
                .setNegativeButton(R.string.no, negativeListener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDateTimePicker() {
        Calendar currentDate = task.getDeadline();
        if (currentDate == null) {
            currentDate = Calendar.getInstance();
            currentDate.set(Calendar.HOUR_OF_DAY, 23);
            currentDate.set(Calendar.MINUTE, 59);
        }
        Calendar finalCurrentDate = (Calendar) currentDate.clone();
        Calendar date = (Calendar) currentDate.clone();
        new DatePickerDialog(EditTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.set(year, month, dayOfMonth);
                task.setDeadline(date);
                new TimePickerDialog(EditTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        task.setDeadline(date);
                        deadlineEdit.setText(task.getDeadlineString(EditTaskActivity.this));
                    }
                } , finalCurrentDate.get(Calendar.HOUR_OF_DAY), finalCurrentDate.get(Calendar.MINUTE), true).show();
            }
        } , currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        if (position != -1) {
            DataManager dataManager = DataManager.getInstance();
            task = dataManager.getTask(position).clone();
        }
    }

    private void setViewsContent() {
        titleEdit.setText(task.getTitle());
        deadlineEdit.setText(task.getDeadlineString(EditTaskActivity.this));
    }

    private void findViews() {
        titleEdit = findViewById(R.id.edit_task_title);
        deadlineEdit = findViewById(R.id.edit_task_deadline_datetime);
        saveButton = findViewById(R.id.edit_task_save_button);
        backButton = findViewById(R.id.edit_task_back_button);
    }
}
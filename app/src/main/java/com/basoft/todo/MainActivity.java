package com.basoft.todo;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private static final Integer DEFAULT_DEADLINE_HOUR = 23, DEFAULT_DEADLINE_MINUTE = 59;
    static Calendar calendar = Calendar.getInstance();
    private static final int DEFAULT_DEADLINE_YEAR = calendar.get(Calendar.YEAR);
    private static final int DEFAULT_DEADLINE_MONTH = calendar.get(Calendar.MONTH);
    private static final int DEFAULT_DEADLINE_DAY = calendar.get(Calendar.DAY_OF_MONTH);
    private EditText newTaskEditText;
    private Button newTaskSubmitButton, newTaskDeadlineDateButton, newTaskDeadlineTimeButton;
    private RecyclerView tasksRecyclerView;
    private Integer deadlineYear, deadlineMonth, deadlineDay, deadlineHour, deadlineMinute;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        newTaskDeadlineDateButton.setOnClickListener(deadlineDateSelectListener);
        newTaskDeadlineTimeButton.setOnClickListener(deadlineTimeSelectListener);
        newTaskSubmitButton.setOnClickListener(newTaskSubmitListener);

        setupTasksRecyclerView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataManager dataManager = DataManager.getInstance();
        dataManager.saveTasksToLocalStorage(MainActivity.this);
    }

    private void setupTasksRecyclerView() {
        DataManager dataManager = DataManager.getInstance();
        dataManager.loadTasksFromLocalStorage(MainActivity.this);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        taskAdapter = new TaskAdapter(dataManager.getTasks(), MainActivity.this);
        tasksRecyclerView.setAdapter(taskAdapter);
    }

    private void findViews() {
        newTaskEditText = findViewById(R.id.new_task_edittext);
        newTaskSubmitButton = findViewById(R.id.new_task_button);
        newTaskDeadlineDateButton = findViewById(R.id.new_task_deadline_date);
        newTaskDeadlineTimeButton = findViewById(R.id.new_task_deadline_time);
        tasksRecyclerView = findViewById(R.id.tasks_recycler_view);
    }

    private final View.OnClickListener newTaskSubmitListener = new View.OnClickListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onClick(View v) {
            String newTaskTitle = newTaskEditText.getText().toString().trim();

            if (newTaskTitle.length() == 0) {
                resetViews();
                return;
            }

            TaskTodo newTask = new TaskTodo(newTaskTitle);

            if (deadlineYear != null && deadlineHour != null)
                newTask = new TaskTodo(newTaskTitle, LocalDateTime.of(
                        deadlineYear, deadlineMonth + 1, deadlineDay,
                        deadlineHour, deadlineMinute
                ));

            DataManager dataManager = DataManager.getInstance();
            dataManager.addTask(newTask);
            taskAdapter.notifyDataSetChanged();
            resetViews();
        }
    };

    private void resetViews() {
        newTaskEditText.setText("");
        deadlineYear = deadlineMonth = deadlineDay = deadlineHour = deadlineMinute = null;
        newTaskDeadlineTimeButton.setText(R.string.select_time);
        newTaskDeadlineDateButton.setText(R.string.select_date);
    }

    private final View.OnClickListener deadlineDateSelectListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            OnDateSetListener onDateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
                setDeadlineDate(year, monthOfYear, dayOfMonth);
                if (deadlineHour == null || deadlineMinute == null)
                    setDeadlineTime(DEFAULT_DEADLINE_HOUR, DEFAULT_DEADLINE_MINUTE);
            };

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.this, onDateSetListener,
                    DEFAULT_DEADLINE_YEAR, DEFAULT_DEADLINE_MONTH, DEFAULT_DEADLINE_DAY
            );

            datePickerDialog.show();
        }
    };

    @SuppressLint("DefaultLocale")
    private void setDeadlineDate(int year, int monthOfYear, int dayOfMonth) {
        this.deadlineYear = year;
        this.deadlineMonth = monthOfYear;
        this.deadlineDay = dayOfMonth;
        newTaskDeadlineDateButton.setText(
                String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year)
        );
    }

    private final View.OnClickListener deadlineTimeSelectListener = v -> {

        OnTimeSetListener onTimeSetListener = (view, hourOfDay, minute) -> {
            setDeadlineTime(hourOfDay, minute);
            if (deadlineYear == null || deadlineMonth == null || deadlineDay == null)
                setDeadlineDate(DEFAULT_DEADLINE_YEAR, DEFAULT_DEADLINE_MONTH, DEFAULT_DEADLINE_DAY);
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                MainActivity.this, onTimeSetListener,
                DEFAULT_DEADLINE_HOUR, DEFAULT_DEADLINE_MINUTE, true
        );

        timePickerDialog.show();
    };

    @SuppressLint("DefaultLocale")
    private void setDeadlineTime(int hourOfDay, int minute) {
        this.deadlineHour = hourOfDay;
        this.deadlineMinute = minute;
        newTaskDeadlineTimeButton.setText(String.format("%02d:%02d", hourOfDay, minute));
    }
}
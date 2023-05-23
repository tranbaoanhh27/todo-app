package com.basoft.todo;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Notification;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements DataChangeObserver {

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
    private TextView greetingTextView;
    private BootCompleteReceiver bootCompleteReceiver = new BootCompleteReceiver();
    public static final String SHARED_PREFERENCES_NAME = "ba_soft_todo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadDataFromSavedPreferences();

        findViews();

        newTaskDeadlineDateButton.setOnClickListener(deadlineDateSelectListener);
        newTaskDeadlineTimeButton.setOnClickListener(deadlineTimeSelectListener);
        newTaskSubmitButton.setOnClickListener(newTaskSubmitListener);

        setupGreeting();
        setupTasksRecyclerView();
    }

    private void setupGreeting() {
        Calendar today = Calendar.getInstance();
        Locale locale = Locale.getDefault();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy", locale);
        String formattedDate = formatter.format(today.getTime());
        greetingTextView.setText(String.format(
                "%s %s", getString(R.string.today_is), formattedDate
        ));
    }

    private void loadDataFromSavedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        NotificationHelper.notificationId = sharedPreferences.getInt(NotificationReceiver.NOTIFICATION_ID, 0) + 1;
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        intentFilter.addAction(Intent.ACTION_LOCKED_BOOT_COMPLETED);
        this.registerReceiver(bootCompleteReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyDatasetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(bootCompleteReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataManager dataManager = DataManager.getInstance();
        dataManager.saveTasksToLocalStorage(MainActivity.this);
        saveDataToSharedPreferences();
        WidgetUpdateService.startActionUpdateTasksListView(MainActivity.this);
    }

    private void saveDataToSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(NotificationReceiver.NOTIFICATION_ID, NotificationHelper.notificationId);
        editor.apply();
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
        greetingTextView = findViewById(R.id.greeting);
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
            Calendar deadline = Calendar.getInstance();

            boolean haveDeadline = deadlineYear != null && deadlineHour != null;

            if (haveDeadline) {
                deadline.set(Calendar.YEAR, deadlineYear);
                deadline.set(Calendar.MONTH, deadlineMonth);
                deadline.set(Calendar.DAY_OF_MONTH, deadlineDay);
                deadline.set(Calendar.HOUR_OF_DAY, deadlineHour);
                deadline.set(Calendar.MINUTE, deadlineMinute);
                deadline.set(Calendar.SECOND, 0);
                newTask = new TaskTodo(newTaskTitle, deadline);
                newTask.scheduleNotification(MainActivity.this);
            }

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

    public void notifyDatasetChanged() {
        tasksRecyclerView.post(new Runnable() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                taskAdapter.notifyDataSetChanged();
            }
        });
    }

    public void notifyItemMoved(int fromPosition, int toPosition) {
        tasksRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                taskAdapter.notifyItemMoved(fromPosition, toPosition);
            }
        });
    }

    @Override
    public void onTaskUpdated(int position) {
        tasksRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                taskAdapter.notifyItemChanged(position);
            }
        });
    }
}
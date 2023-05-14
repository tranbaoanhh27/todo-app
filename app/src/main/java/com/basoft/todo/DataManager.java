package com.basoft.todo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class DataManager {
    private static final String TASKS_FILENAME = "tasks.dat";
    private static ArrayList<TaskTodo> tasks;
    private static DataManager singleInstance;

    public static DataManager getInstance() {
        if (singleInstance == null)
            singleInstance = new DataManager();
        return singleInstance;
    }

    private DataManager() {
        // Do nothing
    }

    public ArrayList<TaskTodo> getTasks() {
        return tasks;
    }

    public void loadTasksFromLocalStorage(Context context) {
        try {
            FileInputStream fileInputStream = context.openFileInput(TASKS_FILENAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            tasks = (ArrayList<TaskTodo>) objectInputStream.readObject();
            Log.d("Loaded Tasks from disk", tasks.toString());
        } catch (IOException | ClassNotFoundException e) {
            Log.d("Load task error:", e.toString());
            tasks = new ArrayList<>();
        }
    }

    public void saveTasksToLocalStorage(Context context) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(TASKS_FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(tasks);
            objectOutputStream.close();
        } catch (IOException e) {
            Log.d("Save tasks error:", e.toString());
        }
    }

    public void addTask(TaskTodo newTask) {
        tasks.add(newTask);
        tasks.sort(new TaskTodo.TaskTodoComparator());
    }

    public void setTaskDone(int position, boolean isDone) {
        if (position >= tasks.size()) return;
        tasks.get(position).setDone(isDone);
        tasks.sort(new TaskTodo.TaskTodoComparator());
    }

    public void deleteTask(int position) {
        if (position >= tasks.size()) return;
        tasks.remove(position);
    }
}

package com.basoft.todo;

import android.content.Context;
import android.util.Log;

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
        moveTaskToCorrectPosition(tasks.size() - 1);
    }

    public int moveTaskToCorrectPosition(int position) {
        if (position >= tasks.size()) return -1;
        TaskTodo task = tasks.remove(position);
        int correctPosition = 0;
        if (task.isDone()) {
            int index = 0;
            while (index < tasks.size() && !tasks.get(index).isDone()) index++;
            // Now tasks[index] is done or index == n (which means there is no done task in tasks)
            while (index < tasks.size()) {
                TaskTodo.TaskTodoComparator comparator = new TaskTodo.TaskTodoComparator();
                if (comparator.compare(task, tasks.get(index)) <= 0) break;
                index++;
            }
            correctPosition = index;
        } else {
            int end = 0;
            while (end < tasks.size() && !tasks.get(end).isDone()) end++;
            int index = 0;
            while (index < end) {
                TaskTodo.TaskTodoComparator comparator = new TaskTodo.TaskTodoComparator();
                if (comparator.compare(task, tasks.get(index)) <= 0) break;
                index++;
            }
            correctPosition = index;
        }
        tasks.add(correctPosition, task);
        return correctPosition;
    }

    public int setTaskDone(int position, boolean isDone) {
        if (position >= tasks.size()) return -1;
        tasks.get(position).setDone(isDone);
        return moveTaskToCorrectPosition(position);
    }

    public void deleteTask(int position) {
        if (position >= tasks.size()) return;
        tasks.remove(position);
    }

    public TaskTodo getTask(int position) {
        if (position >= tasks.size()) return null;
        return tasks.get(position);
    }
}

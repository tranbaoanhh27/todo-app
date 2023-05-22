package com.basoft.todo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

public class TasksListViewWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        DataManager dataManager = DataManager.getInstance();
        return new TasksWidgetListViewFactory(dataManager.getTasks(), getApplicationContext());
    }
}

class TasksWidgetListViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<TaskTodo> tasks;
    private Context context;

    public TasksWidgetListViewFactory(ArrayList<TaskTodo> tasks, Context context) {
        Log.d("tranbaoanh", "TasksWidgetListViewFactory");
        Log.d("tranbaoanh", tasks.toString());
        Log.d("tranbaoanh", context.toString());
        this.tasks = tasks;
        this.context = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Log.d("tranbaoanh", "TasksWidgetListViewFactory::onDataSetChanged");
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tasks_widget_item_view);
        TaskTodo task = tasks.get(position);
        views.setTextViewText(R.id.widget_item_title, task.getTitle());
        views.setTextViewText(R.id.widget_item_deadline, task.getDeadlineString(context));
        int checkboxImageResource = task.isDone() ? R.drawable.checked : R.drawable.unchecked;
        views.setImageViewResource(R.id.widget_item_checkbox, checkboxImageResource);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}

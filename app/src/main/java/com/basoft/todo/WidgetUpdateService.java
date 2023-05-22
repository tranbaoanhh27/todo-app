package com.basoft.todo;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

public class WidgetUpdateService extends IntentService {
    private static final String ACTION_UPDATE_LIST_VIEW = "com.basoft.todo.update_list_view";

    public WidgetUpdateService() {
        super("WidgetUpdateService");
    }

    public WidgetUpdateService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("tranbaoanh", "WidgetUpdateService::handleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            Log.d("tranbaoanh", "WidgetUpdateService::handleIntent action = " + action);
            if (ACTION_UPDATE_LIST_VIEW.equals(action)){
                handleActionUpdateListView();
            }
        }
    }

    private void handleActionUpdateListView() {
        Log.d("tranbaoanh", "handleActionUpdateListView");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, TasksWidget.class));
        TasksWidget.updateAllAppWidgets(this, appWidgetManager, appWidgetIds);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_tasks_list_view);
    }

    public static void startActionUpdateTasksListView(Context context) {
        Log.d("tranbaoanh", "startActionUpdateTasksListView");
        Intent intent = new Intent(context, WidgetUpdateService.class);
        intent.setAction(ACTION_UPDATE_LIST_VIEW);
        context.startService(intent);
    }
}

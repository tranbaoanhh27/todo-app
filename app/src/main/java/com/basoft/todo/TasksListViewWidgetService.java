package com.basoft.todo;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class TasksListViewWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return null;
    }
}

package com.basoft.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class WidgetListViewTaskAdapter extends ArrayAdapter<TaskTodo> {

    private ArrayList<TaskTodo> tasks;
    private Context context;

    public WidgetListViewTaskAdapter(@NonNull Context context, int resource, ArrayList<TaskTodo> tasks) {
        super(context, resource, tasks);
        this.tasks = tasks;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TaskTodo task = tasks.get(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.tasks_widget_item_view, parent, false);
            viewHolder.title = convertView.findViewById(R.id.widget_item_title);
            viewHolder.deadline = convertView.findViewById(R.id.widget_item_deadline);
            viewHolder.checkBox = convertView.findViewById(R.id.widget_item_checkbox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(task.getTitle());
        viewHolder.deadline.setText(task.getDeadlineString(context));

        return convertView;
    }

    private static class ViewHolder {
        private TextView title, deadline;
        private CheckBox checkBox;
    }
}

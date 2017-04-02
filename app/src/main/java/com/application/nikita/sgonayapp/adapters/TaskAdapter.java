package com.application.nikita.sgonayapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.application.nikita.sgonayapp.R;
import com.application.nikita.sgonayapp.entities.Task;

import java.util.ArrayList;

/**
 * Created by Konstantin on 02.04.2017.
 */

public class TaskAdapter extends BaseAdapter {

    Context context;
    ArrayList<Task> tasks;
    LayoutInflater inflater;

    TaskAdapter(Context context, ArrayList<Task> tasks){
        this.context = context;
        this.tasks = tasks;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view  = convertView;
        if (view == null) {
            view = this.inflater.inflate(R.layout.task_item, parent, false);
        }

        Task t = getTask(position);

        ((TextView) view.findViewById(R.id.gameName)).setText(t.getId());
        ((TextView) view.findViewById(R.id.gameDate)).setText(t.getTitle());

        return view;
    }

    Task getTask(int position) {
        return ((Task) getItem(position));
    }
}

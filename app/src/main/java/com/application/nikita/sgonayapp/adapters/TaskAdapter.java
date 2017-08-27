package com.application.nikita.sgonayapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {

    public interface OnItemClickListener {
        void onItemClick(Task task);
    }

    private Context mContext;
    private ArrayList<Task> mTasks;
    private OnItemClickListener mListener;

    public TaskAdapter(Context context, ArrayList<Task> tasks, OnItemClickListener listener){
        mContext = context;
        mTasks = tasks;
        mListener = listener;
    }

    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.task_item, parent, false);
        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskHolder holder, int position) {
        Task task = mTasks.get(position);
        holder.bindTask(task, mListener);
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    class TaskHolder extends RecyclerView.ViewHolder {

        private TextView mTaskIdTextView;
        private TextView mTaskDescriptionTextView;

        private Task mTask;

        public void bindTask(final Task task, final OnItemClickListener listener) {
            mTask = task;
            mTaskIdTextView.setText(String.valueOf(mTask.getId()));
            mTaskDescriptionTextView.setText(mTask.getDescription());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(task);
                }
            });
        }

        public TaskHolder(View itemView) {
            super(itemView);

            mTaskIdTextView = (TextView) itemView.findViewById(R.id.task_id_text_view);
            mTaskDescriptionTextView = (TextView) itemView.findViewById(R.id.task_description_text_view);
        }
    }
}

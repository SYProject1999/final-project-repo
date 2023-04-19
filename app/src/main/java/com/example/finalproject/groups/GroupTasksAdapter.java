package com.example.finalproject.groups;

import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.models.References;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class GroupTasksAdapter extends RecyclerView.Adapter<GroupTasksAdapter.MyHolder> {

    List<GroupTask> groupTasks;
    Group currentGroup;

    public GroupTasksAdapter(Group currentGroup, List<GroupTask> groupTasks) {
        this.groupTasks = groupTasks;
        this.currentGroup = currentGroup;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_task, null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        GroupTask groupTask = groupTasks.get(position);

        holder.tvTaskTitle.setText(groupTask.getTaskTitle());
        holder.tvTaskDueDate.setText(groupTask.getTaskDueDate());
        holder.tvTaskAssignedTo.setText(groupTask.getAssignedToName());

        if (groupTask.isCompleted()) {
            // need to change icon for the completed
            holder.rlRow.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.color_input_2));
            holder.ivIsCompleted.setImageResource(R.drawable.ic_completed);
            holder.tvTaskTitle.setPaintFlags(holder.tvTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.rlRow.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.color_input));
            holder.ivIsCompleted.setImageResource(R.drawable.ic_not_completed);
            holder.tvTaskTitle.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
        }


        holder.ivIsCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentGroup.getCreatedBy().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) || groupTask.getAssignedToId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    if (groupTask.isCompleted()) {
                        groupTask.setCompleted(false);
                        updDateIsTaskSuccessful(groupTask);
                    } else {
                        groupTask.setCompleted(true);
                        updDateIsTaskSuccessful(groupTask);
                    }
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(holder.itemView.getContext(), "You're not allowed to perform this operation", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(),GroupTaskDetailActivity.class);
                intent.putExtra("group", currentGroup);
                intent.putExtra("groupTask", groupTask);
                holder.itemView.getContext().startActivity(intent);
            }
        });


    }

    private void updDateIsTaskSuccessful(GroupTask groupTask) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(References.GROUP_TASKS).child(currentGroup.getId());
        reference.child(groupTask.getId()).setValue(groupTask);
    }

    @Override
    public int getItemCount() {
        return groupTasks.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {


        ImageView ivIsCompleted;
        TextView tvTaskTitle;
        TextView tvTaskDueDate;
        TextView tvTaskAssignedTo;
        RelativeLayout rlRow;

        MyHolder(View itemView) {
            super(itemView);
            ivIsCompleted = itemView.findViewById(R.id.ivIsCompleted);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskDueDate = itemView.findViewById(R.id.tvTaskDueDate);
            tvTaskAssignedTo = itemView.findViewById(R.id.tvAssignedTo);
            rlRow = itemView.findViewById(R.id.rlRow);
        }

    }
}

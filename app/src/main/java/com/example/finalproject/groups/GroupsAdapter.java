package com.example.finalproject.groups;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.GlideApp;
import com.example.finalproject.R;

import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.MyHolder> {
    List<Group> groupsList;

    public GroupsAdapter(List<Group> groupsList) {
        this.groupsList = groupsList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_display_layout, parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Group group = groupsList.get(position);
        holder.tvName.setText(group.getName());
        holder.tvMembers.setText(group.getMembers().size() + " Members");
        GlideApp.with(holder.itemView.getContext()).load(group.getPicUrl()).placeholder(R.drawable.ic_baseline_person_24).into(holder.ivProfile);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(holder.itemView.getContext(), GroupChatActivity.class);
            intent.putExtra("group", group);
            holder.itemView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return groupsList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView ivProfile;
        TextView tvName;
        TextView tvMembers;

        MyHolder(View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvName = itemView.findViewById(R.id.tvGroupName);
            tvMembers = itemView.findViewById(R.id.tvMembers);
        }

    }
}

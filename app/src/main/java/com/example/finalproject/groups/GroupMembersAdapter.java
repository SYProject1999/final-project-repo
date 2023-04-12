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
import com.example.finalproject.models.User;

import java.util.List;

public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.MyHolder> {
    List<User> userList;

    public GroupMembersAdapter(List<User> memberList) {
        this.userList = memberList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_member_layout, parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        User user = userList.get(position);
        holder.tvName.setText(user.getFullName());
        if (user.getStatus()!=null) {
            holder.tvStatus.setText(user.getStatus());
        } else {
            holder.tvStatus.setText("No Status");
        }
        GlideApp.with(holder.itemView.getContext()).load(user.getImageUrl()).placeholder(R.drawable.ic_baseline_person_24).into(holder.ivProfile);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addMember(User user) {
        userList.add(user);
        notifyDataSetChanged();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView ivProfile;
        TextView tvName;
        TextView tvStatus;

        MyHolder(View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

    }
}

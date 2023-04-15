package com.example.finalproject.groups;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.GlideApp;
import com.example.finalproject.R;
import com.example.finalproject.models.User;

import java.util.List;

public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.MyHolder> {

    public static final int NONE_MODE = 0;
    public static final int ADD_MODE = 1;
    public static final int REMOVE_MODE = 2;

    private String createdBy;

    interface Listener {
        void onRemoveClickListener(User user);
        void onAddClickListener(User user, int position);
    }
    List<User> userList;
    int mode;
    Listener listener;

    public GroupMembersAdapter(List<User> memberList, String createdBy, int mode, Listener listener) {
        this.userList = memberList;
        this.mode = mode;
        this.listener = listener;
        this.createdBy = createdBy;
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
        if (mode == ADD_MODE) {
            holder.btnAdd.setVisibility(View.VISIBLE);
            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAddClickListener(user, holder.getAbsoluteAdapterPosition());
                }
            });
        } else if (mode == REMOVE_MODE) {
            if (user.getId().equals(createdBy)) {
                holder.ivRemove.setVisibility(View.GONE);
            } else {
                holder.ivRemove.setVisibility(View.VISIBLE);
            }
            holder.ivRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(holder.itemView.getContext())
                            .setMessage("Are you sure, you want to remove " + user.getFullName() +" from this group?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    userList.remove(holder.getAbsoluteAdapterPosition());
                                    notifyDataSetChanged();
                                    listener.onRemoveClickListener(user);
                                }})
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            });
            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addMember(User user) {
        userList.add(user);
        notifyDataSetChanged();
    }

    public void removeMember(int position) {
        userList.remove(position);
        notifyDataSetChanged();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView ivProfile;
        TextView tvName;
        TextView tvStatus;
        ImageView ivRemove;
        Button btnAdd;

        MyHolder(View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivRemove = itemView.findViewById(R.id.ivRemove);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }

    }
}

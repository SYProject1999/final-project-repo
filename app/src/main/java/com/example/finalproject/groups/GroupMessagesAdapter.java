package com.example.finalproject.groups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;

import java.util.List;


public class GroupMessagesAdapter extends RecyclerView.Adapter<GroupMessagesAdapter.ViewHolder> {
    List<Messages> messagesList;
    Context context;

    public GroupMessagesAdapter(List<Messages> messagesList, Context context) {
        this.messagesList = messagesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_group_message, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(messagesList.get(position).getName());
        holder.tvFileMessage.setText(messagesList.get(position).getFileMessage());
        if (messagesList.get(position).getType().equals("pdf")) {
            holder.type.setText(messagesList.get(position).getType());
            holder.imageView.setImageResource(R.drawable.ic_pdf);
        }
        if (messagesList.get(position).getType().equals("docx")) {
            holder.type.setText(messagesList.get(position).getType());
            holder.imageView.setImageResource(R.drawable.ic_word);
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name, type,tvFileMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.tvTypeName);
            type = itemView.findViewById(R.id.tvTypeDoc);
            tvFileMessage = itemView.findViewById(R.id.tvFileMessage);

        }
    }
}
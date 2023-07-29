package com.example.finalproject.groups;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.FullImageActivity;
import com.example.finalproject.GlideApp;
import com.example.finalproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<Messages> userMessagesList;
    private FirebaseAuth auth;

    public MessageAdapter(List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView senderMessageText, receiverMessageText, tvFileMessage, tvFileRecMessage;
        public CircleImageView receiverProfileImage;
        public ImageView messageSenderPicture, messageReceiverPicture,image_view_receiver,sender_image_view;
        public ProgressBar messageReceiverProgressBar, messageSenderProgressBar;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            tvFileRecMessage = itemView.findViewById(R.id.tvFileRecMessage);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = itemView.findViewById(R.id.message_profile_image);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageReceiverProgressBar = itemView.findViewById(R.id.receiver_message_progressBar);
            image_view_receiver = itemView.findViewById(R.id.image_view_receiver);
            tvFileMessage = itemView.findViewById(R.id.tvFileMessage);
            sender_image_view = itemView.findViewById(R.id.sender_image_view);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layout, parent, false);
        auth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        Context context = holder.itemView.getContext();
        String messageSenderId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        Messages messages = userMessagesList.get(position);
        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users").child(fromUserID);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String receiverImage = "";

                if (snapshot.hasChild("imageUrl")) {
                    receiverImage = snapshot.child("imageUrl").getValue(String.class);
                }

                if (receiverImage != null) {
                    if (receiverImage.isEmpty() == false) {
                        GlideApp.with(holder.itemView.getContext().getApplicationContext()).load(receiverImage).placeholder(R.drawable.ic_baseline_person_24).into(holder.receiverProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.receiverMessageText.setVisibility(View.GONE);
        holder.receiverProfileImage.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.messageSenderPicture.setVisibility(View.GONE);
        holder.messageReceiverPicture.setVisibility(View.GONE);
        holder.tvFileMessage.setVisibility(View.GONE);
        holder.tvFileRecMessage.setVisibility(View.GONE);
        holder.image_view_receiver.setVisibility(View.GONE);
        holder.sender_image_view.setVisibility(View.GONE);
        String fileMessage = userMessagesList.get(position).getFileMessage();
        if (fileMessage == null) {
            holder.tvFileMessage.setVisibility(View.GONE);
        }
        if (fromMessageType.equals("text")) {
            if (fromUserID.equals(messageSenderId)) {
                holder.image_view_receiver.setVisibility(View.GONE);
                holder.tvFileRecMessage.setVisibility(View.GONE);
                holder.messageSenderPicture.setVisibility(View.GONE);
                holder.messageReceiverPicture.setVisibility(View.GONE);
                holder.tvFileMessage.setVisibility(View.GONE);
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderMessageText.setTextColor(Color.WHITE);
                holder.senderMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
            } else {
                holder.image_view_receiver.setVisibility(View.GONE);
                holder.tvFileRecMessage.setVisibility(View.GONE);
                holder.messageSenderPicture.setVisibility(View.GONE);
                holder.messageReceiverPicture.setVisibility(View.GONE);
                holder.tvFileMessage.setVisibility(View.GONE);
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                holder.receiverMessageText.setTextColor(Color.BLACK);
                holder.receiverMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
            }
        } else if (fromMessageType.equals("pdf")) {
            if (fromUserID.equals(messageSenderId)) {
                holder.image_view_receiver.setVisibility(View.GONE);
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                holder.messageSenderPicture.setBackgroundResource(R.drawable.ic_pdf);
                holder.tvFileMessage.setVisibility(View.VISIBLE);
                holder.tvFileRecMessage.setVisibility(View.GONE);
                holder.tvFileMessage.setText(userMessagesList.get(position).getFileMessage());

            } else {
                holder.image_view_receiver.setVisibility(View.GONE);
                holder.tvFileMessage.setVisibility(View.GONE);
                holder.tvFileRecMessage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                holder.tvFileRecMessage.setText(userMessagesList.get(position).getFileMessage());
                holder.messageReceiverPicture.setBackgroundResource(R.drawable.ic_pdf);

            }
        } else if (fromMessageType.equals("docx")) {
            if (fromUserID.equals(messageSenderId)) {
                holder.image_view_receiver.setVisibility(View.GONE);
                holder.image_view_receiver.setVisibility(View.GONE);
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                holder.messageSenderPicture.setBackgroundResource(R.drawable.ic_word);
                holder.tvFileMessage.setVisibility(View.VISIBLE);
                holder.tvFileRecMessage.setVisibility(View.GONE);
                holder.tvFileMessage.setText(userMessagesList.get(position).getFileMessage());

            } else {

                holder.image_view_receiver.setVisibility(View.GONE);
                holder.tvFileMessage.setVisibility(View.GONE);
                holder.tvFileRecMessage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                holder.tvFileRecMessage.setText(userMessagesList.get(position).getFileMessage());
                holder.messageReceiverPicture.setBackgroundResource(R.drawable.ic_word);

            }
        }else if(fromMessageType.equals("Image")){
            if(fromUserID.equals(messageSenderId)){
                holder.sender_image_view.setVisibility(View.VISIBLE);
                holder.tvFileMessage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.GONE);
                holder.messageSenderPicture.setVisibility(View.GONE);
                holder.tvFileRecMessage.setVisibility(View.GONE);
                holder.image_view_receiver.setVisibility(View.GONE);
                holder.tvFileMessage.setText(userMessagesList.get(position).getFileMessage());

                GlideApp.with(holder.itemView.getContext().getApplicationContext()).load(messages.getMessage()).into(holder.sender_image_view);
                holder.messageSenderPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFullImage(context, messages.getMessage());
                    }
                });
            }else{
                holder.sender_image_view.setVisibility(View.GONE);
                holder.tvFileMessage.setVisibility(View.GONE);
                holder.tvFileRecMessage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.GONE);
                holder.messageSenderPicture.setVisibility(View.GONE);
                holder.image_view_receiver.setVisibility(View.VISIBLE);
                GlideApp.with(holder.itemView.getContext().getApplicationContext()).load(messages.getMessage()).into(holder.image_view_receiver);
                holder.tvFileRecMessage.setText(userMessagesList.get(position).getFileMessage());

                holder.messageSenderPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFullImage(context, messages.getMessage());
                    }
                });
            }
        }


        if (fileMessage == null) {
            holder.tvFileMessage.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
            holder.itemView.getContext().startActivity(intent);
        });

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
            holder.itemView.getContext().startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    private void openFullImage(Context context, String url) {
        Intent intent = new Intent(context, FullImageActivity.class);
        intent.putExtra("pic", url);
        context.startActivity(intent);
    }
}
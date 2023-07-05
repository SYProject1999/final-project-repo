package com.example.finalproject.groups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.finalproject.GlideApp;
import com.example.finalproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupsProfileActivity extends AppCompatActivity {

    private String receiverUserID, senderUserID , current_state;

    private CircleImageView userProfileImage;
    private TextView userProfileName, userProfileStatus;
    private Button sendMessageRequestButton, declineMessageRequestButton;

    private DatabaseReference UserRef, ChatRequestRef, ContactsRef, NotificationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_profile);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference("Users");
        ChatRequestRef = FirebaseDatabase.getInstance().getReference("Chat Requests");
        ContactsRef = FirebaseDatabase.getInstance().getReference("Contacts");
        NotificationRef = FirebaseDatabase.getInstance().getReference("Notifications");

        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();
        senderUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        userProfileImage = findViewById(R.id.visit_profile_image);
        userProfileName = findViewById(R.id.visit_user_name);
        userProfileStatus = findViewById(R.id.visit_profile_status);
        sendMessageRequestButton = findViewById(R.id.send_message_request_button);
        declineMessageRequestButton = findViewById(R.id.decline_message_request_button);
        current_state = "new";

        RetrieveUserInfo();
    }

    private void RetrieveUserInfo() {

        UserRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    String ImageUrl = "";
                    String userStatus = "no Status";
                    String userFullName = Objects.requireNonNull(snapshot.child("fullName").getValue()).toString();
                    if (snapshot.hasChild("imageUrl")) {
                        ImageUrl = Objects.requireNonNull(snapshot.child("imageUrl").getValue()).toString();
                    }
                    if (snapshot.hasChild("Status")) {
                        userStatus = Objects.requireNonNull(snapshot.child("Status").getValue()).toString();
                    }
                    GlideApp.with(getApplicationContext()).load(ImageUrl).placeholder(R.drawable.ic_baseline_person_24).into(userProfileImage);
                    userProfileName.setText(userFullName);
                    userProfileStatus.setText(userStatus);

                    ManageChatRequests();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void ManageChatRequests() {

        ChatRequestRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(receiverUserID)) {

                    String request_type = Objects.requireNonNull(snapshot.child(receiverUserID).child("request_type").getValue()).toString();
                    if (request_type.equals("sent")) {
                        current_state = "request_sent";
                        sendMessageRequestButton.setText("Cancel Chat Request");
                    } else if (request_type.equals("received")) {
                        current_state = "request_received";
                        sendMessageRequestButton.setText("Accept Chat Request");

                        declineMessageRequestButton.setVisibility(View.VISIBLE);
                        declineMessageRequestButton.setEnabled(true);
                        declineMessageRequestButton.setOnClickListener(view -> CancelChatRequest());
                    }
                } else {
                    ContactsRef.child(senderUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(receiverUserID)) {
                                current_state = "friends";
                                sendMessageRequestButton.setText("Remove This Contact");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (!senderUserID.equals(receiverUserID)) {
            sendMessageRequestButton.setOnClickListener(view -> {
                sendMessageRequestButton.setEnabled(false);

                if (current_state.equals("new")) {
                    sendChatRequest();
                }
                if (current_state.equals("request_sent")) {
                    CancelChatRequest();
                }
                if (current_state.equals("request_received")) {
                    AcceptChatRequest();
                }
                if (current_state.equals("friends")) {
                    RemoveSpecificContact();
                }
            });
        } else {
            sendMessageRequestButton.setVisibility(View.INVISIBLE);
        }

    }

    @SuppressLint("SetTextI18n")
    private void RemoveSpecificContact() {

        ContactsRef.child(senderUserID).child(receiverUserID)
                .removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ContactsRef.child(receiverUserID).child(senderUserID)
                                .removeValue().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        sendMessageRequestButton.setEnabled(true);
                                        current_state = "new";
                                        sendMessageRequestButton.setText("Send Chat Request");

                                        declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                        declineMessageRequestButton.setEnabled(false);
                                    }
                                });
                    }
                });

    }

    @SuppressLint("SetTextI18n")
    private void AcceptChatRequest() {

        ContactsRef.child(senderUserID).child(receiverUserID)
                .child("Contacts").setValue("Saved").addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ContactsRef.child(receiverUserID).child(senderUserID)
                                .child("Contacts").setValue("Saved").addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        ChatRequestRef.child(senderUserID).child(receiverUserID)
                                                .removeValue().addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful()) {
                                                        ChatRequestRef.child(receiverUserID).child(senderUserID)
                                                                .removeValue().addOnCompleteListener(task3 -> {
                                                                    if (task3.isSuccessful()) {
                                                                        sendMessageRequestButton.setEnabled(true);
                                                                        current_state = "friends";
                                                                        sendMessageRequestButton.setText("Remove This Contact");

                                                                        declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                                        declineMessageRequestButton.setEnabled(false);
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                    }
                });

    }

    @SuppressLint("SetTextI18n")
    private void CancelChatRequest() {

        ChatRequestRef.child(senderUserID).child(receiverUserID)
                .removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ChatRequestRef.child(receiverUserID).child(senderUserID)
                                .removeValue().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        sendMessageRequestButton.setEnabled(true);
                                        current_state = "new";
                                        sendMessageRequestButton.setText("Send Chat Request");

                                        declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                        declineMessageRequestButton.setEnabled(false);
                                    }
                                });
                    }
                });

    }

    @SuppressLint("SetTextI18n")
    private void sendChatRequest() {

        ChatRequestRef.child(senderUserID).child(receiverUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ChatRequestRef.child(receiverUserID).child(senderUserID)
                                .child("request_type").setValue("received")
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {

                                        HashMap<String, String> chatNotificationMap = new HashMap<>();
                                        chatNotificationMap.put("from", senderUserID);
                                        chatNotificationMap.put("type", "request");

                                        NotificationRef.child(receiverUserID).push().setValue(chatNotificationMap).addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                sendMessageRequestButton.setEnabled(true);
                                                current_state = "request_sent";
                                                sendMessageRequestButton.setText("Cancel Chat Request");
                                            }
                                        });
                                    }
                                });
                    }
                });

    }

}
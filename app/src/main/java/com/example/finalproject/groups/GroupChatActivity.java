package com.example.finalproject.groups;

import static com.example.finalproject.models.References.GROUP_MESSAGES;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.GlideApp;
import com.example.finalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity {
    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSenderID, saveCurrentTime, saveCurrentDate;
    private ScrollView scrollView;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    View view;
    public String fileMessage;
    private DatabaseReference RootRef;
    private StorageReference storageReference;

    private ImageButton SendMessageButton, SendFilesButton;
    private EditText MessageInputText;

    private List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;
    private Context context;

    private String checker = "", myUrl = "";
    private Uri fileUri;
    private StorageTask storageTask;

    private Group currentGroup;
    private CircleImageView ivGroupProfile;
    private TextView tvGroupName;
    public EditText ed_FileMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        context = this;
        currentGroup = (Group) getIntent().getSerializableExtra("group");
        Toast.makeText(GroupChatActivity.this, currentGroup.getName(), Toast.LENGTH_SHORT).show();
        initializeFields();

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        messageReceiverID = currentGroup.getId();
        messageReceiverName = currentGroup.getName();
        messageReceiverImage = currentGroup.getPicUrl();

        InitializeControllers();
        setListeners();
    }


    private void initializeFields() {
        Toolbar mToolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        ivGroupProfile = mToolbar.findViewById(R.id.ivProfile);
        tvGroupName = mToolbar.findViewById(R.id.tvGroupName);
        tvGroupName.setText(currentGroup.getName());
        GlideApp.with(this).load(currentGroup.getPicUrl()).placeholder(R.drawable.ic_profile).into(ivGroupProfile);

        mToolbar.findViewById(R.id.llGroupBar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(getNextIntent(GroupProfileActivity.class));
            }
        });
    }

    private void InitializeControllers() {

        SendMessageButton = findViewById(R.id.send_message_btn);
        SendFilesButton = findViewById(R.id.send_files_btn);
        MessageInputText = findViewById(R.id.input_message);
        scrollView = findViewById(R.id.chat_scroll_view);
        ed_FileMessage = findViewById(R.id.ed_FileMessage);
        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        Calendar calendar = Calendar.getInstance();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
    }

    private void setListeners() {

        SendMessageButton.setOnClickListener(view -> {
            SendMessage();
        });

        SendFilesButton.setOnClickListener(view -> {
            CharSequence options[] = new CharSequence[]{
                    "Image",
                    "PDF File",
                    "Ms Word File",
                    "Add a Task"
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Select");

            builder.setItems(options, (dialogInterface, i) -> {
                if (i == 0) {
                    checker = "image";
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), 438);
                }
                if (i == 1) {
                    checker = "pdf";
                    Intent intent = new Intent();
                    intent.setType("application/pdf");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select PDF File"), 438);
                }
                if (i == 2) {
                    checker = "docx";
                    Intent intent = new Intent();
                    intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select MS Word File"), 438);
                }
                if (i == 3) {
                    checker = "task";
                }

            });
            builder.show();
        });

    }

    private void loadMessages() {
        messagesList.clear();

        RootRef.child(GROUP_MESSAGES).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @SuppressLint({"NotifyDataSetChanged", "ClickableViewAccessibility"})
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Messages messages = snapshot.getValue(Messages.class);

                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                        userMessagesList.smoothScrollToPosition(Objects.requireNonNull(userMessagesList.getAdapter()).getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void SendMessage() {

        String messageText = MessageInputText.getText().toString();

        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Message is Empty...", Toast.LENGTH_LONG).show();
        } else {
            String messageSenderRef = GROUP_MESSAGES + "/" + messageReceiverID;
            String messageReceiverRef = GROUP_MESSAGES + "/" + messageReceiverID;

            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                    .child(messageSenderID).child(messageReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();
            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);
            messageTextBody.put("to", messageReceiverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }
                MessageInputText.setText("");
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            fileUri = data.getData();

            if (!checker.equals("image")) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");

                storageReference = FirebaseStorage.getInstance().getReference("Users").child(messageSenderID).child(messageReceiverID).child("Document File");
                String messageSenderRef = GROUP_MESSAGES + "/" + messageReceiverID;
                String messageReceiverRef = GROUP_MESSAGES + "/" + messageReceiverID;

                DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                        .child(messageSenderID).child(messageReceiverID).push();

                String messagePushID = userMessageKeyRef.getKey();

                long timeMillis = System.currentTimeMillis();
                StorageReference filePath = storageReference.child(timeMillis + "." + getFileExtension(fileUri));

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
                view = getLayoutInflater().inflate(R.layout.file_message_dailogbox, null, false);
                Button payNow = view.findViewById(R.id.btn_submit);
                EditText ed_FileMessage = view.findViewById(R.id.ed_FileMessage);
                bottomSheetDialog.setContentView(view);
                bottomSheetDialog.show();
                bottomSheetDialog.setCancelable(false);
                bottomSheetDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

                payNow.setOnClickListener(v -> {
                    fileMessage = ed_FileMessage.getText().toString();

                    if (fileMessage != null) {

                        filePath.putFile(fileUri).addOnSuccessListener(taskSnapshot -> {

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isComplete()) ;
                            myUrl = String.valueOf(uriTask.getResult());
                            Map messageTextBody = new HashMap();
                            messageTextBody.put("message", myUrl);
                            messageTextBody.put("name", fileUri.getLastPathSegment());
                            messageTextBody.put("type", checker);
                            messageTextBody.put("from", messageSenderID);
                            messageTextBody.put("to", messageReceiverID);
                            messageTextBody.put("messageID", messagePushID);
                            messageTextBody.put("time", saveCurrentTime);
                            messageTextBody.put("date", saveCurrentDate);
                            messageTextBody.put("fileMessage", fileMessage);

                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(task1 -> {
                                if (!task1.isSuccessful()) {
                                    Toast.makeText(context, (CharSequence) task1.getException(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);

                        }).addOnProgressListener(snapshot -> {
                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        });


                    }
                    bottomSheetDialog.dismiss();
                });
            } else {

                storageReference = FirebaseStorage.getInstance().getReference("Users").child(messageReceiverID).child("Images File");
                String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
                String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

                DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                        .child(messageSenderID).child(messageReceiverID).push();

                String messagePushID = userMessageKeyRef.getKey();

                long timeMillis = System.currentTimeMillis();
                StorageReference filePath = storageReference.child(timeMillis + "." + getFileExtension(fileUri));
                storageTask = filePath.putFile(fileUri);
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
                view = getLayoutInflater().inflate(R.layout.file_message_dailogbox, null, false);
                Button payNow = view.findViewById(R.id.btn_submit);
                EditText ed_FileMessage = view.findViewById(R.id.ed_FileMessage);
                bottomSheetDialog.setContentView(view);
                bottomSheetDialog.show();
                bottomSheetDialog.setCancelable(false);
                bottomSheetDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

                payNow.setOnClickListener(v -> {
                    fileMessage = ed_FileMessage.getText().toString();

                    if (fileMessage != null) {

                        storageTask.continueWithTask(task -> {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return filePath.getDownloadUrl();
                        }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                myUrl = String.valueOf(downloadUri);

                                Map messageTextBody = new HashMap();
                                messageTextBody.put("message", myUrl);
                                messageTextBody.put("name", fileUri.getLastPathSegment());
                                messageTextBody.put("type", checker);
                                messageTextBody.put("from", messageSenderID);
                                messageTextBody.put("to", messageReceiverID);
                                messageTextBody.put("messageID", messagePushID);
                                messageTextBody.put("time", saveCurrentTime);
                                messageTextBody.put("date", saveCurrentDate);

                                Map messageBodyDetails = new HashMap();
                                messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

                                RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(task1 -> {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(context, (CharSequence) task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                            }
                        });

                    }
                });


            }

        }
    }

    private String getFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadMessages();
    }

    private Intent getNextIntent(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("group", currentGroup);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add_members:
                startActivity(getNextIntent(GroupMembersActivity.class));
                break;
            case R.id.item_tasks:
                startActivity(getNextIntent(GroupTasksActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
package com.example.finalproject.groups;

import static com.example.finalproject.models.FirebaseReference.GROUP_CHAT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finalproject.GlideApp;
import com.example.finalproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity {
    private ImageButton SendMessageButton;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessages;

    private DatabaseReference UsersRef;
    private DatabaseReference groupChatReference;

    private Group currentGroup;
    private String currentUserID;
    private String currentUserName;

    private CircleImageView ivGroupProfile;
    private TextView tvGroupName;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);



        currentGroup = (Group) getIntent().getSerializableExtra("group");
        Toast.makeText(GroupChatActivity.this, currentGroup.getName(), Toast.LENGTH_SHORT).show();


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference("Users");
        groupChatReference = FirebaseDatabase.getInstance().getReference(GROUP_CHAT).child(currentGroup.getId());

        InitializeFields();
        GetUserInfo();


        SendMessageButton.setOnClickListener(view -> {
            SaveMessageInfoToDatabase();

            userMessageInput.setText("");

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        });
    }



    @Override
    protected void onStart()
    {
        super.onStart();

        groupChatReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s)
            {
                if (dataSnapshot.exists())
                {
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s)
            {
                if (dataSnapshot.exists())
                {
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void InitializeFields()
    {
        Toolbar mToolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        ivGroupProfile = mToolbar.findViewById(R.id.ivProfile);
        tvGroupName = mToolbar.findViewById(R.id.tvGroupName);
        tvGroupName.setText(currentGroup.getName());
        GlideApp.with(this).load(currentGroup.getPicUrl()).placeholder(R.drawable.ic_profile).into(ivGroupProfile);

        SendMessageButton = findViewById(R.id.send_message_button);
        userMessageInput = findViewById(R.id.input_group_message);
        displayTextMessages = findViewById(R.id.group_chat_text_display);
        mScrollView = findViewById(R.id.my_scroll_view);

        mToolbar.findViewById(R.id.llGroupBar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(getNextIntent(GroupProfileActivity.class));
            }
        });
    }

    private Intent getNextIntent(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("group", currentGroup);
        return intent;
    }



    private void GetUserInfo()
    {
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    currentUserName = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    private void SaveMessageInfoToDatabase()
    {
        String message = userMessageInput.getText().toString();
        String messageKey = groupChatReference.push().getKey();

        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Please write message first...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForDate = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            String currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            String currentTime = currentTimeFormat.format(calForTime.getTime());


            HashMap<String, Object> groupMessageKey = new HashMap<>();
            groupChatReference.updateChildren(groupMessageKey);

            assert messageKey != null;
            DatabaseReference groupMessageKeyRef = groupChatReference.child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            groupMessageKeyRef.updateChildren(messageInfoMap);
        }
    }



    private void DisplayMessages(DataSnapshot dataSnapshot) {

        String chatName = "", chatMessage = "", chatTime = "", chatDate = "";

        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

            if (Objects.equals(snapshot.getKey(), "date"))
                chatDate = (String) snapshot.getValue();
            if (Objects.equals(snapshot.getKey(), "message"))
                chatMessage = (String)  snapshot.getValue();
            if (snapshot.getKey().equals("name"))
                chatName = (String)  snapshot.getValue();
            if (snapshot.getKey().equals("time"))
                chatTime = (String)  snapshot.getValue();
        }

        displayTextMessages.append(chatName + " :\n" + chatMessage + "\n" + chatTime + "     " + chatDate + "\n\n\n");
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
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
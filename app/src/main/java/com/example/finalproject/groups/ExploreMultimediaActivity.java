package com.example.finalproject.groups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import com.example.finalproject.R;
import com.example.finalproject.models.References;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExploreMultimediaActivity extends AppCompatActivity {

    Group currentGroup;
    RecyclerView recyclerViewMessage;
    public List<Messages> groupList;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_multimedia);
        currentGroup = (Group) getIntent().getSerializableExtra("group");
        recyclerViewMessage = findViewById(R.id.recyclerViewMessage);
        groupList = new ArrayList<>();

        Query query = FirebaseDatabase.getInstance().getReference(References.GROUP_MESSAGES).child(currentGroup.getId()).
                orderByChild("type")
                .equalTo("pdf");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot FetchData : snapshot.getChildren()) {
                    Messages model1 = FetchData.getValue(Messages.class);
                    groupList.add(model1);
                }
                recyclerViewMessage.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
                recyclerViewMessage.setAdapter(new GroupMessagesAdapter(groupList, getApplicationContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExploreMultimediaActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Query query1 = FirebaseDatabase.getInstance().getReference(References.GROUP_MESSAGES).child(currentGroup.getId()).
                orderByChild("type")
                .equalTo("docx");
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot FetchData : snapshot.getChildren()) {
                    Messages model2 = FetchData.getValue(Messages.class);
                    groupList.add(model2);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExploreMultimediaActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
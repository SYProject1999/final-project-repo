package com.example.finalproject.groups;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupsChatsFragment extends Fragment {

    private View groupFragmentView;
    private RecyclerView recyclerView;
    private GroupsAdapter adapter;
    private List<Group> groupsList = new ArrayList<>();

    private DatabaseReference GroupRef;

    public GroupsChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        groupFragmentView = inflater.inflate(R.layout.fragment_groups_chats, container, false);

        GroupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        initializeFields();
        RetrieveAndDisplayGroups();
        return groupFragmentView;
    }



    private void initializeFields()
    {
        recyclerView = groupFragmentView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        groupsList = new ArrayList<>();
        adapter = new GroupsAdapter(groupsList);
        recyclerView.setAdapter(adapter);
    }

    private void RetrieveAndDisplayGroups()
    {
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                groupsList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    if (group.getMembers().contains(FirebaseAuth.getInstance().getUid())) {
                        groupsList.add(group);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
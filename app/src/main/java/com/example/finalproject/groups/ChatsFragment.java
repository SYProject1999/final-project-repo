package com.example.finalproject.groups;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.finalproject.GlideApp;
import com.example.finalproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragment extends Fragment {

    private RecyclerView chatsList;

    private DatabaseReference ChatsRef, UserRef;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View privateChatsView = inflater.inflate(R.layout.fragment_chats, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        ChatsRef = FirebaseDatabase.getInstance().getReference("Contacts").child(currentUserID);
        UserRef = FirebaseDatabase.getInstance().getReference("Users");

        chatsList = privateChatsView.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return privateChatsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ChatsRef, Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts, ChatsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatsViewHolder holder, int position, @NonNull Contacts model) {
                final String usersIDs = getRef(position).getKey();

                assert usersIDs != null;
                UserRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            String retImage = "";
                            if (snapshot.hasChild("imageUrl")) {
                                retImage = snapshot.child("imageUrl").getValue(String.class);
                            }
                            GlideApp.with(requireContext()).load(retImage).placeholder(R.drawable.ic_baseline_person_24).into(holder.profileImage);

                            final String retName = snapshot.child("fullName").getValue(String.class);
                            holder.userName.setText(retName);

                            if (snapshot.hasChild("userState")) {
                                String state = snapshot.child("userState").child("state").getValue(String.class);
                                String date = snapshot.child("userState").child("date").getValue(String.class);
                                String time = snapshot.child("userState").child("time").getValue(String.class);

                                holder.userStatus.setText(state);
                            }

                            String finalRetImage = retImage;
                            holder.itemView.setOnClickListener(view -> {

                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                chatIntent.putExtra("visit_user_id", usersIDs);
                                chatIntent.putExtra("visit_user_name", retName);
                                chatIntent.putExtra("visit_image", finalRetImage);
                                startActivity(chatIntent);

                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                return new ChatsViewHolder(view);
            }
        };
        chatsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        TextView userStatus, userName;

        public ChatsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            profileImage = itemView.findViewById(R.id.users_profile_image);
            userStatus = itemView.findViewById(R.id.user_status);
            userName = itemView.findViewById(R.id.user_profile_name);
        }
    }
}
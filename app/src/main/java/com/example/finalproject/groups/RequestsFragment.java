package com.example.finalproject.groups;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.GlideApp;
import com.example.finalproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestsFragment extends Fragment {

    private View RequestsFragmentView;
    private RecyclerView myRequestsList;

    private DatabaseReference ChatRequestsRef, UsersRef, ContactsRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public RequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RequestsFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference("Users");
        ChatRequestsRef = FirebaseDatabase.getInstance().getReference("Chat Requests");
        ContactsRef = FirebaseDatabase.getInstance().getReference("Contacts");

        myRequestsList = RequestsFragmentView.findViewById(R.id.chat_requests_list);
        myRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return RequestsFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ChatRequestsRef.child(currentUserID), Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts, RequestsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestsViewHolder holder, int position, @NonNull Contacts model) {

                holder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.VISIBLE);

                final String list_user_id = getRef(position).getKey();

                DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            String type = snapshot.getValue(String.class);
                            if (type.equals("received")) {
                                UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        String requestProfileImage = "";
                                        String requestUserStatus = "No Status";

                                        if (snapshot.hasChild("imageUrl")) {
                                            requestProfileImage = snapshot.child("imageUrl").getValue(String.class);
                                        }
                                        if (snapshot.hasChild("Status")) {
                                            requestUserStatus = snapshot.child("Status").getValue(String.class);
                                        }

                                        GlideApp.with(requireContext()).load(requestProfileImage).placeholder(R.drawable.ic_baseline_person_24).into(holder.profileImage);
                                        final String requestUserName = snapshot.child("fullName").getValue(String.class);

                                        holder.userName.setText(requestUserName);
                                        holder.userStatus.setText("Wants To Connect With You");

                                        holder.itemView.setOnClickListener(view -> {

                                            CharSequence options[] = new CharSequence[] {
                                                    "Accept",
                                                    "Cancel"
                                            };

                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle(requestUserName + " Chat Request");

                                            builder.setItems(options, (dialogInterface, i) -> {
                                                if (i == 0) {
                                                    ContactsRef.child(currentUserID).child(list_user_id).child("Contact")
                                                            .setValue("Saved").addOnCompleteListener(task -> {

                                                                if (task.isSuccessful()) {
                                                                    ContactsRef.child(list_user_id).child(currentUserID).child("Contact")
                                                                            .setValue("Saved").addOnCompleteListener(task1 -> {

                                                                                if (task1.isSuccessful()) {
                                                                                    ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                                                            .removeValue().addOnCompleteListener(task2 -> {

                                                                                                if (task2.isSuccessful()) {
                                                                                                    ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                                                            .removeValue().addOnCompleteListener(task3 -> {

                                                                                                                if (task3.isSuccessful()) {
                                                                                                                    Toast.makeText(getContext(), "New Contact Saved", Toast.LENGTH_SHORT).show();
                                                                                                                }
                                                                                                    });
                                                                                                }
                                                                                    });
                                                                                }
                                                                    });
                                                                }
                                                    });
                                                }

                                                if (i == 1) {
                                                    ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                            .removeValue().addOnCompleteListener(task -> {

                                                                if (task.isSuccessful()) {
                                                                    ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                            .removeValue().addOnCompleteListener(task1 -> {
                                                                                if (task1.isSuccessful()) {
                                                                                    Toast.makeText(getContext(), "Contact Deleted", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                    });
                                                                }
                                                    });
                                                }

                                            });
                                            builder.show();
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            else if (type.equals("sent")) {
                                Button request_sent_btn = holder.itemView.findViewById(R.id.request_accept_btn);
                                request_sent_btn.setText("Request Sent");

                                holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.INVISIBLE);

                                UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        String requestProfileImage = "";
                                        String requestUserStatus = "No Status";

                                        if (snapshot.hasChild("imageUrl")) {
                                            requestProfileImage = snapshot.child("imageUrl").getValue(String.class);
                                        }
                                        if (snapshot.hasChild("Status")) {
                                            requestUserStatus = snapshot.child("Status").getValue(String.class);
                                        }

                                        GlideApp.with(requireContext()).load(requestProfileImage).placeholder(R.drawable.ic_baseline_person_24).into(holder.profileImage);
                                        final String requestUserName = snapshot.child("fullName").getValue(String.class);

                                        holder.userName.setText(requestUserName);
                                        holder.userStatus.setText("you have sent a request to " + requestUserName);

                                        holder.itemView.setOnClickListener(view -> {

                                            CharSequence options[] = new CharSequence[] {
                                                    "Cancel Chat Request"
                                            };

                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle(requestUserName + " Chat Request");

                                            builder.setItems(options, (dialogInterface, i) -> {
                                                if (i == 0) {
                                                    ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                            .removeValue().addOnCompleteListener(task -> {

                                                        if (task.isSuccessful()) {
                                                            ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                    .removeValue().addOnCompleteListener(task1 -> {
                                                                if (task1.isSuccessful()) {
                                                                    Toast.makeText(getContext(), "Request Canceled", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    });
                                                }

                                            });
                                            builder.show();
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                return new RequestsViewHolder(view);
            }
        };
        myRequestsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView profileImage;
        Button AcceptButton, CancelButton;


        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);


            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            AcceptButton = itemView.findViewById(R.id.request_accept_btn);
            CancelButton = itemView.findViewById(R.id.request_cancel_btn);
        }
    }
}
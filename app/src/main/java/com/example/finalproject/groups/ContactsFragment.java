package com.example.finalproject.groups;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class ContactsFragment extends Fragment {

    private RecyclerView myContactsList;

    private DatabaseReference ContactsRef, UserRef, RemoveContactsRef;

    private String senderUserID, userIDs;

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contactsView = inflater.inflate(R.layout.fragment_contacts, container, false);

        myContactsList = contactsView.findViewById(R.id.contacts_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        ContactsRef = FirebaseDatabase.getInstance().getReference("Contacts").child(currentUserID);
        RemoveContactsRef = FirebaseDatabase.getInstance().getReference("Contacts");
        UserRef = FirebaseDatabase.getInstance().getReference("Users");

        senderUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        return contactsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ContactsRef, Contacts.class).build();

        final FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactsViewHolder holder, int position, @NonNull Contacts model) {
                userIDs = getRef(position).getKey();

                assert userIDs != null;
                UserRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            String ImageUrl = "";
                            String profileStatus = "no Status";
                            String userFullName = Objects.requireNonNull(snapshot.child("fullName").getValue()).toString();

                            if (snapshot.hasChild("imageUrl")) {
                                ImageUrl = Objects.requireNonNull(snapshot.child("imageUrl").getValue()).toString();
                            }
                            if (snapshot.hasChild("Status")) {
                                profileStatus = Objects.requireNonNull(snapshot.child("Status").getValue()).toString();
                            }

                            holder.userName.setText(userFullName);
                            holder.userStatus.setText(profileStatus);
                            GlideApp.with(requireContext()).load(ImageUrl).placeholder(R.drawable.ic_baseline_person_24).into(holder.profileImage);
                        }

                        holder.itemView.setOnClickListener(view -> {

                            CharSequence options[] = new CharSequence[] {
                                    "Remove",
                                    "Cancel"
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Do you want to remove this contact?");

                            builder.setItems(options, ((dialogInterface, i) -> {
                               if (i == 0) RemoveSpecificContact();

                            }));
                            builder.show();

                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                return new ContactsViewHolder(view);
            }
        };
        myContactsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        CircleImageView profileImage;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }

    private void RemoveSpecificContact() {

        RemoveContactsRef.child(senderUserID).child(userIDs)
                .removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        RemoveContactsRef.child(userIDs).child(senderUserID)
                                .removeValue().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(getContext() , "Contact Removed" , Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

    }
}
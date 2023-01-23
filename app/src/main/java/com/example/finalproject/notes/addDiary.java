package com.example.finalproject.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.R;
import com.example.finalproject.models.NoteModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class addDiary extends AppCompatActivity {

    FloatingActionButton createNoteFab;
    private DatabaseReference databaseReference;

    private String key = "", note = "", content = "";

    RecyclerView recyclerView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    FirebaseRecyclerAdapter<NoteModel, NoteViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);

        createNoteFab = findViewById(R.id.createNoteFab);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        String userID = firebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Notes");

        recyclerView = findViewById(R.id.noteRecyclerView);
        recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(recyclerView.getAdapter());

        createNoteFab.setOnClickListener(view -> {
            startActivity(new Intent(addDiary.this, CreateNote.class));
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<NoteModel> options = new FirebaseRecyclerOptions.Builder<NoteModel>()
                .setQuery(databaseReference, NoteModel.class).build();

        FirebaseRecyclerAdapter<NoteModel, NoteViewHolder> noteAdapter = new FirebaseRecyclerAdapter<NoteModel, NoteViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull NoteModel model) {
                ImageView popUpButton = holder.itemView.findViewById(R.id.menuPopButton);

                int colorCode = getRandomColor();
                holder.note.setBackgroundColor(holder.itemView.getResources().getColor(colorCode, null));

                holder.setNoteTitle(model.getNoteTitle());
                holder.setNoteContent(model.getNoteContent());

                popUpButton.setOnClickListener(view -> {
                    key = getRef(holder.getAbsoluteAdapterPosition()).getKey();
                    PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                    popupMenu.setGravity(Gravity.END);

                    popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(menuItem -> {
                      Intent intent = new Intent(view.getContext(), EditNoteActivity.class);
                      view.getContext().startActivity(intent);
                      return false;
                    });

                    popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(menuItem ->{
                        databaseReference.child(key).removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(view.getContext(), "Note Deleted Successfully", Toast.LENGTH_LONG).show();
                            } else {
                                String error = Objects.requireNonNull(task.getException()).toString();
                                Toast.makeText(view.getContext(), "Delete Failed " + error, Toast.LENGTH_LONG).show();
                            }
                        });

                        return false;
                    });

                    popupMenu.show();

                });

            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };

        recyclerView.setAdapter(noteAdapter);
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        View noteViewHolder;
        LinearLayout note;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteViewHolder = itemView;
            note = itemView.findViewById(R.id.note);
        }

        public void setNoteTitle(String note) {
            TextView taskTextView = noteViewHolder.findViewById(R.id.noteTitle);
            taskTextView.setText(note);
        }

        public void setNoteContent(String content) {
            TextView descriptionTextView = noteViewHolder.findViewById(R.id.noteContent);
            descriptionTextView.setText(content);
        }
    }

    private int getRandomColor()
    {
        List<Integer> colorCode=new ArrayList<>();
        colorCode.add(R.color.gray);
        colorCode.add(R.color.pink);
        colorCode.add(R.color.lightgreen);
        colorCode.add(R.color.skyblue);
        colorCode.add(R.color.color1);
        colorCode.add(R.color.color2);
        colorCode.add(R.color.color3);
        colorCode.add(R.color.color4);
        colorCode.add(R.color.color5);
        colorCode.add(R.color.green);

        Random random=new Random();
        int number=random.nextInt(colorCode.size());
        return colorCode.get(number);
    }

}
package com.example.finalproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.finalproject.models.HabitsModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;

public class HabitsFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private RecyclerView recyclerView;

    private DatabaseReference reference;

    private TextView emptyView;
    private int drawableSelected = 0, day = 0, month = 0, year = 0, hour = 0, minute = 0;
    private String key = "", habitsTitle = "", habitsDescription = "", timeStamp = "", cleanDate = "", cleanTime = "";
    private final Calculations calculations = new Calculations();
    private View dialogView, updateHabitView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_habits, container, false);

        emptyView = view.findViewById(R.id.tv_emptyView);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        String userID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Habits");

        recyclerView = view.findViewById(R.id.rv_habits);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerView.getAdapter());
        recyclerView.setLayoutManager(linearLayoutManager);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.fab_add_habit);
        floatingActionButton.setOnClickListener(v -> addHabit());

        return view;
    }

    @SuppressLint("InflateParams")
    private void addHabit() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());

        dialogView = inflater.inflate(R.layout.habits_input_layout, null);
        myDialog.setView(dialogView);

        AlertDialog alertDialog = myDialog.create();
        alertDialog.setCancelable(false);

        EditText et_habitTitle = dialogView.findViewById(R.id.et_habitTitle);
        EditText et_habitDescription = dialogView.findViewById(R.id.et_habitDescription);
        Button confirmBtn = dialogView.findViewById(R.id.btn_confirm);
        Button cancelBtn = dialogView.findViewById(R.id.habit_cancelBtn);

        // selectedDrawable
        ImageView iv_fastFoodSelected = dialogView.findViewById(R.id.iv_fastFoodSelected);
        ImageView iv_teaSelected = dialogView.findViewById(R.id.iv_teaSelected);
        ImageView iv_smokingSelected = dialogView.findViewById(R.id.iv_smokingSelected);

        iv_fastFoodSelected.setOnClickListener(view -> {
            iv_fastFoodSelected.setSelected(!iv_fastFoodSelected.isSelected());
            drawableSelected = R.drawable.ic_baseline_fastfood_24;
            iv_smokingSelected.setSelected(false);
            iv_teaSelected.setSelected(false);
        });

        iv_smokingSelected.setOnClickListener(view -> {
            iv_smokingSelected.setSelected(!iv_smokingSelected.isSelected());
            drawableSelected = R.drawable.ic_baseline_smoking_rooms_24;
            iv_fastFoodSelected.setSelected(false);
            iv_teaSelected.setSelected(false);
        });

        iv_teaSelected.setOnClickListener(view -> {
            iv_teaSelected.setSelected(!iv_teaSelected.isSelected());
            drawableSelected = R.drawable.ic_baseline_emoji_food_beverage_24;
            iv_fastFoodSelected.setSelected(false);
            iv_smokingSelected.setSelected(false);
        });

        // pickDateAndTime
        Button btn_pickDate = dialogView.findViewById(R.id.btn_pickDate);
        Button btn_pickTime = dialogView.findViewById(R.id.btn_pickTime);

        btn_pickDate.setOnClickListener(v -> {
            getDateCalender();
            new DatePickerDialog(requireContext(), this, year, month, day).show();
        });

        btn_pickTime.setOnClickListener(v -> {
            getTimeCalender();
            new TimePickerDialog(requireContext(), this, hour, minute, true).show();
        });

        cancelBtn.setOnClickListener(v -> alertDialog.dismiss());

        confirmBtn.setOnClickListener(v -> {
            habitsTitle = et_habitTitle.getText().toString().trim();
            habitsDescription = et_habitDescription.getText().toString().trim();
            String id = reference.push().getKey();
            timeStamp = cleanDate + " " + cleanTime;

            if (TextUtils.isEmpty(habitsTitle)) {
                et_habitTitle.setError("Habit Title is Required");
                et_habitTitle.requestFocus();
                return;
            } else if (TextUtils.isEmpty(habitsDescription)) {
                et_habitDescription.setError("Habit Description is Required");
                et_habitDescription.requestFocus();
                return;
            } else if (timeStamp.length() != 16) {
                Toast.makeText(getContext(), "Please select a time and date", Toast.LENGTH_SHORT).show();
                return;
            } else {
                HabitsModel habit = new HabitsModel(id, habitsTitle, habitsDescription, timeStamp, drawableSelected);
                assert id != null;
                reference.child(id).setValue(habit).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Habit Has Been Inserted Successfully", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Failed: " + Objects.requireNonNull(task.getException()), Toast.LENGTH_LONG).show();
                    }
                });
            }
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<HabitsModel> options = new FirebaseRecyclerOptions.Builder<HabitsModel>()
                .setQuery(reference, HabitsModel.class).build();

        FirebaseRecyclerAdapter<HabitsModel, MyViewHolder> habitsAdapter = new FirebaseRecyclerAdapter<HabitsModel, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull HabitsModel model) {
                holder.setHabitTitle(model.getHabitTitle());
                holder.setHabitDescription(model.getHabitDescription());
                holder.setTimeElapsed(model.getHabitStartTime());
                holder.setCreatedDate(model.getHabitStartTime());
                holder.setHabitIcon(model.getImageId());

                holder.holderView.setOnClickListener(view -> {
                    key = getRef(holder.getAbsoluteAdapterPosition()).getKey();
                    habitsTitle = model.getHabitTitle();
                    habitsDescription = model.getHabitDescription();
                    timeStamp = model.getHabitStartTime();
                    drawableSelected = model.getImageId();
                    updateHabit();
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View habitView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_habit_item, parent, false);
                return new MyViewHolder(habitView);
            }
        };
        recyclerView.setAdapter(habitsAdapter);
        habitsAdapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View holderView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            holderView = itemView;
        }

        public void setHabitTitle(String habitTitle) {
            TextView tv_item_title = holderView.findViewById(R.id.tv_item_title);
            tv_item_title.setText(habitTitle);
        }

        public void setHabitDescription(String habitDescription) {
            TextView tv_item_description = holderView.findViewById(R.id.tv_item_description);
            tv_item_description.setText(habitDescription);
        }

        public void setTimeElapsed(String timeElapsed) {
            TextView tv_timeElapsed = holderView.findViewById(R.id.tv_timeElapsed);
            tv_timeElapsed.setText(timeElapsed);
        }

        @SuppressLint("SetTextI18n")
        public void setCreatedDate(String createdDate) {
            TextView tv_item_createdTimeStamp = holderView.findViewById(R.id.tv_item_createdTimeStamp);
            tv_item_createdTimeStamp.setText("Since: " + createdDate);
        }

        public void setHabitIcon(int habitIcon) {
            ImageView iv_habit_icon = holderView.findViewById(R.id.iv_habit_icon);
            iv_habit_icon.setImageResource(habitIcon);
        }
    }

    @SuppressLint("InflateParams")
    private void updateHabit() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        updateHabitView = inflater.inflate(R.layout.habits_update_layout, null);
        myDialog.setView(updateHabitView);
        AlertDialog alertDialog = myDialog.create();

        EditText et_habitTitle = updateHabitView.findViewById(R.id.et_habitTitle_update);
        EditText et_habitDescription = updateHabitView.findViewById(R.id.et_habitDescription_update);
        TextView tv_dateSelected = updateHabitView.findViewById(R.id.tv_dateSelected_update);
        TextView tv_timeSelected = updateHabitView.findViewById(R.id.tv_timeSelected_update);

        et_habitTitle.setText(habitsTitle);
        et_habitTitle.setSelection(habitsTitle.length());

        et_habitDescription.setText(habitsDescription);
        et_habitDescription.setSelection(habitsDescription.length());

        tv_dateSelected.setText(timeStamp.substring(0, 10));
        tv_timeSelected.setText(timeStamp.substring(11));

        Button confirmBtn = updateHabitView.findViewById(R.id.btn_confirm_update);
        Button deleteBtn = updateHabitView.findViewById(R.id.btn_delete_habit);

        // selectedDrawable
        ImageView iv_fastFoodSelected = updateHabitView.findViewById(R.id.iv_fastFoodSelected_update);
        ImageView iv_teaSelected = updateHabitView.findViewById(R.id.iv_teaSelected_update);
        ImageView iv_smokingSelected = updateHabitView.findViewById(R.id.iv_smokingSelected_update);

        if (drawableSelected == R.drawable.ic_baseline_fastfood_24) {
            iv_fastFoodSelected.setSelected(!iv_fastFoodSelected.isSelected());
            drawableSelected = R.drawable.ic_baseline_fastfood_24;
        }

        if (drawableSelected == R.drawable.ic_baseline_emoji_food_beverage_24) {
            iv_teaSelected.setSelected(!iv_teaSelected.isSelected());
            drawableSelected = R.drawable.ic_baseline_emoji_food_beverage_24;
        }

        if (drawableSelected == R.drawable.ic_baseline_smoking_rooms_24) {
            iv_smokingSelected.setSelected(!iv_smokingSelected.isSelected());
            drawableSelected = R.drawable.ic_baseline_smoking_rooms_24;
        }

        iv_fastFoodSelected.setOnClickListener(view -> {
            iv_fastFoodSelected.setSelected(!iv_fastFoodSelected.isSelected());
            drawableSelected = R.drawable.ic_baseline_fastfood_24;
            iv_smokingSelected.setSelected(false);
            iv_teaSelected.setSelected(false);
        });

        iv_smokingSelected.setOnClickListener(view -> {
            iv_smokingSelected.setSelected(!iv_smokingSelected.isSelected());
            drawableSelected = R.drawable.ic_baseline_smoking_rooms_24;
            iv_fastFoodSelected.setSelected(false);
            iv_teaSelected.setSelected(false);
        });

        iv_teaSelected.setOnClickListener(view -> {
            iv_teaSelected.setSelected(!iv_teaSelected.isSelected());
            drawableSelected = R.drawable.ic_baseline_emoji_food_beverage_24;
            iv_fastFoodSelected.setSelected(false);
            iv_smokingSelected.setSelected(false);
        });

        // pickDateAndTime
        Button btn_pickDate = updateHabitView.findViewById(R.id.btn_pickDate_update);
        Button btn_pickTime = updateHabitView.findViewById(R.id.btn_pickTime_update);

        btn_pickDate.setOnClickListener(v -> {
            getDateCalender();
            new DatePickerDialog(requireContext(), this, year, month, day).show();
        });

        btn_pickTime.setOnClickListener(v -> {
            getTimeCalender();
            new TimePickerDialog(requireContext(), this, hour, minute, true).show();
        });

        confirmBtn.setOnClickListener(view -> {
            habitsTitle = et_habitTitle.getText().toString().trim();
            habitsDescription = et_habitDescription.getText().toString().trim();
            String id = reference.push().getKey();
            timeStamp = cleanDate + " " + cleanTime;

            if (TextUtils.isEmpty(habitsTitle)) {
                et_habitTitle.setError("Habit Title is Required");
                et_habitTitle.requestFocus();
            } else if (TextUtils.isEmpty(habitsDescription)) {
                et_habitDescription.setError("Habit Description is Required");
                et_habitDescription.requestFocus();
            } else if (TextUtils.isEmpty(timeStamp)) {
                Toast.makeText(getContext(), "Please select a time and date", Toast.LENGTH_SHORT).show();
            } else {
                HabitsModel habit = new HabitsModel(id, habitsTitle, habitsDescription, timeStamp, drawableSelected);
                reference.child(key).setValue(habit).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Habit Has Been Updated Successfully", Toast.LENGTH_LONG).show();
                    } else {
                        String error = Objects.requireNonNull(task.getException()).toString();
                        Toast.makeText(getContext(), "Update Failed " + error, Toast.LENGTH_LONG).show();
                    }
                });
            }
            alertDialog.dismiss();
        });

        deleteBtn.setOnClickListener(view -> {
            reference.child(key).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Habit Has Been Deleted Successfully", Toast.LENGTH_LONG).show();
                } else {
                    String error = Objects.requireNonNull(task.getException()).toString();
                    Toast.makeText(getContext(), "Delete Failed " + error, Toast.LENGTH_LONG).show();
                }
            });
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int yearX, int monthX, int dayX) {
        TextView tv_dateSelected = dialogView.findViewById(R.id.tv_dateSelected);
        if (tv_dateSelected == null) {
            tv_dateSelected = updateHabitView.findViewById(R.id.tv_dateSelected);
        }
        cleanDate = calculations.cleanDate(dayX, monthX, yearX);
        tv_dateSelected.setText(cleanDate);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minuteX) {
        TextView tv_timeSelected = dialogView.findViewById(R.id.tv_timeSelected);
        if (tv_timeSelected == null) {
            tv_timeSelected = updateHabitView.findViewById(R.id.tv_timeSelected);
        }
        cleanTime = calculations.cleanTime(hourOfDay, minuteX);
        tv_timeSelected.setText(cleanTime);
    }

    private void getTimeCalender() {
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }

    private void getDateCalender() {
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
    }
}
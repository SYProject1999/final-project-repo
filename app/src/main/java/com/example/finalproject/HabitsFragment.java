package com.example.finalproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class HabitsFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private View view;
    private RecyclerView recyclerView;

    private DatabaseReference reference;

    private int drawableSelected = 0, day = 0, month = 0, year = 0, hour = 0, minute = 0;
    private String key = "", habitsTitle = "", habitsDescription = "", timeStamp = "", cleanDate = "", cleanTime = "";
    private final Calculations calculations = new Calculations();
    private View dialogView, updateHabitView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_habits, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String userID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Habits");

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_habits);
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

    @Override
    public void onStart() {
        super.onStart();

        CardView cv_cardView = (CardView) view.findViewById(R.id.cv_cardView);
        FirebaseRecyclerOptions<HabitsModel> options = new FirebaseRecyclerOptions.Builder<HabitsModel>()
                .setQuery(reference, HabitsModel.class).build();

        FirebaseRecyclerAdapter<HabitsModel, HabitsFragment.MyViewHolder> habitsAdapter = new FirebaseRecyclerAdapter<HabitsModel, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull HabitsModel model) {
                holder.setHabitTitle(model.getHabitTitle());
                holder.setHabitDescription(model.getHabitDescription());
                holder.setTimeElapsed(model.getHabitStartTime());
                holder.setCreatedDate(model.getHabitStartTime());
                holder.setHabitIcon(model.getImageId());

                holder.holderView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        key = getRef(holder.getAbsoluteAdapterPosition()).getKey();
                        habitsTitle = model.getHabitTitle();
                        habitsDescription = model.getHabitDescription();
                        updateHabit();
                    }
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View holderView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.holderView = itemView;
        }

        public void setHabitTitle(String habitTitle) {
            TextView tv_item_title = (TextView) holderView.findViewById(R.id.tv_item_title);
            tv_item_title.setText(habitTitle);
        }

        public void setHabitDescription(String habitDescription) {
            TextView tv_item_description = (TextView) holderView.findViewById(R.id.tv_item_description);
            tv_item_description.setText(habitDescription);
        }

        public void setTimeElapsed(String timeElapsed) {
            TextView tv_timeElapsed = (TextView) holderView.findViewById(R.id.tv_timeElapsed);
            tv_timeElapsed.setText(calculations.calculateTimeBetweenDates(timeElapsed));
        }

        public void setCreatedDate(String createdDate) {
            TextView tv_item_createdTimeStamp = (TextView) holderView.findViewById(R.id.tv_item_createdTimeStamp);
            tv_item_createdTimeStamp.setText("Since: " + createdDate);
        }

        public void setHabitIcon(int habitIcon) {
            ImageView iv_habit_icon = (ImageView) holderView.findViewById(R.id.iv_habit_icon);
            iv_habit_icon.setImageResource(habitIcon);
        }
    }

    @SuppressLint("InflateParams")
    private void addHabit() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());

        dialogView = inflater.inflate(R.layout.habits_input_layout, null);
        myDialog.setView(dialogView);

        AlertDialog alertDialog = myDialog.create();
        alertDialog.setCancelable(false);

        EditText et_habitTitle = (EditText) dialogView.findViewById(R.id.et_habitTitle);
        EditText et_habitDescription = (EditText) dialogView.findViewById(R.id.et_habitDescription);
        Button confirmBtn = (Button) dialogView.findViewById(R.id.btn_confirm);
        Button cancelBtn = (Button) dialogView.findViewById(R.id.habit_cancelBtn);

        // selectedDrawable
        ImageView iv_fastFoodSelected = (ImageView) dialogView.findViewById(R.id.iv_fastFoodSelected);
        ImageView iv_teaSelected = (ImageView) dialogView.findViewById(R.id.iv_teaSelected);
        ImageView iv_smokingSelected = (ImageView) dialogView.findViewById(R.id.iv_smokingSelected);

        iv_fastFoodSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_fastFoodSelected.setSelected(!iv_fastFoodSelected.isSelected());
                drawableSelected = R.drawable.fastfood_selected;
                iv_smokingSelected.setSelected(false);
                iv_teaSelected.setSelected(false);
            }
        });

        iv_smokingSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_smokingSelected.setSelected(!iv_smokingSelected.isSelected());
                drawableSelected = R.drawable.ic_baseline_smoking_rooms_24;
                iv_fastFoodSelected.setSelected(false);
                iv_teaSelected.setSelected(false);
            }
        });

        iv_teaSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_teaSelected.setSelected(!iv_teaSelected.isSelected());
                drawableSelected = R.drawable.tea_selected;
                iv_fastFoodSelected.setSelected(false);
                iv_smokingSelected.setSelected(false);
            }
        });

        // pickDateAndTime
        Button btn_pickDate = (Button) dialogView.findViewById(R.id.btn_pickDate);
        Button btn_pickTime = (Button) dialogView.findViewById(R.id.btn_pickTime);

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
            } else if (TextUtils.isEmpty(habitsDescription)) {
                et_habitDescription.setError("Habit Description is Required");
                et_habitDescription.requestFocus();
            } else if (TextUtils.isEmpty(timeStamp)) {
                Toast.makeText(getContext(), "Please select a time and date", Toast.LENGTH_SHORT).show();
            } else {
                HabitsModel habit = new HabitsModel(id, habitsTitle, habitsDescription, timeStamp, drawableSelected);
                reference.child(id).setValue(habit).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Habit Has Been Inserted Successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Failed: " + task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    private void updateHabit() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        updateHabitView = inflater.inflate(R.layout.habits_update_layout, null);
        myDialog.setView(updateHabitView);
        AlertDialog alertDialog = myDialog.create();

        EditText et_habitTitle = (EditText) updateHabitView.findViewById(R.id.et_habitTitle_update);
        EditText et_habitDescription = (EditText) updateHabitView.findViewById(R.id.et_habitDescription_update);
        Button confirmBtn = (Button) updateHabitView.findViewById(R.id.btn_confirm_update);
        Button deleteBtn = (Button) updateHabitView.findViewById(R.id.btn_delete_habit);

        // selectedDrawable
        ImageView iv_fastFoodSelected = (ImageView) updateHabitView.findViewById(R.id.iv_fastFoodSelected_update);
        ImageView iv_teaSelected = (ImageView) updateHabitView.findViewById(R.id.iv_teaSelected_update);
        ImageView iv_smokingSelected = (ImageView) updateHabitView.findViewById(R.id.iv_smokingSelected_update);

        iv_fastFoodSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_fastFoodSelected.setSelected(!iv_fastFoodSelected.isSelected());
                drawableSelected = R.drawable.fastfood_selected;
                iv_smokingSelected.setSelected(false);
                iv_teaSelected.setSelected(false);
            }
        });

        iv_smokingSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_smokingSelected.setSelected(!iv_smokingSelected.isSelected());
                drawableSelected = R.drawable.smocking_selected;
                iv_fastFoodSelected.setSelected(false);
                iv_teaSelected.setSelected(false);
            }
        });

        iv_teaSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_teaSelected.setSelected(!iv_teaSelected.isSelected());
                drawableSelected = R.drawable.tea_selected;
                iv_fastFoodSelected.setSelected(false);
                iv_smokingSelected.setSelected(false);
            }
        });

        // pickDateAndTime
        Button btn_pickDate = (Button) updateHabitView.findViewById(R.id.btn_pickDate_update);
        Button btn_pickTime = (Button) updateHabitView.findViewById(R.id.btn_pickTime_update);

        btn_pickDate.setOnClickListener(v -> {
            getDateCalender();
            new DatePickerDialog(requireContext(), this, year, month, day).show();
        });

        btn_pickTime.setOnClickListener(v -> {
            getTimeCalender();
            new TimePickerDialog(requireContext(), this, hour, minute, true).show();
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    reference.child(key).setValue(habit).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Habit Has Been Updated Successfully", Toast.LENGTH_LONG).show();
                            } else {
                                String error = task.getException().toString();
                                Toast.makeText(getContext(), "Update Failed " + error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                alertDialog.dismiss();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Habit Has Been Deleted Successfully", Toast.LENGTH_LONG).show();
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(getContext(), "Delete Failed " + error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int yearX, int monthX, int dayX) {
        TextView tv_dateSelected = (TextView) dialogView.findViewById(R.id.tv_dateSelected);
        if (tv_dateSelected == null) {
            tv_dateSelected = (TextView) updateHabitView.findViewById(R.id.tv_dateSelected);
        }
        cleanDate = calculations.cleanDate(dayX, monthX, yearX);
        tv_dateSelected.setText(cleanDate);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minuteX) {
        TextView tv_timeSelected = (TextView) dialogView.findViewById(R.id.tv_timeSelected);
        if (tv_timeSelected == null) {
            tv_timeSelected = (TextView) updateHabitView.findViewById(R.id.tv_timeSelected);
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
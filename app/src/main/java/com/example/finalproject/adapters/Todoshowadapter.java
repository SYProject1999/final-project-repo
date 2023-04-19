package com.example.finalproject.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.finalproject.R;
import com.example.finalproject.models.TodoTaskModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class Todoshowadapter extends ArrayAdapter {
    ArrayList<TodoTaskModel> todoTaskModelArrayList=new ArrayList<>();

    public Todoshowadapter(@NonNull Context context, int resource, ArrayList<TodoTaskModel> todoTaskModelArrayList) {
        super(context, resource,todoTaskModelArrayList);
        this.todoTaskModelArrayList=todoTaskModelArrayList;
    }

    @Nullable
    @Override
    public  TodoTaskModel getItem(int position) {
        return todoTaskModelArrayList.get(position);
    }
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView= LayoutInflater.from(getContext()).inflate(R.layout.main_todo_show_layout,parent,false);




        TodoTaskModel todoTaskModel=getItem(position);
        TextView task_title=convertView.findViewById(R.id.task_title);
        TextView task_date=convertView.findViewById(R.id.task_date);
        task_title.setText(todoTaskModel.getTitle());
        task_date.setText(militodate(todoTaskModel.getDuedatetime()));
        ImageView favorite=convertView.findViewById(R.id.favorite);
        LinearLayout select_task=convertView.findViewById(R.id.select_task);


        if (todoTaskModel.getIsimportant())
        {
            favorite.setImageResource(R.drawable.star_filled);
        }else{

            favorite.setImageResource(R.drawable.ic_baseline_star_border_24);
        }

        if (todoTaskModel.getIscompleted()){
            select_task.setBackgroundResource(R.drawable.selected_circle_bg);

        }else{

            select_task.setBackgroundResource(R.drawable.unselected_circle_bg);
        }


//        Boolean isimportant=false;
//        Boolean iscomplete=false;
//        TextView taskname=convertView.findViewById(R.id.step_name);
//        TextView taskdate=convertView.findViewById(R.id.step_name);
//        final Boolean[] step_selection_check = {false};
//        LinearLayout step_selection=convertView.findViewById(R.id.step_selection);
//        step_name.setText(getItem(position));
//        step_selection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (step_selection_check[0]){
//                    step_selection.setBackgroundResource(R.drawable.unselected_circle_bg);
//                    step_selection_check[0] =false;
//                }else{
//                    step_selection.setBackgroundResource(R.drawable.selected_circle_bg);
//                    step_selection_check[0] =true;
//                }
//            }
//        });
        return convertView;
    }
    private String militodate(long milliSeconds){
        String dateFormat="dd/MM/yyyy hh:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        Log.d("TAG", "milli: "+formatter.format(calendar.getTime()));
        return formatter.format(calendar.getTime());
    }
}
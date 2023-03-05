package com.example.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.finalproject.R;

import java.util.ArrayList;

public class TodoListStepAdapter extends ArrayAdapter {
    ArrayList<String> steps=new ArrayList();

    public TodoListStepAdapter(@NonNull Context context, int resource,  ArrayList<String> steps) {
        super(context, resource,steps);
        this.steps=steps;
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return steps.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView= LayoutInflater.from(getContext()).inflate(R.layout.new_step_listview_layout,parent,false);
        TextView step_name=convertView.findViewById(R.id.step_name);
        final Boolean[] step_selection_check = {false};
        LinearLayout step_selection=convertView.findViewById(R.id.step_selection);
        step_name.setText(getItem(position));
        step_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (step_selection_check[0]){
                    step_selection.setBackgroundResource(R.drawable.unselected_circle_bg);
                     step_selection_check[0] =false;
                }else{
                    step_selection.setBackgroundResource(R.drawable.selected_circle_bg);
                     step_selection_check[0] =true;
                }
            }
        });
        return convertView;
    }
}

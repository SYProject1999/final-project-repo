package com.example.finalproject.groups;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class GroupsFragment extends Fragment {

    private Toolbar toolbar;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private TabAccessorAdapter tabAccessorAdapter;

    private DatabaseReference groupsReference;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        groupsReference = FirebaseDatabase.getInstance().getReference();

        toolbar = view.findViewById(R.id.groups_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Groups");

        viewPager2 = view.findViewById(R.id.main_tabs_pager);
        tabLayout = view.findViewById(R.id.main_tabs);

        FragmentManager fragmentManager = getParentFragmentManager();
        tabAccessorAdapter = new TabAccessorAdapter(fragmentManager, getLifecycle());
        viewPager2.setAdapter(tabAccessorAdapter);
        tabLayout.addTab(tabLayout.newTab().setText("Tasks"));
        tabLayout.addTab(tabLayout.newTab().setText("Chats"));
        tabLayout.addTab(tabLayout.newTab().setText("Groups"));
        tabLayout.addTab(tabLayout.newTab().setText("Contacts"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater inflater1 = requireActivity().getMenuInflater();
        inflater1.inflate(R.menu.options_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menu_find_friends_option) {
            sendUserToFindFriendsActivity();
        }

        if (item.getItemId() == R.id.menu_create_group_option) {
            RequestNewGroup();
        }

        if (item.getItemId() == R.id.menu_settings_option) {

        }

        return true;
    }

    private void RequestNewGroup() {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enter Group Name");

        final EditText groupNameField = new EditText(requireContext());
        groupNameField.setHint("e.g Friends");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", (dialogInterface, i) -> {
            String groupName = groupNameField.getText().toString();

            if (TextUtils.isEmpty(groupName))
                Toast.makeText(requireContext(), "Please Enter Group Name", Toast.LENGTH_LONG).show();
            else {
                CreateNewGroup(groupName);
            }
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            dialogInterface.cancel();
        });

        builder.show();
    }

    private void CreateNewGroup(String groupName) {
        groupsReference.child("Groups").child(groupName).setValue("").addOnCompleteListener(task -> {
            if (task.isSuccessful())
                Toast.makeText(requireContext(), groupName + " Group is Created Successfully", Toast.LENGTH_LONG).show();
        });
    }

    private void sendUserToFindFriendsActivity() {
        Intent findFriendsIntent = new Intent(requireContext(), FindFriendsActivity.class);
        startActivity(findFriendsIntent);
    }
}
package com.example.finalproject.Onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.finalproject.BottomNavigationBarActivity;
import com.example.finalproject.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingScreensActivity extends AppCompatActivity {

    private OnboardingAdapter onboardingAdapter;
    private LinearLayout layoutOnBoardingIndicators;
    private MaterialButton buttonOnBoardingAction;

    private DatabaseReference databaseReference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_screens);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");
        assert user != null;
        userID = user.getUid();

        layoutOnBoardingIndicators = findViewById(R.id.layoutOnboardingIndicators);
        buttonOnBoardingAction = findViewById(R.id.buttonOnboardingAction);

        setOnBoardingAdapter();
        ViewPager2 onBoardingViewPager = findViewById(R.id.onboardingViewPager);
        onBoardingViewPager.setAdapter(onboardingAdapter);
        setLayoutOnBoardingIndicators();
        setCurrentOnBoardingIndicator(0);
        onBoardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnBoardingIndicator(position);
            }
        });

        buttonOnBoardingAction.setOnClickListener(view -> {
            if (onBoardingViewPager.getCurrentItem() + 1 < onboardingAdapter.getItemCount()) {
                onBoardingViewPager.setCurrentItem(onBoardingViewPager.getCurrentItem() + 1);
            } else {
                databaseReference.child(userID).child("alreadyUsedTheApp").setValue(true);
                startActivity(new Intent(getApplicationContext(), BottomNavigationBarActivity.class));
                finish();
            }
        });
    }

    private void setOnBoardingAdapter() {

        List<OnboardingItem> onBoardingItems = new ArrayList<>();

        OnboardingItem itemPayOnline = new OnboardingItem();
        itemPayOnline.setTitle("Welcome to Daily Planner!");
        itemPayOnline.setDescription("Boost your efficiency and achieve more with ProductivityMaster, your ultimate productivity companion. This powerful app is designed to streamline your tasks, organize your projects, and help you stay focused on what truly matters. With intuitive features and a user-friendly interface, ProductivityMaster is your key to unlocking your full potential. Let's get started on your journey to increased productivity!");
        itemPayOnline.setImage(R.drawable.habits);

        OnboardingItem itemPayOnline2 = new OnboardingItem();
        itemPayOnline2.setTitle("Stay Organized with Todo List");
        itemPayOnline2.setDescription("Effortlessly manage tasks and stay on top of your to-dos with our intuitive Todo List. Boost productivity and achieve your goals faster!");
        itemPayOnline2.setImage(R.drawable.todolist);

        OnboardingItem itemPayOnline3 = new OnboardingItem();
        itemPayOnline3.setTitle("Collaborate Seamlessly with Groups");
        itemPayOnline3.setDescription("Work together effortlessly! Our Groups feature enables seamless collaboration and communication within teams and projects. Create, join, or manage groups to share ideas, files, and progress updates. Stay connected and boost productivity with real-time collaboration. Teamwork has never been easier with our Groups feature!");
        itemPayOnline3.setImage(R.drawable.diary);

        OnboardingItem itemPayOnline4 = new OnboardingItem();
        itemPayOnline4.setTitle("Enhance Focus with Timer");
        itemPayOnline4.setDescription("Achieve laser-sharp focus and improved time management using our Timer feature. Set customizable timers for work, study, or any activity, and watch your productivity soar. Stay in control of your time and accomplish tasks efficiently with our Timer. Make every second count towards success!");
        itemPayOnline4.setImage(R.drawable.timer);

        onBoardingItems.add(itemPayOnline);
        onBoardingItems.add(itemPayOnline2);
        onBoardingItems.add(itemPayOnline3);
        onBoardingItems.add(itemPayOnline4);

        onboardingAdapter = new OnboardingAdapter(onBoardingItems);
    }

    private void setLayoutOnBoardingIndicators() {

        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.onboarding_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnBoardingIndicators.addView(indicators[i]);
        }
    }
    
    @SuppressLint("SetTextI18n")
    private void setCurrentOnBoardingIndicator(int index) {

        int childCount = layoutOnBoardingIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutOnBoardingIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_active)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive)
                );
            }
        }

        if (index == onboardingAdapter.getItemCount() - 1) {
            buttonOnBoardingAction.setText("Start");
        } else {
            buttonOnBoardingAction.setText("Next");
        }
    }
}
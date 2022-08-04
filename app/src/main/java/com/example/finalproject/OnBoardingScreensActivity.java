package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
        itemPayOnline.setTitle("Pay Your");
        itemPayOnline.setDescription("Electric bill");
        itemPayOnline.setImage(R.drawable.todolist);

        OnboardingItem itemPayOnline2 = new OnboardingItem();
        itemPayOnline2.setTitle("Pay Your");
        itemPayOnline2.setDescription("Electric bill");
        itemPayOnline2.setImage(R.drawable.habits);

        OnboardingItem itemPayOnline3 = new OnboardingItem();
        itemPayOnline3.setTitle("Pay Your");
        itemPayOnline3.setDescription("Electric bill");
        itemPayOnline3.setImage(R.drawable.diary);

        OnboardingItem itemPayOnline4 = new OnboardingItem();
        itemPayOnline4.setTitle("Pay Your");
        itemPayOnline4.setDescription("Electric bill");
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
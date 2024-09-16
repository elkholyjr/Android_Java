package com.deyaa.metroappv1;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.deyaa.metroappv1.databinding.ActivityNavigationBinding;

public class NavigationActivity extends AppCompatActivity {

    private ActivityNavigationBinding binding;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewPager = binding.viewPager;
        viewPager.setAdapter(new ViewPagerAdapter(this));

        // Link ViewPager2 with BottomNavigationView
        binding.navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                viewPager.setCurrentItem(0);
                return true;
            } else {
                viewPager.setCurrentItem(1);
                return true;
            }
        });

        // Sync ViewPager2 with BottomNavigationView
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    binding.navView.setSelectedItemId(R.id.navigation_home);
                } else if (position == 1) {
                    binding.navView.setSelectedItemId(R.id.navigation_lines);
                }
            }
        });
    }
}

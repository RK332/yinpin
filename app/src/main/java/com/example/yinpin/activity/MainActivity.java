package com.example.yinpin.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.yinpin.R;
import com.example.yinpin.fragment.CartFragment;
import com.example.yinpin.fragment.MainFragment;
import com.example.yinpin.fragment.MeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Fragment> fragments;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        fragments = new ArrayList<>();
        fragments.add(new MainFragment());
        fragments.add(new CartFragment());
        fragments.add(new MeFragment());

        // 默认显示首页
        currentFragment = fragments.get(0);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main, currentFragment)
                .commit();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment targetFragment = null;
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    targetFragment = fragments.get(0);
                } else if (itemId == R.id.cart) {
                    targetFragment = fragments.get(1);
                } else if (itemId == R.id.me) {
                    targetFragment = fragments.get(2);
                }
                
                if (targetFragment != null && targetFragment != currentFragment) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    if (targetFragment.isAdded()) {
                        transaction.hide(currentFragment).show(targetFragment);
                    } else {
                        transaction.hide(currentFragment).add(R.id.main, targetFragment);
                    }
                    transaction.commit();
                    currentFragment = targetFragment;
                    
                    // 切换到购物车时刷新数据
                    if (targetFragment instanceof CartFragment) {
                        ((CartFragment) targetFragment).refreshCart();
                    }
                }
                return true;
            }
        });
        navigation.setSelectedItemId(R.id.home);
    }
}
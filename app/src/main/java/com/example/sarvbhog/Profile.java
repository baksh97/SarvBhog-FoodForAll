package com.example.sarvbhog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.sarvbhog.Adapters.MyAdapter;
import com.example.sarvbhog.fragments.Sh1_fragment;
import com.example.sarvbhog.fragments.Sh2_fragment;
import com.example.sarvbhog.fragments.info_fragment;
import com.google.android.material.tabs.TabLayout;

public class Profile extends AppCompatActivity {

    private MyAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        adapter = new MyAdapter(getSupportFragmentManager());
        adapter.addFragment(new info_fragment(), "Info");
        adapter.addFragment(new Sh1_fragment(), "Requests Served");
        adapter.addFragment(new Sh2_fragment(), "Producers Connected");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}

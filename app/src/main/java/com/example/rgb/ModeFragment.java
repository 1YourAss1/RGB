package com.example.rgb;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class ModeFragment extends Fragment {
    private static final String ARG_MODE = "param_mode", ARG_RGB = "param_rgb", ARG_HSV = "param_hsv", ARG_INTERVAL = "param_interval", ARG_STEP = "param_step";
    private int[] RGB, HSV;
    private int mode, interval, step;

    ModeFragment(){}

    static ModeFragment newInstance(int param_mode, int[] param_rgb, int[] param_hsv, int param_interval, int param_step){
        ModeFragment modeFragment = new ModeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MODE, param_mode);
        args.putIntArray(ARG_RGB, param_rgb);
        args.putIntArray(ARG_HSV, param_hsv);
        args.putInt(ARG_INTERVAL, param_interval);
        args.putInt(ARG_STEP, param_step);
        modeFragment.setArguments(args);
        return modeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = getArguments();
            mode = args.getInt(ARG_MODE);
            RGB = args.getIntArray(ARG_RGB);
            HSV = args.getIntArray(ARG_HSV);
            interval = args.getInt(ARG_INTERVAL);
            step = args.getInt(ARG_STEP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mode, container, false);

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new TabsAdapter(getChildFragmentManager()));

        // Настройка вкладок - режимов
        TabLayout tabs = view.findViewById(R.id.tab_layout);
        tabs.setupWithViewPager(viewPager);
        Objects.requireNonNull(tabs.getTabAt(mode - 1)).select();

        // При переключении владок, переключать режим работы - отправлять соотвествующие значения на Arduino
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                SendPackage activity = (SendPackage) getActivity();
                assert activity != null;
                switch (tab.getPosition()){
                    case 0:
                        activity.sendPackage(1, RGB);
                        break;
                    case 1:
                        activity.sendPackage(2, HSV);
                        break;
                    case 2:
                        activity.sendPackage(3, new int[]{interval, step, 0});
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }


    public class TabsAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 4;
        private String[] tabTitles = new String[] {"RGB", "HSV", "GRADIENT", "FAVORITE"};

        TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return RGBFragment.newInstance(RGB);
                case 1:
                    return HSVFragment.newInstance(HSV);
                case 2:
                    return GradientFragment.newInstance(interval, step);
                case 3:
                    return new FavoriteFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }
}



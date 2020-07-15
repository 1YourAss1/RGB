package com.example.rgb;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class GradientFragment extends Fragment {

    private static final String ARG_INTERVAL = "param_interval", ARG_STEP = "param_step";
    private static final int step_of_step_seekbar = 5, max_of_step_seekbar = 100, max_of_interval_seekbar = 5000, step_of_interval_seekbar = 100;
    private int interval, step;
    private TextView intervalTextView, stepTextView;
    private SendPackage activity;

    public GradientFragment() {}

    public static GradientFragment newInstance(int param_interval, int param_step) {
        GradientFragment fragment = new GradientFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INTERVAL, param_interval);
        args.putInt(ARG_STEP, param_step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = getArguments();
            interval = args.getInt(ARG_INTERVAL);
            step = args.getInt(ARG_STEP);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gradient, container, false);
        activity = (SendPackage) getActivity();

        intervalTextView = view.findViewById(R.id.intervalTextView);
        intervalTextView.setText(String.valueOf(interval));

        // SeekBar для интервала с шагом step_of_interval_seekbar = 100
        SeekBar intervalSeekBar = view.findViewById(R.id.intervalSeekBar);
        intervalSeekBar.setMax(max_of_interval_seekbar / step_of_interval_seekbar);
        intervalSeekBar.setProgress(interval / step_of_interval_seekbar);

        intervalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                interval = progress * step_of_interval_seekbar;
                assert activity != null;
                activity.sendPackage(3, new int[]{interval, step, 0});
                intervalTextView.setText(String.valueOf(interval));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        stepTextView = view.findViewById(R.id.stepTextView);
        stepTextView.setText(String.valueOf(step));

        // SeekBar для шага с шагом step_of_step_seekbar = 5
        SeekBar stepSeekBar = view.findViewById(R.id.stepSeekBar);
        stepSeekBar.setMax(max_of_step_seekbar / step_of_step_seekbar);
        stepSeekBar.setProgress(step / step_of_step_seekbar);

        stepSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                step = progress == 0 ? 1 : progress * step_of_step_seekbar;
                assert activity != null;
                activity.sendPackage(3, new int[]{interval, step, 0});
                stepTextView.setText(String.valueOf(step));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return view;
    }
}
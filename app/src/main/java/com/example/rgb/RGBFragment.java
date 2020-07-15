package com.example.rgb;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class RGBFragment extends Fragment {
    private static final String ARG_RGB = "param_rgb";
    private int[] RGB;
    private TextView rTextView, gTextView, bTextView;

    public RGBFragment() {}

    static RGBFragment newInstance(int[] param) {
        RGBFragment rgbFragment = new RGBFragment();
        Bundle args = new Bundle();
        args.putIntArray(ARG_RGB, param);
        rgbFragment.setArguments(args);
        return rgbFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            RGB = getArguments().getIntArray(ARG_RGB);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rgb, container, false);
        final SendPackage activity = (SendPackage) getActivity();

        // SeekBar для R - красной составляющей
        rTextView = view.findViewById(R.id.rTextView);
        rTextView.setText(String.valueOf(RGB[0]));

        SeekBar rSeekBar = view.findViewById(R.id.rSeekBar);
        rSeekBar.setProgress(RGB[0]);
        rSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                RGB[0] = progress;
                assert activity != null;
                activity.sendPackage(1, RGB);
                rTextView.setText(String.valueOf(RGB[0]));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // SeekBar для G - зеленой составляющей
        gTextView = view.findViewById(R.id.gTextView);
        gTextView.setText(String.valueOf(RGB[1]));

        SeekBar gSeekBar = view.findViewById(R.id.gSeekBar);
        gSeekBar.setProgress(RGB[1]);
        gSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                RGB[1] = progress;
                assert activity != null;
                activity.sendPackage(1, RGB);
                gTextView.setText(String.valueOf(RGB[1]));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // SeekBar для B - синей составляющей
        bTextView = view.findViewById(R.id.bTextView);
        bTextView.setText(String.valueOf(RGB[2]));

        SeekBar bSeekBar = view.findViewById(R.id.bSeekBar);
        bSeekBar.setProgress(RGB[2]);
        bSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                RGB[2] = progress;
                assert activity != null;
                activity.sendPackage(1, RGB);
                bTextView.setText(String.valueOf(RGB[2]));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Кнопка "Добавить в избранное"
        Button buttonAddRGBToFavorite = view.findViewById(R.id.buttonAddRGBToFavorite);
        buttonAddRGBToFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText colorNameInput = new EditText(getContext());
                Context context = getContext();
                if (context != null) {
                    new AlertDialog.Builder(context)
                            .setTitle("Добавить в избранное")
                            .setMessage("Введите название цвета:")
                            .setView(colorNameInput)
                            .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Загрузка БД
                                    FavoriteColorDBHelper favoriteColorDBHelper = new FavoriteColorDBHelper(getContext(), "colors", null, 1);
                                    SQLiteDatabase db = favoriteColorDBHelper.getWritableDatabase();

                                    // Составление набора данных для добавления
                                    ContentValues contentValues = new ContentValues();
                                    String colorName = colorNameInput.getText().toString();
                                    contentValues.put("name", colorName);
                                    contentValues.put("mode", 1);
                                    contentValues.put("parameter1", RGB[0]);
                                    contentValues.put("parameter2", RGB[1]);
                                    contentValues.put("parameter3", RGB[2]);

                                    // Добавление строки в БД
                                    db.insert("favorite_colors", null, contentValues);
                                    Toast.makeText(getContext(), "Добавлено в избранное", Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            }
        });

        return view;
    }
}

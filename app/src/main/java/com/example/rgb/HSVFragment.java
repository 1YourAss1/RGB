package com.example.rgb;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
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

public class HSVFragment extends Fragment {
    private static final String ARG_HSV = "param_hsv";
    private int[] HSV;
    private TextView hTextView, sTextView, vTextView;
    private GradientDrawable sGradientDrawable, vGradientDrawable, hThumb, sThumb, vThumb;
    private SeekBar hSeekBar, sSeekBar, vSeekBar;
    private SendPackage activity;

    public HSVFragment() {}

    static HSVFragment newInstance(int[] param) {
        HSVFragment hsvFragment = new HSVFragment();
        Bundle args = new Bundle();
        args.putIntArray(ARG_HSV, param);
        hsvFragment.setArguments(args);
        return hsvFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            HSV = getArguments().getIntArray(ARG_HSV);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hsv, container, false);

        activity = (SendPackage) getActivity();

        // SeekBar для H - тональной составляющей
        hTextView = view.findViewById(R.id.hTextView);
        hTextView.setText(String.valueOf(HSV[0]));

        hSeekBar = view.findViewById(R.id.hSeekBar);

        GradientDrawable hGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] {0xFFFF0000, 0xFFFFFF00, 0xFF00FF00, 0xFF00FFFF, 0xFF0000FF, 0xFFFF00FF, 0xFFFF0000});
        hGradientDrawable.setCornerRadius(20);

        hSeekBar.setProgressDrawable(hGradientDrawable);

        hThumb = (GradientDrawable) hSeekBar.getThumb();
        hThumb.setColor(Color.HSVToColor(new float[]{map(HSV[0], 0, 255, 0, 360),1,1}));
        hSeekBar.setThumb(hThumb);

        hSeekBar.setProgress(HSV[0]);

        // SeekBar для S - составляющей насыщенности
        sTextView = view.findViewById(R.id.sTextView);
        sTextView.setText(String.valueOf(HSV[1]));

        sSeekBar = view.findViewById(R.id.sSeekBar);

        sGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] {0xFFFFFFFF, Color.HSVToColor(new float[]{map(HSV[0], 0, 255, 0, 360),1,1})});
        sGradientDrawable.setCornerRadius(20);

        sSeekBar.setProgressDrawable(sGradientDrawable);

        sThumb = (GradientDrawable) sSeekBar.getThumb();
        sThumb.setColor(Color.HSVToColor(new float[]{map(HSV[0], 0, 255, 0, 360),1,1}));
        sSeekBar.setThumb(sThumb);

        sSeekBar.setProgress(HSV[1]);

        // SeekBar для V - составляющей значения
        vTextView = view.findViewById(R.id.vTextView);
        vTextView.setText(String.valueOf(HSV[2]));

        vSeekBar = view.findViewById(R.id.vSeekBar);

        vGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] {0xFF000000, Color.HSVToColor(new float[]{map(HSV[0], 0, 255, 0, 360),1,1})});
        vGradientDrawable.setCornerRadius(20);

        vSeekBar.setProgressDrawable(vGradientDrawable);

        vThumb = (GradientDrawable) vSeekBar.getThumb();
        vThumb.setColor(Color.HSVToColor(new float[]{map(HSV[0], 0, 255, 0, 360),1,1}));
        vSeekBar.setThumb(vThumb);

        vSeekBar.setProgress(HSV[2]);

        // Изменение hSeekBar
        hSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Change Hue SeekBar
                float V = map(progress, 0, 255, 0, 360);
                hThumb.setColor(Color.HSVToColor(new float[]{V,1,1}));
                seekBar.setThumb(hThumb);

                // Change Saturation SeekBar
                sGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[] {0xFFFFFFFF, Color.HSVToColor(new float[]{V,1,1})});
                sGradientDrawable.setCornerRadius(20);
                sSeekBar.setProgressDrawable(sGradientDrawable);
                sThumb.setColor(Color.HSVToColor(new float[]{V,1,1}));
                sSeekBar.setThumb(sThumb);

                // Change Value SeekBar
                vGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[] {0xFF000000, Color.HSVToColor(new float[]{V,1,1})});
                vGradientDrawable.setCornerRadius(20);
                vSeekBar.setProgressDrawable(vGradientDrawable);
                vThumb.setColor(Color.HSVToColor(new float[]{V,1,1}));
                vSeekBar.setThumb(vThumb);

                // Send data
                HSV[0] = progress;
                assert activity != null;
                activity.sendPackage(2, HSV);
                hTextView.setText(String.valueOf(HSV[0]));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Изменение sSeekBar
        sSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                HSV[1] = progress;
                assert activity != null;
                activity.sendPackage(2, HSV);
                sTextView.setText(String.valueOf(HSV[1]));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Изменение vSeekBar
        vSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                HSV[2] = progress;
                assert activity != null;
                activity.sendPackage(2, HSV);
                vTextView.setText(String.valueOf(HSV[2]));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Кнопка "Добавить в избранное"
        Button buttonAddHSVToFavorite = view.findViewById(R.id.buttonAddHSVToFavorite);
        buttonAddHSVToFavorite.setOnClickListener(new View.OnClickListener() {
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
                                    contentValues.put("mode", 2);
                                    contentValues.put("parameter1", HSV[0]);
                                    contentValues.put("parameter2", HSV[1]);
                                    contentValues.put("parameter3", HSV[2]);

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

    // Функция изменения диапозона
    private float map(float x, float in_min, float in_max, float out_min, float out_max) {
        return ((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);
    }
}
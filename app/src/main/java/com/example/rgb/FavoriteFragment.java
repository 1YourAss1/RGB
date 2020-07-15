package com.example.rgb;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment implements FavoriteColorAdapter.OnFavoriteColorListener {
    private RecyclerView colorsRecyclerView;
    private SendPackage activity;
    private List<FavoriteColor> favoriteColorsList;
    private FavoriteColorAdapter favoriteColorAdapter;
    private FavoriteColorDBHelper favoriteColorDBHelper;
    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        activity = (SendPackage) getActivity();

        // Чтение БД избранных цветов
        favoriteColorDBHelper = new FavoriteColorDBHelper(getContext(), "colors", null, 1);
        db = favoriteColorDBHelper.getWritableDatabase();

        // Создание листа со значениями из полученной БД
        favoriteColorsList = new ArrayList<>();
        Cursor cursor = db.query("favorite_colors", null, null,null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                favoriteColorsList.add(new FavoriteColor(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getInt(cursor.getColumnIndex("mode")),
                        cursor.getInt(cursor.getColumnIndex("parameter1")),
                        cursor.getInt(cursor.getColumnIndex("parameter2")),
                        cursor.getInt(cursor.getColumnIndex("parameter3"))));
            } while (cursor.moveToNext());
        }
        cursor.close();

        colorsRecyclerView = view.findViewById(R.id.recycler_view_colors);
        favoriteColorAdapter = new FavoriteColorAdapter(favoriteColorsList, this);
        colorsRecyclerView.setAdapter(favoriteColorAdapter);

        return view;
    }

    // Реализация нажатия - отправка выбранного цвета на Arduino
    @Override
    public void onFavoriteColorClick(int position) {
        Toast.makeText(getContext(), favoriteColorsList.get(position).getColorName(), Toast.LENGTH_SHORT).show();
        assert activity != null;
        activity.sendPackage(favoriteColorsList.get(position).getMode(), favoriteColorsList.get(position).getParameters());
    }

    // Реализация длинного нажатия - удаление выбранного цвета из БД
    @Override
    public void onFavoriteColorLongClick(final int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Удалить?")
                .setMessage("Вы действительно хотите удалить \"" + favoriteColorsList.get(position).getColorName() + "\"?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        favoriteColorDBHelper = new FavoriteColorDBHelper(getContext(), "colors", null, 1);
                        db = favoriteColorDBHelper.getWritableDatabase();
                        db.delete("favorite_colors", "id=?", new String[]{Integer.toString(favoriteColorsList.get(position).getId())});

                        favoriteColorsList.remove(position);
                        if (colorsRecyclerView.getAdapter() != null) {
                            colorsRecyclerView.getAdapter().notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

    }
}
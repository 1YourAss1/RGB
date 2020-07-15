package com.example.rgb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class FavoriteColorDBHelper extends SQLiteOpenHelper {

    public FavoriteColorDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table favorite_colors ("
            + "id integer primary key autoincrement,"
            + "name text,"
            + "mode integer,"
            + "parameter1 integer,"
            + "parameter2 integer,"
            + "parameter3 integer" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
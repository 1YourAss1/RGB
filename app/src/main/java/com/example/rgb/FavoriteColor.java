package com.example.rgb;

import android.graphics.Color;

// Класс для избранных цветов
class FavoriteColor {

    private int id, mode, parameter1, parameter2, parameter3;
    private String colorName;

    FavoriteColor(int id, String colorName, int mode, int parameter1, int parameter2, int parameter3) {
        this.id = id;
        this.colorName = colorName;
        this.mode = mode;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.parameter3 = parameter3;
    }

    // ID из базы данных
    int getId() { return id; }

    // RGB = 1, HSV = 2
    int getMode(){ return mode; }

    // {R, G, B} или {H, S, V}
    int[] getParameters() { return new int[] {parameter1, parameter2, parameter3}; }

    // Название цвета
    String getColorName() { return colorName; }

    // Преобразование массива параметров в целочисленное значение цвета
    int getIntColor() {
        if (mode == 1) {
            return Color.rgb(parameter1, parameter2, parameter3);
        } else {
            float[] hsv = new float[3];
            hsv[0] = map(parameter1, 0, 360);
            hsv[1] = map(parameter2, 0, 1);
            hsv[2] = map(parameter3, 0, 1);
            return Color.HSVToColor(hsv);
        }
    }

    // Функция изменения диапозона
    private float map(float x, float out_min, float out_max) {
        return (x * (out_max - out_min) / 255f + out_min);
    }
}

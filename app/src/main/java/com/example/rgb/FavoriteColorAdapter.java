package com.example.rgb;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoriteColorAdapter extends RecyclerView.Adapter<FavoriteColorAdapter.ViewHolder> {
    private List<FavoriteColor> favoriteColors;
    private OnFavoriteColorListener mOnFavoriteColorListener;

    FavoriteColorAdapter(List<FavoriteColor> favoriteColors, OnFavoriteColorListener onFavoriteColorListener) {
        this.favoriteColors = favoriteColors;
        this.mOnFavoriteColorListener = onFavoriteColorListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_favorite, parent, false);
        return new ViewHolder(view, mOnFavoriteColorListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int color = favoriteColors.get(position).getIntColor();

        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] {0, color});
        gradientDrawable.setCornerRadius(20);
        gradientDrawable.setStroke(3, color);
        holder.colorView.setBackground(gradientDrawable);

        holder.colorNameTextView.setText(favoriteColors.get(position).getColorName());

        int mode = favoriteColors.get(position).getMode();
        if (mode == 1) {
            int[] RGB = favoriteColors.get(position).getParameters();
            holder.colorParametersTextView.setText("R: " + RGB[0] + " B: " + RGB[1] + " G: " + RGB[2]);
        } else {
            int[] HSV = favoriteColors.get(position).getParameters();
            holder.colorParametersTextView.setText("H: " + HSV[0] + " S: " + HSV[1] + " V: " + HSV[2]);
        }

    }

    @Override
    public int getItemCount() {
        return favoriteColors.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView colorNameTextView, colorParametersTextView;
        LinearLayout colorView;
        OnFavoriteColorListener onFavoriteColorListener;

        ViewHolder(@NonNull View itemView, OnFavoriteColorListener onFavoriteColorListener) {
            super(itemView);
            colorNameTextView = itemView.findViewById(R.id.color_name);
            colorParametersTextView = itemView.findViewById(R.id.color_parameters);
            colorView = itemView.findViewById(R.id.color_view);

            this.onFavoriteColorListener = onFavoriteColorListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onFavoriteColorListener.onFavoriteColorClick(getAdapterPosition());
        }


        @Override
        public boolean onLongClick(View v) {
            onFavoriteColorListener.onFavoriteColorLongClick(getAdapterPosition());
            return false;
        }
    }

    // Интерфейс слушателя нажатий и длинных нажатий
    public interface OnFavoriteColorListener{
        void onFavoriteColorClick(int position);
        void onFavoriteColorLongClick(int position);
    }

}
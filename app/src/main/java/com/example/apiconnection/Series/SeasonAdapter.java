package com.example.apiconnection.Series;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apiconnection.R;

import java.util.List;

public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.ViewHolder> {

    private List<String> seasonList;
    private OnItemClickListener clickListener;
    private int selectedSeason = -1;
    public static final int YELLOW = Color.parseColor("#FFD700"); // Yellow color

    public interface OnItemClickListener {
        void onItemClick(int seasonNumber); // Pass the season number
    }

    public SeasonAdapter(List<String> seasonList) {
        this.seasonList = seasonList;
        selectedSeason=0;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_season, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String season = seasonList.get(position);
        holder.bind(season);
    }

    @Override
    public int getItemCount() {
        return seasonList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSeason;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSeason = itemView.findViewById(R.id.tvSeason);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    int clickedSeason = Integer.parseInt(seasonList.get(position));
                    setSelectedSeason(clickedSeason - 1); // Set the selected season
                    clickListener.onItemClick(clickedSeason);
                }
            });
        }

        public void bind(String season) {
            tvSeason.setText(season);

            // Set background color based on whether this season is selected or not
            int backgroundColor = (getAdapterPosition() == selectedSeason) ?
                    YELLOW : Color.TRANSPARENT; // Use the YELLOW color constant
            tvSeason.setBackgroundColor(backgroundColor);
        }
    }

    private void setSelectedSeason(int season) {
        selectedSeason = season;
        notifyDataSetChanged(); // Refresh the list to update background colors
    }
}

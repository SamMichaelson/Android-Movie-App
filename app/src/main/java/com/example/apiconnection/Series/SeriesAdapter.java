package com.example.apiconnection.Series;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apiconnection.R;
import com.example.apiconnection.items.Image;
import com.example.apiconnection.items.SeriesItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.ViewHolder> {

    private final List<SeriesItem> seriesItems;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(SeriesItem series);
    }

    public SeriesAdapter(List<SeriesItem> seriesItems) {
        this.seriesItems = seriesItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_details, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SeriesItem seriesItem = seriesItems.get(position);

        holder.bind(seriesItem);
    }

    @Override
    public int getItemCount() {
        return seriesItems.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final ImageView ivImage;
        private final TextView plainTextTextView;
        private final TextView tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivImage = itemView.findViewById(R.id.ivImage);
            plainTextTextView = itemView.findViewById(R.id.plainTextTextView);
            tvDate = itemView.findViewById(R.id.tvDate);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    SeriesItem clickedSeries = seriesItems.get(position);
                    clickListener.onItemClick(clickedSeries);
                }
            });
        }

        @SuppressLint("SetTextI18n")
        public void bind(SeriesItem series) {
            if (series != null) {
                tvTitle.setText("S" + series.getSeasonNo() + "." + "E" + series.getEpisodeNo() + " · " + series.getTitle());

                Image image = series.getImage();
                if (image != null) {
                    String imageUrl = image.getUrl();
                    if (imageUrl != null) {
                        Picasso.get()
                                .load(imageUrl)
                                .resize(760, 480)
                                .centerInside()
                                .into(ivImage);
                    } else {
                        // Handle missing image here, like using a placeholder image
                    }

                    if (series.getImage().getCaption() != null) {
                        if (series.getImage().getCaption().getPlainText() != null) {
                            plainTextTextView.setText(series.getImage().getCaption().getPlainText());
                        } else {
                            plainTextTextView.setVisibility(View.GONE);
                        }
                    } else {
                        plainTextTextView.setVisibility(View.GONE);
                    }

                    if (series.getDate() != null) {
                        tvDate.setText(series.getDate());
                    } else {
                        tvDate.setVisibility(View.GONE);
                    }
                }
            } else {
                // Handle null SeriesItem here if necessary
            }
        }
    }
}

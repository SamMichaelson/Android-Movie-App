package com.example.apiconnection.Movie;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apiconnection.items.MovieItem;
import com.example.apiconnection.R;
import com.example.apiconnection.items.Image;
import com.squareup.picasso.Picasso;

import java.util.List;
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private final List<MovieItem> movieItems;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(MovieItem movie); // Listener method to be implemented
    }

    public MovieAdapter(List<MovieItem> movieItems) {
        this.movieItems = movieItems;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view, clickListener);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MovieItem movieItem = movieItems.get(position);

        holder.tvTitle.setText(movieItem.getTitle());
        int year = movieItem.getYear();
        holder.tvYear.setText(String.valueOf(year));

        Image image = movieItem.getImage();
        if (image != null) {
            String imageUrl = image.getUrl();
            if (imageUrl != null) {
                Picasso.get().load(imageUrl).into(holder.ivImage);
            }  // Handle missing image here, like using a placeholder image

        }  // Handle missing image and Image object


        holder.bind(movieItem);
    }



    @Override
    public int getItemCount() {
        return movieItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvYear;
        ImageView ivImage;

        public ViewHolder(@NonNull View itemView, OnItemClickListener clickListener) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvYear = itemView.findViewById(R.id.tvYear);
            ivImage = itemView.findViewById(R.id.ivImage);
            itemView.setOnClickListener(v -> {
                if (MovieAdapter.this.clickListener != null) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        MovieItem clickedMovie=movieItems.get(position);
                        MovieAdapter.this.clickListener.onItemClick(clickedMovie);
                    }
                }
            });
        }

        public void bind(MovieItem movie) {
            tvTitle.setText(movie.getTitle());
            int year = movie.getYear();
            tvYear.setText(String.valueOf(year));

            Image image = movie.getImage();
            if (image != null) {
                String imageUrl = image.getUrl();
                if (imageUrl != null) {
//                    Picasso.get().load(imageUrl).into(ivImage);
                    Picasso.get()
                            .load(imageUrl)
                            .resize(1080, 1920) // Set your target width and height here
                            .centerInside() // Or .centerCrop() depending on how you want to fit the image
                            .into(ivImage);
                }  // Handle missing image here, like using a placeholder image

            }  // Handle missing image and Image object

        }

    }
}
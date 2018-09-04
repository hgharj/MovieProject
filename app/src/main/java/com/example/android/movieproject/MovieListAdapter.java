package com.example.android.movieproject;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.movieproject.provider.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jcgray on 8/5/18.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>{

//    private Context mContext;
//    private Cursor mCursor;

//    public MovieListAdapter(Context context,Cursor cursor){
//        this.mContext=context;
//        this.mCursor=cursor;
//    }

    private final List<Movie> movies;
    private final OnItemClickListener listener;

    public MovieListAdapter(List<Movie> movies, OnItemClickListener listener){
        this.movies=movies;
        this.listener=listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(movies.get(position),listener);
//        mCursor.moveToPosition(position);
//
//        int movieListImageIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
//        int movieTitleIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
//        int releaseDateIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
//        int voteAverageIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
//        int voteDescIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_DESCRIPTION);
//        int plotIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_PLOT);
//
//        int movieImageRes = mCursor.getInt(movieListImageIndex);
//        String movieTitle = mCursor.getString(movieTitleIndex);
//        long releaseDate = mCursor.getLong(releaseDateIndex);
//        double voteAverage = mCursor.getDouble(voteAverageIndex);
//        String voteDesc = mCursor.getString(voteDescIndex);
//        String plot = mCursor.getString(plotIndex);


        /*int imgRes = PlantUtils.getPlantImageRes(mContext, timeNow - createdAt, timeNow - wateredAt, plantType);

        holder.plantImageView.setImageResource(imgRes);
        holder.plantNameView.setText(String.valueOf(plantId));
        holder.plantImageView.setTag(plantId);
        */
    }

//    public void swapCursor(Cursor newCursor) {
//        if (mCursor != null) {
//            mCursor.close();
//        }
//        mCursor = newCursor;
//        if (mCursor != null) {
//            // Force the RecyclerView to refresh
//            this.notifyDataSetChanged();
//        }
//    }

    public void clear(){
       movies.clear();
    }

    public void addAll(List<Movie> movies){
        this.movies.addAll(movies);
    }

    @Override
    public int getItemCount() {
//        if (mCursor == null) return 0;
//        return mCursor.getCount();
        return movies.size();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        View view=inflater.inflate(R.layout.list_item,parent,false);
//        return new MovieViewHolder(view);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MovieViewHolder(v);
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder{
        private ImageView movieImg;
        private TextView title;
        private TextView plot;
        private TextView releaseDate;
        private RatingBar voteAvg;

        public MovieViewHolder(View v){
            super(v);
            movieImg = (ImageView) v.findViewById(R.id.movie_list_item_image);
        }
        
        public void bind(final Movie movie, final OnItemClickListener listener){
//            title.setText(movie.getMovieTitle());
//            plot.setText(movie.getPlot());
//            releaseDate.setText(movie.getReleaseDate());
//            voteAvg.setRating(movie.getVoteAverage());
            Picasso.with(itemView.getContext()).load(movie.getPosterImage())
                    .placeholder(R.drawable.imageunavailabe)
                    .error(R.drawable.imageunavailabe)
                    .into(movieImg);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(movie);
                }
            });
        }
    }
}

package com.example.android.movieproject;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movieproject.provider.MovieContract;
import com.example.android.movieproject.utils.MovieModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jcgray on 8/5/18.
 */

public class MovieCursorAdapter extends CursorAdapter{

//    private final List<MovieModel> movies;
    private final OnItemClickListener listener;

    public MovieCursorAdapter(Context context, Cursor c, OnItemClickListener listener){
        super(context, c, 0 /* flags */);
//        this.movies=movies;
        this.listener=listener;
    }

    public interface OnItemClickListener {
        void onItemClick(MovieModel movie);
    }

//    @Override
//    public void onBindViewHolder(MovieViewHolder holder, int position) {
//        holder.bind(movies.get(position),listener);
//    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

//    public void clear(){
//       movies.clear();
//    }
//
//    public void addAll(List<MovieModel> movies){
//        this.movies.addAll(movies);
//    }
//
//    @Override
//    public int getItemCount() {
//        return movies.size();
//    }

//    @Override
//    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
//        return new MovieViewHolder(v);
//    }
//
//    static class MovieViewHolder extends RecyclerView.ViewHolder{
//        @BindView(R.id.movie_list_item_image) ImageView movieImg;
//
//        public MovieViewHolder(View v){
//            super(v);
//            ButterKnife.bind(this, v);
//        }
//
//        public void bind(final MovieModel movie, final OnItemClickListener listener){
//            Picasso.with(itemView.getContext()).load(movie.getPosterUrl())
//                    .placeholder(R.drawable.imageunavailabe)
//                    .error(R.drawable.imageunavailabe)
//                    .into(movieImg);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onItemClick(movie);
//                }
//            });
//        }
//    }

    @BindView(R.id.movie_list_item_image) ImageView movieImg;

    @Override
    public void bindView(final View view, Context context, Cursor cursor) {
        ButterKnife.bind(this, view);

        int movieIdIndex = cursor.getColumnIndex(MovieContract.MovieEntry._ID);
        int movieTitleIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
        int moviePosterIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
        int releaseDateIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        int voteAverageIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        int overviewIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);

        Long movieId = cursor.getLong(movieIdIndex);
        String movieTitle = cursor.getString(movieTitleIndex);
        String movieImgRes = cursor.getString(moviePosterIndex);
        String releaseDate = cursor.getString(releaseDateIndex);
        float voteAverage = cursor.getFloat(voteAverageIndex);
        String overview = cursor.getString(overviewIndex);

        final MovieModel movie = new MovieModel(movieId,movieTitle,movieImgRes,releaseDate,voteAverage,overview);

        Picasso.with(view.getContext()).load(movieImgRes)
                .placeholder(R.drawable.imageunavailabe)
                .error(R.drawable.imageunavailabe)
                .into(movieImg);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(movie);
            }
        });
    }
}

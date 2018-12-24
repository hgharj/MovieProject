package com.example.android.movieproject;

/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movieproject.R;
import com.example.android.movieproject.provider.MovieContract;
import com.example.android.movieproject.provider.MovieContract.MovieEntry;
import com.example.android.movieproject.utils.MovieModel;
import com.example.android.movieproject.utils.MovieUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteMovieListAdapter extends RecyclerView.Adapter<FavoriteMovieListAdapter.FavoriteMovieViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    /**
     * Constructor using the context and the db mCursor
     *
     * @param context the calling context/activity
     */
    public FavoriteMovieListAdapter(Context context, Cursor mCursor) {
        this.mContext = context;
        this.mCursor = mCursor;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item
     *
     * @param parent   The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new MovieViewHolder that holds a View with the plant_list_item layout
     */
    @Override
    public FavoriteMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new FavoriteMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteMovieViewHolder holder, int position) {

        mCursor.moveToPosition(position);
        int movieIdIndex = mCursor.getColumnIndex(MovieContract.MovieEntry._ID);
        int movieTitleIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
        int moviePosterIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
        int releaseDateIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        int voteAverageIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        int overviewIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);

        Long movieId = mCursor.getLong(movieIdIndex);
        String movieTitle = mCursor.getString(movieTitleIndex);
        String movieImgRes = mCursor.getString(moviePosterIndex);
        String releaseDate = mCursor.getString(releaseDateIndex);
        float voteAverage = mCursor.getFloat(voteAverageIndex);
        String overview = mCursor.getString(overviewIndex);

        final MovieModel movie = new MovieModel(movieId,movieTitle,movieImgRes,releaseDate,voteAverage,overview);

//        Picasso.with(view.getContext()).load(movieImgRes)
//                .placeholder(R.drawable.imageunavailabe)
//                .error(R.drawable.imageunavailabe)
//                .into(movieImg);
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        if (mCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    /**
     * Returns the number of items in the mCursor
     *
     * @return Number of items in the mCursor, or 0 if null
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    /**
     * MovieViewHolder class for the recycler view item
     */
    class FavoriteMovieViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_list_item_image) ImageView movieImg;

        public FavoriteMovieViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }

        public void bind(final MovieModel movie, final MovieListAdapter.OnItemClickListener listener){
            Picasso.with(itemView.getContext()).load(movie.getPosterUrl())
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


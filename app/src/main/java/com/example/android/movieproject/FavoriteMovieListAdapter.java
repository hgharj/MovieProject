package com.example.android.movieproject;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.movieproject.provider.MovieContract;
import com.example.android.movieproject.utils.MovieModel;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteMovieListAdapter extends RecyclerView.Adapter<FavoriteMovieListAdapter.FavoriteMovieViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private final OnItemClickListener listener;

    public FavoriteMovieListAdapter(Context context, Cursor cursor, OnItemClickListener listener) {
        this.mContext = context;
        this.mCursor = cursor;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(MovieModel movie);
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

        final MovieModel movie = new MovieModel(movieId, movieTitle, movieImgRes, releaseDate, voteAverage, overview);

        holder.bind(movie, new OnItemClickListener() {
            @Override
            public void onItemClick(MovieModel movie) {
                listener.onItemClick(movie);
            }
        });
    }

    public void swapCursor(Cursor newCursor) {
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
     * FavoriteMovieViewHolder class for the recycler view item
     */
    class FavoriteMovieViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movie_list_item_image)
        ImageView movieImg;

        public FavoriteMovieViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void bind(final MovieModel movie, final FavoriteMovieListAdapter.OnItemClickListener listener) {
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


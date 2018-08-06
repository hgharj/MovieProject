package com.example.android.movieproject;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by jcgray on 8/5/18.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>{

    private Context mContext;
    private Cursor mCursor;

    public MovieListAdapter(Context context,Cursor cursor){
        this.mContext=context;
        this.mCursor=cursor;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view=inflater.inflate(R.layout.list_item,parent,false);
        return new MovieViewHolder(view);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{
        private ImageView movieImg;
        public MovieViewHolder(View v){
            super(v);
            movieImg = (ImageView) v.findViewById(R.id.plot_tv);
        }
    }
}

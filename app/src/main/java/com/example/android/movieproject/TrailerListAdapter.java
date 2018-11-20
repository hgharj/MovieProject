package com.example.android.movieproject;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.movieproject.utils.TrailerModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jcgray on 8/5/18.
 */

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.TrailerViewHolder>{

    private final List<TrailerModel> trailers;
    private final OnItemClickListener listener;

    public TrailerListAdapter(List<TrailerModel> trailers, OnItemClickListener listener){
        this.trailers=trailers;
        this.listener=listener;
    }

    public interface OnItemClickListener {
        void onItemClick(TrailerModel trailer);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bind(trailers.get(position),listener);
    }

    public void clear(){
       trailers.clear();
    }

    public void addAll(List<TrailerModel> trailers){
        this.trailers.addAll(trailers);
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list_item, parent, false);
        return new TrailerViewHolder(v);
    }

    static class TrailerViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.trailer_list_item_image) ImageView trailerImg;

        public TrailerViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }
        
        public void bind(final TrailerModel trailer, final OnItemClickListener listener){
            String thumbnailBaseUrl = "https://img.youtube.com/vi/";
            String thumbnailEndUrl = "/0.jpg";
            String thumbnailUrl = thumbnailBaseUrl + trailer.getKey() + thumbnailEndUrl;

            Picasso.with(itemView.getContext()).load(thumbnailUrl)
                    .placeholder(R.drawable.imageunavailabe)
                    .error(R.drawable.imageunavailabe)
                    .into(trailerImg);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(trailer);
                }
            });
        }
    }
}

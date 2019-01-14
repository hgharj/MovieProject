package com.example.android.movieproject;

import android.content.Context;
import android.content.Intent;
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

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.TrailerViewHolder> {

    private final List<TrailerModel> trailers;
    private final OnItemClickListener listener;
    private final Context mContext;
    private static final String SHARE_TRAILER_TITLE = "Check out this trailer!";
    private static final String INTENT_TYPE = "text/plain";
    private static final String THUMBNAIL_BASE = "https://img.youtube.com/vi/";
    private static final String THUMBNAIL_END = "/0.jpg";

    public TrailerListAdapter(List<TrailerModel> trailers, Context context, OnItemClickListener listener) {
        this.trailers = trailers;
        this.listener = listener;
        this.mContext = context;
    }

    public interface OnItemClickListener {
        void onItemClick(TrailerModel trailer);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bind(trailers.get(position), listener);
        if (position != 0) {
            holder.shareImg.setVisibility(View.GONE);
        }
    }

    public void clear() {
        trailers.clear();
    }

    public void addAll(List<TrailerModel> trailers) {
        this.trailers.addAll(trailers);
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list_item, parent, false);
        return new TrailerViewHolder(v);
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.trailer_list_item_image)
        ImageView trailerImg;
        @BindView(R.id.share_image)
        ImageView shareImg;

        public TrailerViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void bind(final TrailerModel trailer, final OnItemClickListener listener) {
            String thumbnailBaseUrl = THUMBNAIL_BASE;
            String thumbnailEndUrl = THUMBNAIL_END;
            final String thumbnailUrl = thumbnailBaseUrl + trailer.getKey() + thumbnailEndUrl;

            Picasso.with(itemView.getContext()).load(thumbnailUrl)
                    .error(R.drawable.imageunavailabe)
                    .into(trailerImg);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(trailer);
                }
            });

            Picasso.with(itemView.getContext()).load(R.drawable.baseline_share_white_24)
                    .into(shareImg);

            shareImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, thumbnailUrl);
                    sendIntent.setType(INTENT_TYPE);
                    mContext.startActivity(Intent.createChooser(sendIntent, SHARE_TRAILER_TITLE));
                }
            });
        }
    }
}

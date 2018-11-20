package com.example.android.movieproject;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movieproject.utils.UserReviewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jcgray on 8/5/18.
 */

public class UserReviewListAdapter extends RecyclerView.Adapter<UserReviewListAdapter.UserReviewHolder>{

    private final List<UserReviewModel> userReviews;
    private final OnItemClickListener listener;

    public UserReviewListAdapter(List<UserReviewModel> userReviews, OnItemClickListener listener){
        this.userReviews=userReviews;
        this.listener=listener;
    }

    public interface OnItemClickListener {
        void onItemClick(UserReviewModel userReview);
    }

    @Override
    public void onBindViewHolder(UserReviewHolder holder, int position) {
        holder.bind(userReviews.get(position),listener);
    }

    public void clear(){
       userReviews.clear();
    }

    public void addAll(List<UserReviewModel> userReviews){
        this.userReviews.addAll(userReviews);
    }

    @Override
    public int getItemCount() {
        return userReviews.size();
    }

    @Override
    public UserReviewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_review_list_item, parent, false);
        return new UserReviewHolder(v);
    }

    static class UserReviewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.user_review_author_list_item) TextView userReviewAuthor_tv;
        @BindView(R.id.user_review_content_list_item) TextView userReviewContent_tv;

        public UserReviewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }
        
        public void bind(final UserReviewModel userReview, final OnItemClickListener listener){
            userReviewContent_tv.setText(userReview.getContent());
            userReviewAuthor_tv.setText(userReview.getAuthor());
        }
    }
}

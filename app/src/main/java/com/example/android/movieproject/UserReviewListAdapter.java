package com.example.android.movieproject;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.example.android.movieproject.utils.UserReviewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jcgray on 8/5/18.
 */

public class UserReviewListAdapter extends RecyclerView.Adapter<UserReviewListAdapter.UserReviewHolder> {
    private final List<UserReviewModel> userReviews;
    private static final String READ_MORE = "READ MORE";
    private static final String READ_LESS = "READ LESS";
    private static final String BREAKPOINT = "<br/>";

    public UserReviewListAdapter(List<UserReviewModel> userReviews) {
        this.userReviews = userReviews;
    }

    @Override
    public void onBindViewHolder(UserReviewHolder holder, int position) {
        holder.bind(userReviews.get(position));
    }

    public void clear() {
        userReviews.clear();
    }

    public void addAll(List<UserReviewModel> userReviews) {
        this.userReviews.addAll(userReviews);
    }

    @Override
    public int getItemCount() {
        return userReviews.size();
    }

    @Override
    public UserReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_review_list_item, parent, false);
        return new UserReviewHolder(v);
    }

    static class UserReviewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_review_author_list_item)
        TextView userReviewAuthor_tv;
        @BindView(R.id.user_review_content_list_item)
        TextView userReviewContent_tv;

        public UserReviewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void bind(final UserReviewModel userReview) {
            userReviewContent_tv.setText(userReview.getContent());
            resizeTextView(userReviewContent_tv, 3, READ_MORE, true);
            userReviewAuthor_tv.setText(userReview.getAuthor());
        }

        public static void resizeTextView(final TextView tv, final int maxLine, final String expandText, final boolean readMore) {
            if (tv.getTag() == null) {
                tv.setTag(tv.getText());
            }

            ViewTreeObserver vto = tv.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    String text;
                    int lineEndIndex;
                    ViewTreeObserver obs = tv.getViewTreeObserver();
                    obs.removeOnGlobalLayoutListener(this);
                    if (maxLine == 0) {
                        lineEndIndex = tv.getLayout().getLineEnd(0);
                        text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + "\n" + expandText;
                    } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                        lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                        text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + "\n" + expandText;
                    } else if (maxLine > 0 && tv.getLineCount() > 0 && tv.getLineCount() < maxLine) {
                        lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                        text = tv.getText().subSequence(0, lineEndIndex).toString();
                    } else {
                        lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                        text = tv.getText().subSequence(0, lineEndIndex) + "\n" + expandText;
                    }
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString().replace("\n", BREAKPOINT) + "\n"), tv, lineEndIndex, expandText,
                                    readMore), TextView.BufferType.SPANNABLE);
                }
            });
        }

        private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                                final int maxLine, final String spanableText, final boolean readMore) {
            String str = strSpanned.toString();
            SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

            if (str.contains(spanableText)) {
                ssb.setSpan(new ClickableSpan() {
                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                        ds.setColor(Color.BLUE);
                        ds.setFakeBoldText(true);
                    }

                    @Override
                    public void onClick(View widget) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        if (readMore) {
                            resizeTextView(tv, -1, READ_LESS, false);
                        } else {
                            resizeTextView(tv, 3, READ_MORE, true);
                        }
                    }
                }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);
            }
            return ssb;
        }
    }
}

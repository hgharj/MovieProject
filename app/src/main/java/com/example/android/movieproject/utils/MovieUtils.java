package com.example.android.movieproject.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.View;

import com.example.android.movieproject.R;
import com.squareup.picasso.Picasso;

/**
 * Created by jcgray on 8/26/18.
 */

public class MovieUtils {
    public static int getPlantImgRes(Context context, String imgPath, View view) {
        Resources res = context.getResources();
        return 0; //context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
    }

    public static String getReleaseDateAsString(Context context, long longDate){

        return "";
    }
}

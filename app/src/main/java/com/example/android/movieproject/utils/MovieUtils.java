package com.example.android.movieproject.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import com.example.android.movieproject.R;

/**
 * Created by jcgray on 8/26/18.
 */

public class MovieUtils {
    public static int getPlantImgRes(Context context) {
        Resources res = context.getResources();
        return context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
    }
}
n
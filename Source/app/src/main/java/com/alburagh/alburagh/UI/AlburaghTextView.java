package com.alburagh.alburagh.UI;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.alburagh.alburagh.Helpers.AppHelper;

import java.util.Locale;
import java.util.Objects;

public class AlburaghTextView extends TextView {
    public AlburaghTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Locale locale = getResources().getConfiguration().locale;
        if (AppHelper.LANGUAGE.equals("fa"))
            this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/GE_SS_Two_Light.ttf"));
        else
            this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/GE_SS_Two_Light.otf"));

        int color = this.getCurrentTextColor();
        this.setTextColor(Color.rgb(Color.red(color), Color.green(color), Color.blue(color)));
    }
}
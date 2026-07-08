package com.alburagh.alburagh.UI;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;

import com.alburagh.alburagh.Helpers.AppHelper;

import java.util.Locale;

public class AlburaghEditText extends EditText {
    public AlburaghEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (AppHelper.LANGUAGE.equals("fa")) {
            this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/GE_SS_Two_Light.ttf"));

        }
        else
            this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/GE_SS_Two_Light.otf"));

        int color = this.getCurrentTextColor();
        this.setTextColor(Color.rgb(Color.red(color), Color.green(color), Color.blue(color)));

        this.setGravity(Gravity.LEFT);
    }
}

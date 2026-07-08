package com.alburagh.alburagh.Helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alburagh.alburagh.R;

public class UIHelper {

    static public Point getDrawableSize(Context context, int id) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), id, o);
        return new Point(bmp.getWidth(), bmp.getHeight());
    }

    public static class ScreenOptions {
        public Context context;
        public float imageWidth = 1920.0f;
        public float imageHeight = 1080.0f;
        public float width;
        public float height;
        public float ratio;
        public float widthOffset;
        public float heightOffset;

        public ScreenOptions (Context context, View view) {
            this.context = context;

            width = view.getWidth();
            height = view.getHeight();

            ratio = height / imageHeight;
            widthOffset = ((imageWidth * ratio) - width) / 2;
            heightOffset = ((imageHeight * ratio) - height) / 2;
        }

        public ScreenOptions(Context context, View view, int width, int height) {
            this.context = context;

            this.width = view.getWidth();
            this.height = view.getHeight();

            ratio = height / imageHeight;
            widthOffset = ((imageWidth * ratio) - width) / 2;
            heightOffset = ((imageHeight * ratio) - height) / 2;
        }
    }

    public static void fitViewToBackground(ScreenOptions so, View view, Point center) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.leftMargin = (int) ((center.x * so.ratio - (layoutParams.width / 2)) - so.widthOffset);
        layoutParams.topMargin = (int) (center.y * so.ratio - (layoutParams.height / 2));
        view.setLayoutParams(layoutParams);
    }

    public static void fitViewToBackground(ScreenOptions so, int id, View view, Point center, float scale) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        Point size = getDrawableSize(so.context, id);
        layoutParams.width = (int) (size.x * 2 * so.ratio * scale);
        layoutParams.height = (int) (size.y * 2 * so.ratio * scale);
        fitViewToBackground(so, view, center);
    }

    public static void fitViewToBackground(ScreenOptions so, View view, int width, int height, Point center, float scale) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.width = (int) (width * 2 * so.ratio * scale);
        layoutParams.height = (int) (height * 2 * so.ratio * scale);
        fitViewToBackground(so, view, center);
    }

    public static void fitImageToBackground(ScreenOptions so, View view, int id, int x, int y) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.width = (int) (getDrawableSize(so.context, id).x * 2 * so.ratio);
        layoutParams.height = (int) (getDrawableSize(so.context, id).y * 2 * so.ratio);
        layoutParams.setMargins((int) ((x * so.ratio) - so.widthOffset), (int) (y * so.ratio), 0, 0);
        view.setLayoutParams(layoutParams);
    }

    public static void fitLayoutToBackground(ScreenOptions so, View view, int width, int height, int x, int y) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.width = (int) (width * so.ratio);
        layoutParams.height = (int) (height * so.ratio);
        layoutParams.setMargins((int) ((x * so.ratio) - so.widthOffset), (int) ((y * so.ratio) - so.heightOffset), 0, 0);
        view.setLayoutParams(layoutParams);
    }

    public static void fitSize(ScreenOptions so, View view, int id) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.width = (int) (getDrawableSize(so.context, id).x * 2 * so.ratio);
        layoutParams.height = (int) (getDrawableSize(so.context, id).y * 2 * so.ratio);
        view.setLayoutParams(layoutParams);
    }

    public static void fitSize(ScreenOptions so, View view, int width, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) (width * 2 * so.ratio);
        layoutParams.height = (int) (height * 2 * so.ratio);
        view.setLayoutParams(layoutParams);
    }

    public static void setupButtonEffects(ImageButton button) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                ImageButton button = (ImageButton) view;
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    button.setColorFilter(Color.argb(164, 0, 0, 0));
                else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                    button.setColorFilter(Color.alpha(0));

                return false;
            }
        });
    }

    public static void setupAccountBuy(final Button button, final int textColor) {
        button.setTextColor(textColor);

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    button.setTextColor(Color.argb(64, Color.red(textColor), Color.green(textColor), Color.blue(textColor)));
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    button.setTextColor(textColor);

                return false;
            }
        });
    }

    public static void setupAccountButton(final Button button, final int backgroundColor, final int textColor) {
        button.setTextColor(textColor);
        button.setBackgroundResource(R.drawable.account_button_bg);
        final GradientDrawable drawable = (GradientDrawable) button.getBackground();
        drawable.setColor(backgroundColor);

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    button.setTextColor(Color.argb(64, Color.red(textColor), Color.green(textColor), Color.blue(textColor)));
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    button.setTextColor(textColor);

                return false;
            }
        });
    }

    public static void setupAccountTextButton(final TextView textView) {
        textView.setTextColor(Color.BLUE);
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    textView.setTextColor(Color.argb(64, 0, 0, 256));
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    textView.setTextColor(Color.BLUE);

                return false;
            }
        });
    }

    public static void setupAccountTabBackground(View view, int strokeColor) {
        view.setBackgroundResource(R.drawable.account_tab_bg);
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        drawable.setStroke(10, strokeColor);
    }

    public static void setupAccountTabButton(View view, int backgroundColor) {
        if (view instanceof Button)
            ((Button) view).setTextColor(Color.BLACK);

        view.setBackgroundResource(R.drawable.account_button_tab_bg);
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        drawable.setColor(backgroundColor);
    }

    public static Point intArrayToPoint(Context context, int id) {
        int[] array = context.getResources().getIntArray(id);
        return new Point(array[0], array[1]);
    }

    public static void playButtonSound(Context context) {
        AppHelper.playSound(context, R.raw.button, 1.0f);
    }
}
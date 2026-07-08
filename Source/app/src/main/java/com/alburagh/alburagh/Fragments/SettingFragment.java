package com.alburagh.alburagh.Fragments;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.res.Configuration;

import com.alburagh.alburagh.Helpers.UIHelper;
import com.alburagh.alburagh.MainActivity;
import com.alburagh.alburagh.R;

public class SettingFragment extends Fragment {

    public SettingFragment() {}

    ImageButton emailButton, telegramButton, instagramButton,
            siteButton, appSiteButton, guideButton,
            shareButton, closeButton, facebookButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        Animation slide_in_up = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_up);
        slide_in_up.setDuration(1500);
        view.findViewById(R.id.container).startAnimation(slide_in_up);

        emailButton = view.findViewById(R.id.mail_button);
        telegramButton = view.findViewById(R.id.telegram_button);
        instagramButton = view.findViewById(R.id.instagram_button);
        siteButton = view.findViewById(R.id.alburagh_button);
        closeButton = view.findViewById(R.id.close_button);
        shareButton = view.findViewById(R.id.share_button);
        guideButton = view.findViewById(R.id.guide_button);
        appSiteButton = view.findViewById(R.id.app_button);
        facebookButton = view.findViewById(R.id.facebook_button);

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "noorart1@gmail.com", null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getResources().getString(R.string.link_facebook)));
                startActivity(browserIntent);

                UIHelper.playButtonSound(getActivity());
            }
        });

        telegramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getResources().getString(R.string.link_telegram)));
                startActivity(browserIntent);

                UIHelper.playButtonSound(getActivity());
            }
        });

        instagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getResources().getString(R.string.link_instagram)));
                startActivity(browserIntent);

                UIHelper.playButtonSound(getActivity());
            }
        });

        siteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getResources().getString(R.string.link_pub)));
                startActivity(browserIntent);

                UIHelper.playButtonSound(getActivity());
            }
        });

        appSiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getResources().getString(R.string.link_app_site)));
                startActivity(browserIntent);

                UIHelper.playButtonSound(getActivity());
            }
        });

        guideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getResources().getString(R.string.link_guide)));
                startActivity(browserIntent);

                UIHelper.playButtonSound(getActivity());
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.showHome(false);

                UIHelper.playButtonSound(getActivity());
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                ViewTreeObserver observer = view.getViewTreeObserver();
                observer.removeOnGlobalLayoutListener(this);

                UIHelper.ScreenOptions so = new UIHelper.ScreenOptions(getActivity(), view);

                //==================================================================================
                // Settings Background Image (Fit Size)
                //==================================================================================
                if ((getResources().getConfiguration().screenLayout &
                        Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    View container = view.findViewById(R.id.container);
                    ImageView Background = view.findViewById(R.id.background);
                    container.setBackgroundResource(R.drawable.setting_bg);
                    Background.setBackgroundResource(R.drawable.setting_bg);
                } else {
                    View container = view.findViewById(R.id.container);
                    ImageView Background = view.findViewById(R.id.background);
                    container.setBackgroundResource(R.drawable.setting_bg);
                    Background.setBackgroundResource(R.drawable.setting_bg);
                }

                if (so.height / so.width < so.imageHeight / so.imageWidth) {
                    View container = view.findViewById(R.id.container);
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) container.getLayoutParams();
                    int width = (int) ((container.getHeight() * so.imageWidth) / so.imageHeight);
                    layoutParams.width = width;
                    container.setLayoutParams(layoutParams);

                    so = new UIHelper.ScreenOptions(getActivity(), container, width, container.getHeight());
                }

                TypedValue outValue = new TypedValue();
                getResources().getValue(R.dimen.button_scale, outValue, true);
                float buttonScale = outValue.getFloat();

                UIHelper.fitViewToBackground(so, view.findViewById(R.id.mail_button), 50, 50, new Point(400 + 60, 210), buttonScale);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.facebook_button), 50, 50, new Point(600 + 60, 210), buttonScale);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.instagram_button), 50, 50, new Point(800 + 60, 210), buttonScale);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.telegram_button), 50, 50, new Point(1000 + 60, 210), buttonScale);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.alburagh_button), 50, 50, new Point(1200 + 60, 210), buttonScale);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.app_button), 50, 50, new Point(1261 + 60, 210), buttonScale);//new Point(1261 + 60, 210), buttonScale);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.guide_button), 50, 50, new Point(1400 + 60, 210), buttonScale);//new Point(1446 + 60, 210), buttonScale);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.close_button), 50, 50, new Point(1500, 900), buttonScale);


                //==================================================================================
                // Version Name In TextView
                //==================================================================================
                if ((getResources().getConfiguration().screenLayout &
                        Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.settings_vname),
                            250, 120, new Point(1160, 710), 3.77f);
                    TextView SettingsVName = view.findViewById(R.id.settings_vname);
                    SettingsVName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                } else {
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.settings_vname),
                            250, 120, new Point(1160, 710), 4.66f);
                    TextView SettingsVName = view.findViewById(R.id.settings_vname);
                    SettingsVName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                }

                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.mail_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.facebook_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.instagram_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.telegram_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.alburagh_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.app_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.guide_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.close_button));
            }
        });
    }
}
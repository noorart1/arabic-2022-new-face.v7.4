package com.alburagh.alburagh.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.content.Intent;
import android.net.Uri;

import android.content.res.Configuration;

import java.io.IOException;

import android.net.NetworkInfo;


import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import java.io.FileReader;
import java.io.BufferedReader;


import com.alburagh.alburagh.Helpers.AnimationHelper;
import com.alburagh.alburagh.Helpers.AppHelper;


import com.alburagh.alburagh.Helpers.ContentManager;
import com.alburagh.alburagh.Helpers.DownloadManager;
import com.alburagh.alburagh.Helpers.HttpHandler;

import com.alburagh.alburagh.Models.Content;


import com.alburagh.alburagh.Helpers.Setting;
import com.alburagh.alburagh.Helpers.UIHelper;
import com.alburagh.alburagh.MainActivity;

import com.alburagh.alburagh.Models.Quran;
import com.alburagh.alburagh.R;
import com.duowan.mobile.netroid.NetroidError;
import com.filippudak.ProgressPieView.ProgressPieView;

import java.util.List;

public class HomeFragment extends Fragment {
    public Boolean unlocked = false;

    public HomeFragment() {}

    private String TAG = MainActivity.class.getSimpleName();

    ImageButton  favoriteButton, startButton,
            settingsButton, RateButton,
            musicButton, publicationButton, accountButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        final MainActivity mainActivity = (MainActivity) getActivity();
        Log.i(TAG, "onCreateViewcontent: ");



        startButton         = view.findViewById(R.id.startButton);
        favoriteButton      = view.findViewById(R.id.favorite_button);
        settingsButton      = view.findViewById(R.id.settings_button);
        RateButton          = view.findViewById(R.id.rate_button);
        musicButton         = view.findViewById(R.id.music_button);
        publicationButton   = view.findViewById(R.id.publication_icon);
        accountButton       = view.findViewById(R.id.account_button);

        Animation slide_out_up = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_up);
        slide_out_up.setDuration(1500);
        view.findViewById(R.id.container).startAnimation(slide_out_up);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.showMenu();
                UIHelper.playButtonSound(getActivity());
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.showFavoriteContent(new HomeFragment());
                UIHelper.playButtonSound(getActivity());
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.showSettings();
                UIHelper.playButtonSound(getActivity());
            }
        });



        //==========================================================================================
        // Rate App
        //==========================================================================================

        RateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + mainActivity.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + mainActivity.getPackageName())));
                }
            }
        });
        //==========================================================================================

        if (!Setting.getInstance().preferences.getBoolean("music_state", true))
            musicButton.setImageResource(R.drawable.music_off_button);

        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity.musicPlayer != null && mainActivity.musicPlayer.isPlaying()) {
                    mainActivity.pauseMusic(true);
                    ((ImageButton) v).setImageResource(R.drawable.music_off_button);
                } else {
                    mainActivity.playMusic();
                    ((ImageButton) v).setImageResource(R.drawable.music_on_button);
                }
            }
        });

        view.findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppHelper.shareApp(getActivity());
            }
        });

        publicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.showAbout();
                UIHelper.playButtonSound(getActivity());
            }
        });

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.showAccountView(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) getActivity()).showAccountWarning(false);
                    }
                });

                UIHelper.playButtonSound(getActivity());
            }
        });

        if (!AppHelper.homeSoundPlayed) {
            AppHelper.playSound(getActivity(), R.raw.title, 0.6f);
            AppHelper.homeSoundPlayed = true;
        }

        return view;
    }




    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                ViewTreeObserver observer = view.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    observer.removeOnGlobalLayoutListener(this);
                else
                    observer.removeGlobalOnLayoutListener(this);

                UIHelper.ScreenOptions so = new UIHelper.ScreenOptions(getActivity(), view);
                if (so.height / so.width < so.imageHeight / so.imageWidth) {
                    View container = view.findViewById(R.id.container);
                    container.setBackgroundResource(R.drawable.home_bg);
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) container.getLayoutParams();
                    int width = (int) ((container.getHeight() * so.imageWidth) / so.imageHeight);
                    layoutParams.width = width;
                    container.setLayoutParams(layoutParams);
                    so = new UIHelper.ScreenOptions(getActivity(), container, width, container.getHeight());
                }

                TypedValue outValue = new TypedValue();
                getResources().getValue(R.dimen.button_scale, outValue, true);
                float buttonScale = outValue.getFloat();


                if ((getResources().getConfiguration().screenLayout &
                        Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.account_button), 110, 110, UIHelper.intArrayToPoint(getActivity(), R.array.home_account), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.rate_button), 95, 95, UIHelper.intArrayToPoint(getActivity(), R.array.home_rate), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.favorite_button_wrapper), 90, 90, UIHelper.intArrayToPoint(getActivity(), R.array.home_favorite), buttonScale);
                } else {
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.account_button), 90, 90, UIHelper.intArrayToPoint(getActivity(), R.array.home_account), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.rate_button), 85, 85, UIHelper.intArrayToPoint(getActivity(), R.array.home_rate), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.favorite_button_wrapper), 75, 75, UIHelper.intArrayToPoint(getActivity(), R.array.home_favorite), buttonScale);
                }

                UIHelper.fitViewToBackground(so, view.findViewById(R.id.settings_button), 60, 60, UIHelper.intArrayToPoint(getActivity(), R.array.home_setting), buttonScale);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.music_button), 60, 60, UIHelper.intArrayToPoint(getActivity(), R.array.home_music), buttonScale);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.share_button), 65, 65, UIHelper.intArrayToPoint(getActivity(), R.array.home_share), buttonScale);


//                UIHelper.fitViewToBackground(so, view.findViewById(R.id.startButton), R.drawable.start_button, 735, 817);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.start_button_wrapper), 125, 125, new Point(955, 970), 1.0f);
                UIHelper.fitImageToBackground(so, view.findViewById(R.id.publication_icon), R.drawable.publication_icon, 264, 31);
                UIHelper.fitImageToBackground(so, view.findViewById(R.id.trophy_button), R.drawable.trophy_button, 1440, 240);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.logo), 250, 125, new Point(960, 150), 1.0f);
                UIHelper.fitLayoutToBackground(so, view.findViewById(R.id.giraffe_view), 503, 583, 0, 600); // x = 1366
                UIHelper.fitLayoutToBackground(so, view.findViewById(R.id.elephant_head), 400, 401, 163, 648);
                UIHelper.fitSize(so, view.findViewById(R.id.cloud_01), R.drawable.home_cloud);

                int planeWidth = (int) (598 * 1.16);
                int planeHeight = (int) (519 * 1.16);
                int planePartWidth = 598 / 2;
                int planePartHeight = 519 / 2;

                UIHelper.fitLayoutToBackground(so, view.findViewById(R.id.plane_drag), planeWidth, planeHeight, 643, 255);
                UIHelper.fitLayoutToBackground(so, view.findViewById(R.id.plane), planeWidth, planeHeight, 0, 0);
                UIHelper.fitSize(so, view.findViewById(R.id.plane_body), planePartWidth, planePartHeight);
                UIHelper.fitSize(so, view.findViewById(R.id.plane_eye), planePartWidth, planePartHeight);
                UIHelper.fitSize(so, view.findViewById(R.id.propeller), planePartWidth, planePartHeight);
                UIHelper.fitSize(so, view.findViewById(R.id.head_01), planePartWidth, planePartHeight);
                UIHelper.fitSize(so, view.findViewById(R.id.head_02), planePartWidth, planePartHeight);
                UIHelper.fitSize(so, view.findViewById(R.id.balloon), planePartWidth, planePartHeight);

                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.trophy_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.favorite_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.startButton));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.publication_icon));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.music_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.share_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.rate_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.settings_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.account_button));

                initAnimations(so, view);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        DownloadManager.getInstance().unsetListener();
        View layout = getView();
        if (layout != null) {
            layout.findViewById(R.id.logo).animate().cancel();
            layout.findViewById(R.id.giraffe_head).animate().cancel();
            layout.findViewById(R.id.plane).animate().cancel();
            layout.findViewById(R.id.propeller).animate().cancel();
            layout.findViewById(R.id.giraffe_eye).animate().cancel();
            layout.findViewById(R.id.elephant_eye).animate().cancel();
            layout.findViewById(R.id.plane_eye).animate().cancel();
            layout.findViewById(R.id.favorite_button_wrapper).animate().cancel();
            layout.findViewById(R.id.cloud_01).animate().cancel();
            layout.findViewById(R.id.plane_drag).animate().cancel();
            layout.findViewById(R.id.elephant_head).animate().cancel();
            layout.findViewById(R.id.start_button_wrapper).animate().cancel();
            layout.findViewById(R.id.giraffe_view).animate().cancel();
        }
    }


    //==============================================================================================
    //== Animations
    //==============================================================================================

    public void initAnimations(UIHelper.ScreenOptions so, View layout) {
        // Animate giraffe to right edge to avoid shrinking for less wider screens
        layout.findViewById(R.id.giraffe_view).animate()
                .translationX((1466 * so.ratio) - so.widthOffset)
                .setDuration(0).withLayer().start();

        // giraffeHeadAnimation(layout);
        logoAnimation(layout);
        AnimationHelper.getInstance().setupWiggleAnimation01(layout.findViewById(R.id.plane), 1.0f);
        AnimationHelper.getInstance().setupSpinAnimation(layout.findViewById(R.id.propeller), 0.08f, 0.39f, 200, true);
        AnimationHelper.getInstance().setupEyeAnimation(layout.findViewById(R.id.giraffe_eye), 3000);
        AnimationHelper.getInstance().setupEyeAnimation(layout.findViewById(R.id.elephant_eye), 2000);
        AnimationHelper.getInstance().setupEyeAnimation(layout.findViewById(R.id.plane_eye), 4000);
        AnimationHelper.getInstance().setupFavoriteButtonAnimation(layout.findViewById(R.id.favorite_button_wrapper));
        AnimationHelper.getInstance().setupStartButtonAnimation(layout.findViewById(R.id.start_button_wrapper));
        AnimationHelper.getInstance().setupCloudAnimation(so, layout.findViewById(R.id.cloud_01), 30000);

        final List<View> buttons = new ArrayList<>();
        buttons.add(layout.findViewById(R.id.publication_icon));
        buttons.add(layout.findViewById(R.id.settings_button));
        buttons.add(layout.findViewById(R.id.music_button));
        buttons.add(layout.findViewById(R.id.share_button));
        buttons.add(layout.findViewById(R.id.rate_button));
        buttons.add(layout.findViewById(R.id.account_button));
        buttons.add(layout.findViewById(R.id.trophy_button));
        buttons.add(layout.findViewById(R.id.favorite_button));
        buttons.add(layout.findViewById(R.id.startButton));
        AnimationHelper.getInstance().buttonsAnimation(buttons);

        // Plane
        RelativeLayout plane = layout.findViewById(R.id.plane_drag);
        plane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AppHelper.playSound(getActivity(), R.raw.cheer, 1.0f);
                view.setClickable(false);
                view.animate().withLayer().translationXBy(-2500).setDuration(3000).setStartDelay(0).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.animate().withLayer().translationXBy(5000).setDuration(0).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                view.animate().translationXBy(-2500).setStartDelay(500).setDuration(3000).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        view.setClickable(true);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        // Elephant Head
        FrameLayout elephantHead = layout.findViewById(R.id.elephant_head);
        elephantHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AppHelper.playSound(getActivity(), R.raw.elephant, 1.0f);
                view.setClickable(false);
                view.setPivotX((float) (view.getWidth() * 0.5));
                view.setPivotY((float) (view.getHeight() * 0.6));

                view.animate().rotationBy(11).withLayer().setDuration(500).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.animate().rotationBy(-16).withLayer().setDuration(500).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                view.animate().withLayer().rotationBy(5).setDuration(500).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        view.setClickable(true);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        // Giraffe
        FrameLayout giraffe = layout.findViewById(R.id.giraffe_view);
        giraffe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AppHelper.playSound(getActivity(), R.raw.giraffe, 1.0f);
                view.setClickable(false);
                view.animate().translationXBy(600).translationYBy(600).withLayer().setDuration(2000).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.animate().translationXBy(-600).translationYBy(-600).withLayer().setDuration(2000).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                view.setClickable(true);
                            }
                        });
                    }
                });
            }
        });
    }

    public void logoAnimation(final View layout) {
        final ImageView logo = layout.findViewById(R.id.logo);
        logo.animate().translationYBy(-40).withLayer().setStartDelay(3500).setDuration(500).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                logo.animate().translationYBy(40).withLayer().setStartDelay(0).setInterpolator(new BounceInterpolator()).setDuration(1500).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        logoAnimation(layout);
                    }
                });
            }
        });
    }

    public void giraffeHeadAnimation(final View layout) {
        final View view = layout.findViewById(R.id.giraffe_head);
        view.setPivotX((float) (view.getWidth() * 0.42));
        view.setPivotY((float) (view.getHeight() * 0.35));

        view.animate().rotationBy(-11).withLayer().setDuration(500).setStartDelay(3000).withEndAction(new Runnable() {
            @Override
            public void run() {
                view.animate().rotationBy(16).withLayer().setDuration(500).setStartDelay(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.animate().withLayer().rotationBy(-5).setDuration(500).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                giraffeHeadAnimation(layout);
                            }
                        });
                    }
                });
            }
        });
    }
}
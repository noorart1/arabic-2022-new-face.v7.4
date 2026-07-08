package com.alburagh.alburagh.Helpers;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.alburagh.alburagh.R;

import java.util.List;

import static com.alburagh.alburagh.Helpers.UIHelper.*;

public class AnimationHelper {
    private static AnimationHelper instance = null;
    private AnimationHelper(){}
    public static AnimationHelper getInstance(){
        if (instance == null)
            instance = new AnimationHelper();
        return instance;
    }

    public void setupEyeAnimation(final View view, final int delay) {
        if (view == null)
            return;

        view.setAlpha(0);
        view.animate().alpha(1).withLayer().setDuration(10).setStartDelay(delay).withEndAction(new Runnable() {
            @Override
            public void run() {
                view.animate().alpha(0).withLayer().setDuration(10).setStartDelay(500).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.animate().alpha(1).withLayer().setDuration(10).setStartDelay(300).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                view.animate().alpha(0).withLayer().setDuration(10).setStartDelay(50).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        setupEyeAnimation(view, delay);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    public void setupBlinkAnimation(final View view, int delay, final int duration) {
        view.animate().withLayer().alpha(0).setDuration(duration).setStartDelay(50 + delay).withEndAction(new Runnable() {
            @Override
            public void run() {
                view.animate().withLayer().alpha(1).setDuration(duration).setStartDelay(50).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        setupBlinkAnimation(view, 0, duration);
                    }
                });
            }
        });
    }

    public void setupSpinAnimation(final View view, final float pivotX, final float pivotY, final int duration, final boolean reverse) {
        view.setPivotX(view.getWidth() * pivotX);
        view.setPivotY(view.getHeight() * pivotY);
        view.animate().rotationBy(reverse ? -45 : 45).setDuration(duration).setInterpolator(new LinearInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                setupSpinAnimation(view, pivotX, pivotY, duration, reverse);
            }
        });
    }

    public void setupKidAnimation(final View wrapper, View kid, final Boolean isKid) {
        kidWiggle(wrapper);
        kid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(isKid) AppHelper.playSound(wrapper.getContext(), R.raw.kid, 1.0f);
                else AppHelper.playSound(wrapper.getContext(), R.raw.dragon, 1.0f);
                view.setClickable(false);
                view.animate().withLayer().translationYBy(200).setDuration(600).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.animate().withLayer().translationYBy(-200).setDuration(600).setStartDelay(200).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
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

    public void kidWiggle (final View view) {
        view.animate().withLayer().translationYBy(20).setDuration(800).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                view.animate().withLayer().translationYBy(-20).setDuration(800).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        kidWiggle(view);
                    }
                });
            }
        });
    }

    public void setupFavoriteButtonAnimation(final View view) {
        view.animate().scaleX(1.2f).scaleY(1.2f).setStartDelay(2500).withLayer().setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                view.animate().scaleX(1.0f).scaleY(1.0f).withLayer().setStartDelay(0).setInterpolator(new BounceInterpolator()).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        setupFavoriteButtonAnimation(view);
                    }
                });
            }
        });
    }

    public void setupStartButtonAnimation(final View view) {
        view.animate().scaleX(1.2f).scaleY(1.2f).setStartDelay(1000).withLayer().setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                view.animate().scaleX(1.0f).scaleY(1.0f).withLayer().setStartDelay(0).setInterpolator(new BounceInterpolator()).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        setupStartButtonAnimation(view);
                    }
                });
            }
        });
    }

    public void setupWrongpassAnimation(final View view) {

        view.animate().translationXBy(-40).withLayer().setStartDelay(0).setDuration(20).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                view.animate().translationXBy(80).withLayer().setStartDelay(0).setInterpolator(new BounceInterpolator()).setDuration(20).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.animate().translationXBy(-60).withLayer().setStartDelay(0).setDuration(20).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                view.animate().translationXBy(20).withLayer().setStartDelay(0).setInterpolator(new BounceInterpolator()).setDuration(20).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

    }

    public void setupWiggleAnimation01 (final View plane, final float power) {
        plane.animate().translationXBy(-10 * power).translationYBy(-12 * power).withLayer().setDuration(1000).setInterpolator(new LinearInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                plane.animate().translationXBy(20 * power).translationYBy(20 * power).withLayer().setInterpolator(new LinearInterpolator()).setDuration(1200).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        plane.animate().translationXBy(-15 * power).translationYBy(-3 * power).withLayer().setInterpolator(new LinearInterpolator()).setDuration(1000).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                plane.animate().translationXBy(+20 * power).translationYBy(-20 * power).withLayer().setInterpolator(new LinearInterpolator()).setDuration(1300).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        plane.animate().translationXBy(-15 * power).translationYBy(15 * power).withLayer().setInterpolator(new LinearInterpolator()).setDuration(1000).withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                setupWiggleAnimation01(plane, power);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    public void setupDraggable(final View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("ALBURAGH", String.format("%.1f, %.1f", event.getX(), event.getY()));
                if (event.getAction() == MotionEvent.ACTION_MASK) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    layoutParams.setMargins((int) event.getX(), (int) event.getY(), 0, 0);
                    view.setLayoutParams(layoutParams);
                }
                return false;
            }
        });
    }

    public void setupCloudAnimation(ScreenOptions screenOptions, View view, int duration) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.setMargins((int)(screenOptions.width + view.getWidth() + 100), layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
        cloudAnimation(screenOptions, view, duration);
    }

    private void cloudAnimation (final ScreenOptions screenOptions, final View view, final int duration) {
        final float amount = screenOptions.width + (view.getWidth() * 2)  + 200;
        view.animate().withLayer().translationXBy(-amount).setDuration(duration).withEndAction(new Runnable() {
            @Override
            public void run() {
                view.animate().withLayer().translationXBy(amount).setDuration(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        cloudAnimation(screenOptions, view, duration);
                    }
                });
            }
        });
    }

    public void buttonsAnimation(final List<View> buttons) {
        for(View button: buttons) {
            button.setScaleX(0);
            button.setScaleY(0);
        }

        for (int i=0; i<buttons.size(); i++) {
            final View button = buttons.get(i);
            button.animate().scaleX(1.1f).scaleY(1.1f).setDuration(400).setStartDelay(i * 150).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
                @Override
                public void run() {
                    button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).setStartDelay(0).setInterpolator(new DecelerateInterpolator());
                }
            });
        }
    }

}
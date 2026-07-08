package com.alburagh.alburagh.Fragments;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alburagh.alburagh.Helpers.AnimationHelper;
import com.alburagh.alburagh.Helpers.UIHelper;
import com.alburagh.alburagh.MainActivity;
import com.alburagh.alburagh.R;

public class LockFragment extends Fragment {

    public int clicked = 0;
    public int num = 0;
    public boolean haveLine = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_lock, container, false);
        final MainActivity mainActivity = (MainActivity) getActivity();

        Animation slide_in_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_up);
        view.findViewById(R.id.phone_body).startAnimation(slide_in_down);

        RelativeLayout mainLayout = view.findViewById(R.id.main_layout);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        final RelativeLayout lock_form = view.findViewById(R.id.phone_body);
        lock_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { }
        });

        final TextView pass = view.findViewById(R.id.pass);

        ImageButton lock_1 = view.findViewById(R.id.lock1);
        lock_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = (num * 10) + 1;
                clicked = clicked + 1;
                if (haveLine) {
                    pass.append("-" + getString(R.string.one));
                } else {
                    pass.append(getString(R.string.one));
                    haveLine = true;
                }
                UIHelper.playButtonSound(getActivity());
            }
        });

        ImageButton lock_2 = view.findViewById(R.id.lock2);
        lock_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = (num * 10) + 2;
                clicked = clicked + 1;
                if (haveLine)
                    pass.append("-" + getString(R.string.two));
                else {
                    pass.append(getString(R.string.two));
                    haveLine = true;
                }

                UIHelper.playButtonSound(getActivity());
            }
        });

        ImageButton lock_3 = view.findViewById(R.id.lock3);
        lock_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = (num * 10) + 3;
                clicked = clicked + 1;
                if (haveLine) {
                    pass.append("-" + getString(R.string.three));
                } else {
                    pass.append(getString(R.string.three));
                    haveLine = true;
                }
                UIHelper.playButtonSound(getActivity());
            }
        });

        ImageButton lock_4 = view.findViewById(R.id.lock4);
        lock_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = (num * 10) + 4;
                clicked = clicked + 1;
                if (haveLine) {
                    pass.append("-" + getString(R.string.four));
                } else {
                    pass.append(getString(R.string.four));
                    haveLine = true;
                }
                UIHelper.playButtonSound(getActivity());
            }
        });

        ImageButton lock_5 = view.findViewById(R.id.lock5);
        lock_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = (num * 10) + 5;
                clicked = clicked + 1;
                if (haveLine) {
                    pass.append("-" + getString(R.string.five));
                } else {
                    pass.append(getString(R.string.five));
                    haveLine = true;
                }
                UIHelper.playButtonSound(getActivity());
            }
        });

        ImageButton lock_6 = view.findViewById(R.id.lock6);
        lock_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = (num * 10) + 6;
                clicked = clicked + 1;
                if (haveLine) {
                    pass.append("-" + getString(R.string.six));
                } else {
                    pass.append(getString(R.string.six));
                    haveLine = true;
                }
                UIHelper.playButtonSound(getActivity());
            }
        });

        ImageButton lock_7 = view.findViewById(R.id.lock7);
        lock_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = (num * 10) + 7;
                clicked = clicked + 1;
                if (haveLine) {
                    pass.append("-" + getString(R.string.seven));
                } else {
                    pass.append(getString(R.string.seven));
                    haveLine = true;
                }
                UIHelper.playButtonSound(getActivity());
            }
        });
        ImageButton lock_8 = view.findViewById(R.id.lock8);
        lock_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = (num * 10) + 8;
                clicked = clicked + 1;
                if (haveLine) {
                    pass.append("-" + getString(R.string.eight));
                } else {
                    pass.append(getString(R.string.eight));
                    haveLine = true;
                }
                UIHelper.playButtonSound(getActivity());
            }
        });

        ImageButton lock_9 = view.findViewById(R.id.lock9);
        lock_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = (num * 10) + 9;
                clicked = clicked + 1;
                if (haveLine) {
                    pass.append("-" + getString(R.string.nine));
                } else {
                    pass.append(getString(R.string.nine));
                    haveLine = true;
                }
                UIHelper.playButtonSound(getActivity());
            }
        });

        ImageButton lock_0 = view.findViewById(R.id.lock0);
        lock_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = (num * 10) + 0;
                clicked = clicked + 1;
                if (haveLine) {
                    pass.append("-" + getString(R.string.zero));
                } else {
                    pass.append(getString(R.string.zero));
                    haveLine = true;
                }
                UIHelper.playButtonSound(getActivity());
            }
        });

        final TextView soal = view.findViewById(R.id.soal);
        Random randomGenerator = new Random();
        int first_num = randomGenerator.nextInt(10);
        int secound_num = randomGenerator.nextInt(10);
        int third_num = randomGenerator.nextInt(10);

        final int pass_lock = (first_num * 100) + (secound_num * 10) + third_num;
        String first_txt = "واحد";
        String secound_txt = "واحد";
        String third_txt = "واحد";

        Log.i("tag", "onCreateView: " + first_num + " " + secound_num + " " + third_num);

        switch (first_num) {
            case 0:
                first_txt = getString(R.string.zero);
                break;
            case 1:
                first_txt = getString(R.string.one);
                break;
            case 2:
                first_txt = getString(R.string.two);
                break;
            case 3:
                first_txt = getString(R.string.three);
                break;
            case 4:
                first_txt = getString(R.string.four);
                break;
            case 5:
                first_txt = getString(R.string.five);
                break;
            case 6:
                first_txt = getString(R.string.six);
                break;
            case 7:
                first_txt = getString(R.string.seven);
                break;
            case 8:
                first_txt = getString(R.string.eight);
                break;
            case 9:
                first_txt = getString(R.string.nine);
                break;
        }

        switch (secound_num) {
            case 0:
                secound_txt = getString(R.string.zero);
                break;
            case 1:
                secound_txt = getString(R.string.one);
                break;
            case 2:
                secound_txt = getString(R.string.two);
                break;
            case 3:
                secound_txt = getString(R.string.three);
                break;
            case 4:
                secound_txt = getString(R.string.four);
                break;
            case 5:
                secound_txt = getString(R.string.five);
                break;
            case 6:
                secound_txt = getString(R.string.six);
                break;
            case 7:
                secound_txt = getString(R.string.seven);
                break;
            case 8:
                secound_txt = getString(R.string.eight);
                break;
            case 9:
                secound_txt = getString(R.string.nine);
                break;
        }

        switch (third_num) {
            case 0:
                third_txt = getString(R.string.zero);
                break;
            case 1:
                third_txt = getString(R.string.one);
                break;
            case 2:
                third_txt = getString(R.string.two);
                break;
            case 3:
                third_txt = getString(R.string.three);
                break;
            case 4:
                third_txt = getString(R.string.four);
                break;
            case 5:
                third_txt = getString(R.string.five);
                break;
            case 6:
                third_txt = getString(R.string.six);
                break;
            case 7:
                third_txt = getString(R.string.seven);
                break;
            case 8:
                third_txt = getString(R.string.eight);
                break;
            case 9:
                third_txt = getString(R.string.nine);
                break;
        }

        soal.setText(first_txt + "-" + secound_txt + "-" + third_txt);

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i != 0) {
                    if (num == pass_lock) {
                        ((MainActivity) getActivity()).unlocked = true;
                        getActivity().getSupportFragmentManager().popBackStack();
                        mainActivity.showAccountView(null);
                        UIHelper.playButtonSound(getActivity());
                    } else if (clicked == 3) {
                        num = 0;
                        pass.setText("");
                        clicked = 0;
                        haveLine = false;
                        AnimationHelper.getInstance().setupWrongpassAnimation(view.findViewById(R.id.phone_body));
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        return view;
    }


    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                ViewTreeObserver observer = view.getViewTreeObserver();
                ViewGroup container = view.findViewById(R.id.phone_body);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    observer.removeOnGlobalLayoutListener(this);
                else
                    observer.removeGlobalOnLayoutListener(this);

                UIHelper.ScreenOptions so = new UIHelper.ScreenOptions(getActivity(), view);
                if (so.height / so.width < so.imageHeight / so.imageWidth) {
                    // RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    //         ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
                    // ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    //         ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    // ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                    //         ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT);

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
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.phone_body), 250, 425, UIHelper.intArrayToPoint(getActivity(), R.array.phone_body), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock1), 69, 50, UIHelper.intArrayToPoint(getActivity(), R.array.num_1), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock2), 69, 50, UIHelper.intArrayToPoint(getActivity(), R.array.num_2), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock3), 69, 50, UIHelper.intArrayToPoint(getActivity(), R.array.num_3), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock4), 69, 50, UIHelper.intArrayToPoint(getActivity(), R.array.num_4), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock5), 69, 50, UIHelper.intArrayToPoint(getActivity(), R.array.num_5), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock6), 69, 50, UIHelper.intArrayToPoint(getActivity(), R.array.num_6), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock7), 69, 50, UIHelper.intArrayToPoint(getActivity(), R.array.num_7), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock8), 69, 50, UIHelper.intArrayToPoint(getActivity(), R.array.num_8), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock9), 69, 50, UIHelper.intArrayToPoint(getActivity(), R.array.num_9), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock0), 69, 50, UIHelper.intArrayToPoint(getActivity(), R.array.num_0), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.soal), 250, 50, UIHelper.intArrayToPoint(getActivity(), R.array.soal), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.pass), 250, 50, UIHelper.intArrayToPoint(getActivity(), R.array.pass), buttonScale);

                } else {
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.phone_body), 200, 339, UIHelper.intArrayToPoint(getActivity(), R.array.phone_body), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock1), 55, 40, UIHelper.intArrayToPoint(getActivity(), R.array.num_1), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock2), 55, 40, UIHelper.intArrayToPoint(getActivity(), R.array.num_2), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock3), 55, 40, UIHelper.intArrayToPoint(getActivity(), R.array.num_3), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock4), 55, 40, UIHelper.intArrayToPoint(getActivity(), R.array.num_4), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock5), 55, 40, UIHelper.intArrayToPoint(getActivity(), R.array.num_5), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock6), 55, 40, UIHelper.intArrayToPoint(getActivity(), R.array.num_6), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock7), 55, 40, UIHelper.intArrayToPoint(getActivity(), R.array.num_7), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock8), 55, 40, UIHelper.intArrayToPoint(getActivity(), R.array.num_8), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock9), 55, 40, UIHelper.intArrayToPoint(getActivity(), R.array.num_9), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.lock0), 55, 40, UIHelper.intArrayToPoint(getActivity(), R.array.num_0), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.soal), 250, 50, UIHelper.intArrayToPoint(getActivity(), R.array.soal), buttonScale);
                    UIHelper.fitViewToBackground(so, view.findViewById(R.id.pass), 250, 50, UIHelper.intArrayToPoint(getActivity(), R.array.pass), buttonScale);
                }

                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.lock1));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.lock2));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.lock3));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.lock4));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.lock5));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.lock6));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.lock7));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.lock8));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.lock9));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.lock0));

                initAnimations(so, view);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        View layout = getView();
        if (layout != null) {
            layout.findViewById(R.id.lock1).animate().cancel();
            layout.findViewById(R.id.lock2).animate().cancel();
            layout.findViewById(R.id.lock3).animate().cancel();
            layout.findViewById(R.id.lock4).animate().cancel();
            layout.findViewById(R.id.lock5).animate().cancel();
            layout.findViewById(R.id.lock6).animate().cancel();
            layout.findViewById(R.id.lock7).animate().cancel();
            layout.findViewById(R.id.lock8).animate().cancel();
            layout.findViewById(R.id.lock9).animate().cancel();
            layout.findViewById(R.id.lock0).animate().cancel();
        }
    }

    public void initAnimations(UIHelper.ScreenOptions so, View layout) {
        final List<View> buttons = new ArrayList<>();
        buttons.add(layout.findViewById(R.id.lock1));
        buttons.add(layout.findViewById(R.id.lock2));
        buttons.add(layout.findViewById(R.id.lock3));
        buttons.add(layout.findViewById(R.id.lock4));
        buttons.add(layout.findViewById(R.id.lock5));
        buttons.add(layout.findViewById(R.id.lock6));
        buttons.add(layout.findViewById(R.id.lock7));
        buttons.add(layout.findViewById(R.id.lock8));
        buttons.add(layout.findViewById(R.id.lock9));
        buttons.add(layout.findViewById(R.id.lock0));
        AnimationHelper.getInstance().buttonsAnimation(buttons);
    }
}

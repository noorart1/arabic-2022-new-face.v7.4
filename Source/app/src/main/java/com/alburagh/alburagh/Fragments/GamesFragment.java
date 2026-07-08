package com.alburagh.alburagh.Fragments;

import static android.content.ContentValues.TAG;

import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.alburagh.alburagh.Helpers.AnimationHelper;
import com.alburagh.alburagh.Helpers.UIHelper;
import com.alburagh.alburagh.MainActivity;
import com.alburagh.alburagh.R;

import java.util.ArrayList;
import java.util.List;

public class GamesFragment extends Fragment {

    public GamesFragment() {}

    ImageButton favoriteButton,homeButton,booksButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_games, container, false);

        homeButton = view.findViewById(R.id.home_button);
//        favoriteButton = view.findViewById(R.id.favorite_button);
//        booksButton = view.findViewById(R.id.books_button);
//        MainActivity mainActivity = (MainActivity) getActivity();
      //  mainActivity.checkForPermission();
        Animation slide_in_up = AnimationUtils.loadAnimation(getActivity(),R.anim.slide_out_up);
        slide_in_up.setDuration(1500);
        view.findViewById(R.id.container).startAnimation(slide_in_up);
//
//        slide_in_up.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
////                view.findViewById(R.id.dragon_wrapper).setVisibility(View.VISIBLE);
////                Animation enter_from_left = AnimationUtils.loadAnimation(getActivity(),R.anim.enter_from_left);
////                view.findViewById(R.id.dragon_wrapper).startAnimation(enter_from_left);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });

//        favoriteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity mainActivity = (MainActivity) getActivity();
//                mainActivity.showFavoriteContent(new GamesFragment());
//                UIHelper.playButtonSound(getActivity());
//            }
//        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.showHome(false);
                UIHelper.playButtonSound(getActivity());
            }
        });

//        booksButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity mainActivity = (MainActivity) getActivity();
//                mainActivity.showBooks();
//                UIHelper.playButtonSound(getActivity());
//            }
//        });

        return view;
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

                Bundle bundle = new Bundle();
                bundle.putSerializable("type", ContentsFragment.ContentType.GAMES);

                ContentsFragment soundBooks = new ContentsFragment();
                soundBooks.setArguments(bundle);

                getChildFragmentManager().beginTransaction()
                        .replace(R.id.place_holder, soundBooks)
                        .commit();

                UIHelper.ScreenOptions so = new UIHelper.ScreenOptions(getActivity(), view);

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

//                UIHelper.fitViewToBackground(so, view.findViewById(R.id.favorite_button), 75, 75, UIHelper.intArrayToPoint(getActivity(), R.array.list_favorite), buttonScale);
//                UIHelper.fitViewToBackground(so, view.findViewById(R.id.books_button), 90, 100, UIHelper.intArrayToPoint(getActivity(), R.array.list_alter), buttonScale);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.home_button), 50, 50, UIHelper.intArrayToPoint(getActivity(), R.array.menu_home), buttonScale);

//                UIHelper.fitImageToBackground(so, view.findViewById(R.id.kid_cover), R.drawable.kid_cover, 1303, 191);
//                UIHelper.fitLayoutToBackground(so, view.findViewById(R.id.boy), 287, 262, 1368, 2);
//                UIHelper.fitLayoutToBackground(so, view.findViewById(R.id.dragon), 427, 263, 151, 818);
//                UIHelper.fitLayoutToBackground(so, view.findViewById(R.id.spaceship), 554, 158, getResources().getInteger(R.integer.games_spaceship_x), 40);

                UIHelper.fitLayoutToBackground(so, view.findViewById(R.id.place_holder), 1416, view.getHeight()+100 , 245, 241); // 1416

//                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.favorite_button));
//                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.books_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.home_button));

                initAnimations(view);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        View layout = getView();
        if (layout != null) {
//            layout.findViewById(R.id.dragon_eyes).animate().cancel();
//            layout.findViewById(R.id.dragon_wrapper).animate().cancel();
//            layout.findViewById(R.id.dragon).animate().cancel();
//            layout.findViewById(R.id.favorite_button).animate().cancel();
//            layout.findViewById(R.id.boy_eyes).animate().cancel();
//            layout.findViewById(R.id.boy_wrapper).animate().cancel();
//            layout.findViewById(R.id.boy).animate().cancel();
//            layout.findViewById(R.id.spaceship_lights).animate().cancel();
//            layout.findViewById(R.id.spaceship_afterburn).animate().cancel();
//            layout.findViewById(R.id.spaceship).animate().cancel();
        }
    }



    //==============================================================================================
    //== Animations
    //==============================================================================================

    public void initAnimations (View layout) {
//        AnimationHelper.getInstance().setupEyeAnimation(layout.findViewById(R.id.dragon_eyes), 3500);
//        AnimationHelper.getInstance().setupKidAnimation(layout.findViewById(R.id.dragon_wrapper), layout.findViewById(R.id.dragon),false);
//        AnimationHelper.getInstance().setupFavoriteButtonAnimation(layout.findViewById(R.id.favorite_button));
//        AnimationHelper.getInstance().setupEyeAnimation(layout.findViewById(R.id.boy_eyes), 3000);
//        AnimationHelper.getInstance().setupKidAnimation(layout.findViewById(R.id.boy_wrapper), layout.findViewById(R.id.boy),true);
//        AnimationHelper.getInstance().setupBlinkAnimation(layout.findViewById(R.id.spaceship_lights), 0, 60);
//        AnimationHelper.getInstance().setupBlinkAnimation(layout.findViewById(R.id.spaceship_afterburn), 10, 80);

        List<View> buttons = new ArrayList<>();
//        buttons.add(layout.findViewById(R.id.favorite_button));
//        buttons.add(layout.findViewById(R.id.books_button));
        buttons.add(layout.findViewById(R.id.home_button));
        AnimationHelper.getInstance().buttonsAnimation(buttons);

//        final View spaceship = layout.findViewById(R.id.spaceship);
//        spaceship.animate().withLayer().rotation(25).setDuration(0).withEndAction(new Runnable() {
//            @Override
//            public void run() {
//                spaceship.animate().withLayer().translationXBy(2500).translationYBy(1750).setDuration(0).withEndAction(new Runnable() {
//                    @Override
//                    public void run() {
//                        spaceship.animate().withLayer().translationXBy(-2500).translationYBy(-1750).setDuration(4000).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(new Runnable() {
//                            @Override
//                            public void run() {
//                                AnimationHelper.getInstance().setupWiggleAnimation01(spaceship, 0.5f);
//                            }
//                        });
//                    }
//                });
//            }
//        });
    }
}
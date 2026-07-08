package com.alburagh.alburagh.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.alburagh.alburagh.Helpers.AnimationHelper;
import com.alburagh.alburagh.Helpers.AppHelper;
import com.alburagh.alburagh.Helpers.DownloadManager;
import com.alburagh.alburagh.Helpers.Setting;
import com.alburagh.alburagh.Helpers.UIHelper;
import com.alburagh.alburagh.MainActivity;
import com.alburagh.alburagh.R;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    public FavoriteFragment() {}

    ContentsFragment booksFragment;
    ContentsFragment soundBooksFragment;
    ContentsFragment gamesFragment;

    ImageButton sound_books_filter_button,books_filter_button,games_filter_button,backButton,homeButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        games_filter_button         = view.findViewById(R.id.games_filter_button);
        books_filter_button         = view.findViewById(R.id.books_filter_button);
        sound_books_filter_button   = view.findViewById(R.id.sound_books_filter_button);
        homeButton                  = view.findViewById(R.id.home_button);

        Animation slide_in_up = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_up);
        slide_in_up.setDuration(1500);
        view.findViewById(R.id.container).startAnimation(slide_in_up);

        books_filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBooks(view, booksFragment);
                UIHelper.playButtonSound(getActivity());
            }
        });
        sound_books_filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSoundBooks(view, soundBooksFragment);
                UIHelper.playButtonSound(getActivity());
            }
        });
        games_filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGames(view, gamesFragment);
                UIHelper.playButtonSound(getActivity());
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
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

                view.findViewById(R.id.games_place_holder).setVisibility(View.GONE);
                view.findViewById(R.id.sound_books_place_holder).setVisibility(View.GONE);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                Bundle booksBundle = new Bundle();
                booksBundle.putSerializable("type", ContentsFragment.ContentType.BOOKS);
                booksBundle.putBoolean("favorite", true);
                booksFragment = new ContentsFragment();
                booksFragment.setArguments(booksBundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.books_place_holder, booksFragment)
                        .commit();

                Bundle gamesBundle = new Bundle();
                gamesBundle.putSerializable("type", ContentsFragment.ContentType.GAMES);
                gamesBundle.putSerializable("favorite", true);
                gamesFragment = new ContentsFragment();
                gamesFragment.setArguments(gamesBundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.games_place_holder, gamesFragment)
                        .commit();

                Bundle soundBooksBundle = new Bundle();
                soundBooksBundle.putSerializable("type", ContentsFragment.ContentType.SOUND_BOOKS);
                soundBooksBundle.putSerializable("favorite", true);
                soundBooksFragment = new ContentsFragment();
                soundBooksFragment.setArguments(soundBooksBundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.sound_books_place_holder, soundBooksFragment)
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

                UIHelper.fitViewToBackground(so, view.findViewById(R.id.home_button), 50, 50, UIHelper.intArrayToPoint(getActivity(), R.array.list_home), buttonScale);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.books_filter_button), 90, 31, UIHelper.intArrayToPoint(getActivity(), R.array.favorite_books), buttonScale);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.games_filter_button), 90, 31, UIHelper.intArrayToPoint(getActivity(), R.array.favorite_games), buttonScale);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.sound_books_filter_button), 90, 31, UIHelper.intArrayToPoint(getActivity(), R.array.favorite_sound_books), buttonScale);

                UIHelper.fitLayoutToBackground(so, view.findViewById(R.id.books_place_holder), 1416, view.getHeight()+100 , 245, 241);
                UIHelper.fitLayoutToBackground(so, view.findViewById(R.id.games_place_holder), 1416, view.getHeight()+100 , 245, 241);
                UIHelper.fitLayoutToBackground(so, view.findViewById(R.id.sound_books_place_holder), 1416, view.getHeight()+100 , 245, 241);

                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.books_filter_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.games_filter_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.sound_books_filter_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.home_button));

                initAnimations(view);

                if (Setting.getInstance().getVariable("last_favorite_type").equals("books"))
                    showBooks(view, booksFragment);
                else if (Setting.getInstance().getVariable("last_favorite_type").equals("sound_books"))
                    showBooks(view, soundBooksFragment);
                else
                    showGames(view, gamesFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        booksFragment = null;
        gamesFragment = null;
        soundBooksFragment = null;
    }

    public void showBooks(View layout, ContentsFragment fragment) {
        layout.findViewById(R.id.books_place_holder).setVisibility(View.VISIBLE);
        layout.findViewById(R.id.sound_books_place_holder).setVisibility(View.INVISIBLE);
        layout.findViewById(R.id.games_place_holder).setVisibility(View.INVISIBLE);
        layout.findViewById(R.id.books_filter_button).setBackgroundResource(R.drawable.favorite_books_button_active);
        layout.findViewById(R.id.games_filter_button).setBackgroundResource(R.drawable.favorite_games_button);
        layout.findViewById(R.id.sound_books_filter_button).setBackgroundResource(R.drawable.favorite_sound_books_button);

        if (fragment != null)
            DownloadManager.getInstance().setListener(fragment.listener);

        Setting.getInstance().setVariable("last_favorite_type", "books");
    }

    public void showGames(View layout, ContentsFragment fragment) {
        layout.findViewById(R.id.books_place_holder).setVisibility(View.INVISIBLE);
        layout.findViewById(R.id.sound_books_place_holder).setVisibility(View.INVISIBLE);
        layout.findViewById(R.id.games_place_holder).setVisibility(View.VISIBLE);
        layout.findViewById(R.id.books_filter_button).setBackgroundResource(R.drawable.favorite_books_button);
        layout.findViewById(R.id.games_filter_button).setBackgroundResource(R.drawable.favorite_games_button_active);
        layout.findViewById(R.id.sound_books_filter_button).setBackgroundResource(R.drawable.favorite_sound_books_button);

        if (fragment != null)
            DownloadManager.getInstance().setListener(fragment.listener);

        Setting.getInstance().setVariable("last_favorite_type", "games");
    }

    public void showSoundBooks(View layout, ContentsFragment fragment) {
        layout.findViewById(R.id.books_place_holder).setVisibility(View.INVISIBLE);
        layout.findViewById(R.id.games_place_holder).setVisibility(View.INVISIBLE);
        layout.findViewById(R.id.sound_books_place_holder).setVisibility(View.VISIBLE);
        layout.findViewById(R.id.books_filter_button).setBackgroundResource(R.drawable.favorite_books_button);
        layout.findViewById(R.id.games_filter_button).setBackgroundResource(R.drawable.favorite_games_button);
        layout.findViewById(R.id.sound_books_filter_button).setBackgroundResource(R.drawable.favorite_sound_books_button_active);

        if (fragment != null)
            DownloadManager.getInstance().setListener(fragment.listener);

        Setting.getInstance().setVariable("last_favorite_type", "sound_books");
    }



    //==============================================================================================
    //== Animations
    //==============================================================================================

    public void initAnimations (View layout) {
        List<View> buttons = new ArrayList<>();
        buttons.add(layout.findViewById(R.id.home_button));
        buttons.add(layout.findViewById(R.id.books_filter_button));
        buttons.add(layout.findViewById(R.id.games_filter_button));
        buttons.add(layout.findViewById(R.id.sound_books_filter_button));
        AnimationHelper.getInstance().buttonsAnimation(buttons);
    }
}
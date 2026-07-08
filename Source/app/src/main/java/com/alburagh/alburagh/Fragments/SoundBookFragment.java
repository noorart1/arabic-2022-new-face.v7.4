package com.alburagh.alburagh.Fragments;

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

import com.alburagh.alburagh.Helpers.AnimationHelper;
import com.alburagh.alburagh.Helpers.UIHelper;
import com.alburagh.alburagh.MainActivity;
import com.alburagh.alburagh.R;

import java.util.ArrayList;
import java.util.List;

public class SoundBookFragment extends Fragment {

    public SoundBookFragment() {}

    ImageButton homeButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sound_book, container, false);

        homeButton = view.findViewById(R.id.home_button);
        MainActivity mainActivity = (MainActivity) getActivity();
        Animation slide_in_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_up);
        slide_in_down.setDuration(1500);
        view.findViewById(R.id.container).startAnimation(slide_in_down);
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

                Bundle bundle = new Bundle();
                bundle.putSerializable("type", ContentsFragment.ContentType.SOUND_BOOKS);

                ContentsFragment contentsFragment = new ContentsFragment();
                contentsFragment.setArguments(bundle);

                getChildFragmentManager().beginTransaction().
                        replace(R.id.place_holder, contentsFragment)
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

                UIHelper.fitViewToBackground(so, view.findViewById(R.id.home_button), 50, 50, UIHelper.intArrayToPoint(getActivity(), R.array.menu_home), buttonScale);

                UIHelper.fitLayoutToBackground(so, view.findViewById(R.id.place_holder), 1416, view.getHeight()+100 , 245, 241);

                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.home_button));

                initAnimations(view);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser)
            getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



    //==============================================================================================
    //== Animations
    //==============================================================================================

    public void initAnimations(View layout) {

        List<View> buttons = new ArrayList<>();
        buttons.add(layout.findViewById(R.id.home_button));
        AnimationHelper.getInstance().buttonsAnimation(buttons);
    }
}
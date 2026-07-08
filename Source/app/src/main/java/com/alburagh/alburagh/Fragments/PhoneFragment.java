package com.alburagh.alburagh.Fragments;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.alburagh.alburagh.Helpers.UIHelper;
import com.alburagh.alburagh.R;

public class PhoneFragment extends Fragment {

    public PhoneFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
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
            }
        });
    }
}

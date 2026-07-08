package com.alburagh.alburagh.Fragments.Account;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import com.alburagh.alburagh.Helpers.UIHelper;
import com.alburagh.alburagh.MainActivity;
import com.alburagh.alburagh.R;

public class AccountWarningFragment extends Fragment {

    boolean closeable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_warning, container, false);

        view.findViewById(R.id.main_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closeable)
                    getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        view.findViewById(R.id.exit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        closeable = getArguments().getBoolean("closeable");

        view.findViewById(R.id.subscribe_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.showAccountView(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) getActivity()).showAccountWarning(false);
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver observer = view.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    observer.removeOnGlobalLayoutListener(this);
                else
                    observer.removeGlobalOnLayoutListener(this);

                UIHelper.ScreenOptions so = new UIHelper.ScreenOptions(getActivity(), view);

                UIHelper.fitSize(so, view.findViewById(R.id.face), R.drawable.account_warning_face);
                UIHelper.fitSize(so, view.findViewById(R.id.eyes), R.drawable.account_warning_eyes);

                UIHelper.fitViewToBackground(so, view.findViewById(R.id.message), new Point(960, 400));

                UIHelper.fitLayoutToBackground(so, view.findViewById(R.id.subscribe_button), 300, 90, 630, 711);
                UIHelper.fitLayoutToBackground(so, view.findViewById(R.id.exit_button), 300, 90, 1020, 711);

                UIHelper.setupAccountButton((Button) view.findViewById(R.id.subscribe_button), AccountFragment.GREEN_COLOR, Color.BLACK);
                UIHelper.setupAccountButton((Button) view.findViewById(R.id.exit_button), AccountFragment.RED_COLOR, Color.BLACK);
            }
        });
    }
}

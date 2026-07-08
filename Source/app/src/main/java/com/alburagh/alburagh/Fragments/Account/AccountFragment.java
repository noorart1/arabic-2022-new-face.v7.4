package com.alburagh.alburagh.Fragments.Account;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.alburagh.alburagh.Helpers.AppHelper;
import com.alburagh.alburagh.Helpers.UIHelper;
import com.alburagh.alburagh.Helpers.UserManager;
import com.alburagh.alburagh.R;
import com.alburagh.alburagh.UI.AlburaghButton;

public class AccountFragment extends Fragment {

    public static int RED_COLOR = Color.parseColor("#ff1518");
    public static int YELLOW_COLOR = Color.parseColor("#ffec00");
    public static int GREEN_COLOR = Color.parseColor("#97d500");
    public static int PURPLE_COLOR = Color.parseColor("#ce75cd");
    public static int CYAN_COLOR = Color.parseColor("#8bddd8");
    public static int BLUE_COLOR = Color.parseColor("#1616ec");
    public static int DARK_RED_COLOR = Color.parseColor("#b30000");

    public AccountFragment() {}

    RelativeLayout tabPlaceHolder;
    ScrollView tabScrollview;
    AlburaghButton registerButton;
    AlburaghButton codeButton;
    AlburaghButton account_button;

    public Runnable onClose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        tabPlaceHolder = view.findViewById(R.id.tab_place_holder);
        tabScrollview = view.findViewById(R.id.tab_scrollview);
        registerButton = view.findViewById(R.id.register_button);
        codeButton = view.findViewById(R.id.code_button);
        account_button = view.findViewById(R.id.account_button);

        Animation slide_in_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_up);
        view.findViewById(R.id.account_container).startAnimation(slide_in_down);

        RelativeLayout mainLayout = view.findViewById(R.id.main_layout);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClose != null) {
                    onClose.run();
                    onClose = null;
                }

                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        Button loginButton = view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginTab();
                UIHelper.playButtonSound(getActivity());
            }
        });

        Button registerButton = view.findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.playButtonSound(getActivity());
                showRegisterTab();
            }
        });

        Button subscriptionButton = view.findViewById(R.id.account_button);
        subscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.setupAccountTabBackground(tabScrollview, PURPLE_COLOR);
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.tab_place_holder, new AccountSubscriptionFragment())
                        .commit();
                UIHelper.playButtonSound(getActivity());
            }
        });

        Button codeButton = view.findViewById(R.id.code_button);
        codeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.setupAccountTabBackground(tabScrollview, CYAN_COLOR);
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.tab_place_holder, new AccountCodeFragment())
                        .commit();
                UIHelper.playButtonSound(getActivity());
            }
        });

        UIHelper.setupAccountTabButton(loginButton, YELLOW_COLOR);
        UIHelper.setupAccountTabButton(registerButton, GREEN_COLOR);
        UIHelper.setupAccountTabButton(subscriptionButton, PURPLE_COLOR);
        UIHelper.setupAccountTabButton(codeButton, CYAN_COLOR);

        showLoginTab();
        fixTabsVisibility();
        return view;
    }

    @Override
    public void onDestroyView() {
        onClose = null;
        super.onDestroyView();
    }

    public void showLoginTab(String mail, String password) {
        UIHelper.setupAccountTabBackground(tabScrollview, YELLOW_COLOR);
        if (UserManager.getInstance().isLoggedIn) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.tab_place_holder, new AccountDetailsFragment())
                    .commit();
        } else {
            AccountLoginFragment accountLoginFragment = new AccountLoginFragment();
            if (mail != null && password != null) {
                Bundle bundle = new Bundle();
                bundle.putString("mail", mail);
                bundle.putString("password", password);
                accountLoginFragment.setArguments(bundle);
            }

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.tab_place_holder, accountLoginFragment)
                    .commit();
        }
    }

    public void showLoginTab() {
        showLoginTab(null, null);
    }

    public void showRegisterTab() {
        UIHelper.setupAccountTabBackground(tabScrollview, GREEN_COLOR);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_place_holder, new AccountRegisterFragment())
                .commit();
    }

    public void showResetPassword() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_place_holder, new AccountResetPasswordFragment())
                .commit();
    }

    public void showChangePassword() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_place_holder, new AccountChangePasswordFragment())
                .commit();
    }

    public void fixTabsVisibility() {
        if (UserManager.getInstance().isLoggedIn)
            registerButton.setVisibility(View.GONE);
        else
            registerButton.setVisibility(View.VISIBLE);

        if (AppHelper.SubscriptionExist)
            codeButton.setVisibility(View.GONE);
        else {
            codeButton.setVisibility(View.VISIBLE);

            // try {
            //     User user = UserManager.getInstance().user;
            //     if (Long.parseLong(user.expire) > System.currentTimeMillis())
            //         account_button.setVisibility(View.GONE);
            // } catch (Exception e) {
            //     account_button.setVisibility(View.VISIBLE);
            //     Log.e("ERROR", e.getMessage());
            // }
        }
    }
}
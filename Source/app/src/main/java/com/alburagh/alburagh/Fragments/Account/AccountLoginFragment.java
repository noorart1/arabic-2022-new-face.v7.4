package com.alburagh.alburagh.Fragments.Account;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alburagh.alburagh.Helpers.Setting;
import com.alburagh.alburagh.Helpers.UIHelper;
import com.alburagh.alburagh.Helpers.UserManager;
import com.alburagh.alburagh.MainActivity;
import com.alburagh.alburagh.Models.User;
import com.alburagh.alburagh.R;

public class AccountLoginFragment extends Fragment {
    EditText email;
    EditText password;
    TextView error;

    public AccountLoginFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_login, container, false);

        email = (EditText) view.findViewById(R.id.login_email);
        password = (EditText) view.findViewById(R.id.login_password);
        error = (TextView) view.findViewById(R.id.error_label);

        // Set Last email and pass
        String lastMail = Setting.getInstance().getVariable("last_mail");
        email.setText(lastMail);

        String lastPass = Setting.getInstance().getVariable("last_pass");
        password.setText(lastPass);

        // Set Arguments email ant pass (Override last email and pass)
        if (getArguments() != null) {
            if (getArguments().getString("mail") != null && getArguments().getString("mail", "").length() > 0) {
                email.setText(getArguments().getString("mail", ""));
                password.setText(getArguments().getString("password"));
            }
        }

        Button submit = (Button) view.findViewById(R.id.login_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
                UIHelper.playButtonSound(getActivity());
            }
        });

        TextView forgotPassword = (TextView) view.findViewById(R.id.forgot_password_button);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountFragment accountFragment = (AccountFragment) getParentFragment();
                accountFragment.showResetPassword();
                UIHelper.playButtonSound(getActivity());
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                error.setText("");
            }
        };
        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);

        UIHelper.setupAccountButton(submit, AccountFragment.YELLOW_COLOR, Color.BLACK);
        UIHelper.setupAccountTextButton(forgotPassword);

        view.findViewById(R.id.main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).hideSoftKeyboard();
            }
        });

        return view;
    }

    public void login() {
        ((MainActivity) getActivity()).showLoadingLayout();
        ((MainActivity) getActivity()).hideSoftKeyboard();

        UserManager.getInstance().login(email.getText().toString(), password.getText().toString(), new UserManager.LoginCallback() {
            @Override
            public void success(User user) {
                UserManager.getInstance().isLoggedIn = true;
                UserManager.getInstance().user = user;

                UserManager.getInstance().setUserExpire(Long.valueOf(user.expire) * 1000);

                Setting.getInstance().setVariable("last_mail", email.getText().toString());
                Setting.getInstance().setVariable("last_pass", password.getText().toString());

                AccountFragment accountFragment = (AccountFragment) getParentFragment();
                accountFragment.showLoginTab();
                accountFragment.fixTabsVisibility();

                ((MainActivity) getActivity()).hideLoadingLayout();
            }

            @Override
            public void failed() {
                error.setText(R.string.account_error_login);
                ((MainActivity) getActivity()).hideLoadingLayout();
            }
        });
    }


}
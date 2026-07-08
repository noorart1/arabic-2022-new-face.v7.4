package com.alburagh.alburagh.Fragments.Account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alburagh.alburagh.Helpers.UIHelper;
import com.alburagh.alburagh.Helpers.UserManager;
import com.alburagh.alburagh.MainActivity;
import com.alburagh.alburagh.Models.User;
import com.alburagh.alburagh.R;

import java.util.Locale;

public class AccountCodeFragment extends Fragment {
    EditText code;
    TextView remaining;
    TextView errorLabel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_code, container, false);

        code = view.findViewById(R.id.code_code);
        remaining = view.findViewById(R.id.code_remaining_days);
        errorLabel = view.findViewById(R.id.error_label);

        if (UserManager.getInstance().isLoggedIn)
            showRemainingDays();

        Button submit = view.findViewById(R.id.code_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCode();
                UIHelper.playButtonSound(getActivity());
            }
        });

        UIHelper.setupAccountTextButton(submit);
        UIHelper.setupAccountButton(submit, AccountFragment.CYAN_COLOR, Color.BLACK);

        view.findViewById(R.id.main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).hideSoftKeyboard();
            }
        });

        return view;
    }

    public void submitCode() {
        if (!UserManager.getInstance().isLoggedIn) {
            new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.code_login_message)
                    .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AccountFragment fragment = (AccountFragment) getParentFragment();
                            fragment.showLoginTab();
                        }
                    })
                    .setNegativeButton(R.string.register, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AccountFragment fragment = (AccountFragment) getParentFragment();
                            fragment.showRegisterTab();
                        }
                    })
                    .show();

            return;
        }

        ((MainActivity) getActivity()).hideSoftKeyboard();

        UserManager.getInstance().submitCode(UserManager.getInstance().user.mail, code.getText().toString(), new UserManager.SubmitCodeCallback() {
            @Override
            public void success(long expireTimestamp) {
                UserManager.getInstance().setUserExpire(expireTimestamp * 1000);
                showRemainingDays();
                code.setText("");

                try {
                    new AlertDialog.Builder(getActivity())
                            .setMessage(R.string.code_submitted_message)
                            .setPositiveButton(R.string.ok, null)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed() {
                try {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error)
                            .setMessage(R.string.code_submit_error_message)
                            .setPositiveButton(R.string.ok, null)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showRemainingDays () {
        User user = UserManager.getInstance().user;
        if (user.expire.equals("0"))
            return;

        if (Long.parseLong(user.expire) < System.currentTimeMillis())
            remaining.setText(R.string.no_remaining_time);
        else
            remaining.setText(String.format(Locale.ENGLISH, getResources().getString(R.string.code_remaining_text), user.expireDays, user.expireHours));
    }
}

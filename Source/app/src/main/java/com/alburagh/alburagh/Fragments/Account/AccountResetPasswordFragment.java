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

import com.alburagh.alburagh.Helpers.Setting;
import com.alburagh.alburagh.Helpers.UIHelper;
import com.alburagh.alburagh.Helpers.UserManager;
import com.alburagh.alburagh.MainActivity;
import com.alburagh.alburagh.R;

public class AccountResetPasswordFragment extends Fragment {
    public AccountResetPasswordFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_account_reset_passwod, container, false);

        Button submit = (Button) view.findViewById(R.id.change_pass_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.playButtonSound(getActivity());
                resetPassword(view);
            }
        });

        TextView back = (TextView) view.findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.playButtonSound(getActivity());
                AccountFragment accountFragment = (AccountFragment) getParentFragment();
                accountFragment.showLoginTab();
            }
        });

        UIHelper.setupAccountButton(submit, AccountFragment.YELLOW_COLOR, Color.BLACK);
        UIHelper.setupAccountTextButton(back);

        view.findViewById(R.id.main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).hideSoftKeyboard();
            }
        });

        return view;
    }

    public void resetPassword (View layout) {
        ((MainActivity) getActivity()).hideSoftKeyboard();

        EditText email = (EditText) layout.findViewById(R.id.change_pass_email);
        final TextView errorLabel = (TextView) layout.findViewById(R.id.error_label);

        UserManager.getInstance().resetPassword(email.getText().toString(), new UserManager.ResetPasswordCallback() {
            @Override
            public void success() {
                try {
                    Setting.getInstance().setVariable("last_pass", "");
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.account_password_reset_alert_title)
                            .setMessage(R.string.account_password_reset_alert_message)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AccountFragment accountFragment = (AccountFragment) getParentFragment();
                                    accountFragment.showLoginTab();
                                }
                            })
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed() {
                errorLabel.setText(R.string.account_error_reset_password);
            }
        });
    }
}
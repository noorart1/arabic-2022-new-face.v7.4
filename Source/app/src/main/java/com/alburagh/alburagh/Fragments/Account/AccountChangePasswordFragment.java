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

public class AccountChangePasswordFragment extends Fragment {

    EditText currentPassword;
    EditText newPassword;
    EditText confirmNewPassword;
    TextView errorLabel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_change_password, container, false);

        currentPassword = (EditText) view.findViewById(R.id.change_current_password);
        newPassword = (EditText) view.findViewById(R.id.change_new_password);
        confirmNewPassword = (EditText) view.findViewById(R.id.change_confirm_new_password);
        errorLabel = (TextView) view.findViewById(R.id.error_label);

        TextView backButton = (TextView) view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountFragment accountFragment = (AccountFragment) getParentFragment();
                accountFragment.showLoginTab();
            }
        });

        Button submit = (Button) view.findViewById(R.id.change_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        UIHelper.setupAccountTextButton(backButton);
        UIHelper.setupAccountButton(submit, AccountFragment.YELLOW_COLOR, Color.BLACK);

        view.findViewById(R.id.main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).hideSoftKeyboard();
            }
        });

        return view;
    }

    public void changePassword () {
        ((MainActivity) getActivity()).hideSoftKeyboard();

        // Todo: validate inputs
        UserManager.getInstance().changePassword(UserManager.getInstance().user.mail,
                currentPassword.getText().toString(),
                newPassword.getText().toString(),
                new UserManager.ChangePasswordCallback() {
                    @Override
                    public void success() {
                        try {
                            Setting.getInstance().setVariable("last_pass", "");
                            new AlertDialog.Builder(getActivity())
                                    .setMessage(R.string.change_pass_password_changed_message)
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
                        errorLabel.setText(R.string.account_error_change_password);
                    }
                });
    }
}
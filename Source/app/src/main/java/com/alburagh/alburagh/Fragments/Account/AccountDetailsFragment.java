package com.alburagh.alburagh.Fragments.Account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alburagh.alburagh.Helpers.UIHelper;
import com.alburagh.alburagh.Helpers.UserManager;
import com.alburagh.alburagh.R;

public class AccountDetailsFragment extends Fragment {

    public AccountDetailsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_details, container, false);

        TextView changePasswordButton = (TextView) view.findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountFragment accountFragment = (AccountFragment) getParentFragment();
                accountFragment.showChangePassword();
            }
        });

        TextView logoutButton = (TextView) view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.account_details_logout_message)
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManager.getInstance().logout();

                                AccountFragment accountFragment = (AccountFragment) getParentFragment();
                                accountFragment.showLoginTab();
                                accountFragment.fixTabsVisibility();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), null)
                        .show();
            }
        });

        UIHelper.setupAccountTextButton(changePasswordButton);
        UIHelper.setupAccountTextButton(logoutButton);

        return view;
    }
}
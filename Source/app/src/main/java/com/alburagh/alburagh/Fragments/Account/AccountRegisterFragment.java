package com.alburagh.alburagh.Fragments.Account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.alburagh.alburagh.Helpers.AppHelper;
import com.alburagh.alburagh.Helpers.UIHelper;
import com.alburagh.alburagh.Helpers.UserManager;
import com.alburagh.alburagh.MainActivity;
import com.alburagh.alburagh.Models.Country;
import com.alburagh.alburagh.R;
import com.alburagh.alburagh.UI.AlburaghTextView;

import java.util.List;

public class AccountRegisterFragment extends Fragment {

    EditText email;
    EditText password;
    EditText confirmPassword;
    Spinner country;
    EditText fullname;

    ImageView emailStatus;
    ProgressBar emailProgress;
    ImageView passwordStatus;
    ImageView confirmPasswordStatus;

    TextView emailError;
    TextView passwordError;
    TextView confirmPasswordError;

    List<Country> countryList;

    boolean emailOK;
    boolean passwordOK;
    boolean confirmPasswordOK;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_register, container, false);

        email = (EditText) view.findViewById(R.id.register_email);
        emailStatus = (ImageView) view.findViewById(R.id.email_status);
        emailProgress = (ProgressBar) view.findViewById(R.id.email_progress);
        password = (EditText) view.findViewById(R.id.register_password);
        passwordStatus = (ImageView) view.findViewById(R.id.password_status);
        confirmPassword = (EditText) view.findViewById(R.id.register_re_password);
        confirmPasswordStatus = (ImageView) view.findViewById(R.id.re_password_status);
        fullname = (EditText) view.findViewById(R.id.register_full_name);

        emailError = (TextView) view.findViewById(R.id.email_error);
        passwordError = (TextView) view.findViewById(R.id.password_error);
        confirmPasswordError = (TextView) view.findViewById(R.id.re_password_error);

        country = (Spinner) view.findViewById(R.id.register_country);
        countryList = AppHelper.getCountryList(getActivity(), AppHelper.LANGUAGE);
        country.setAdapter(new CountriesAdaptor(countryList));
        country.setSelection(0);

        Button register = (Button) view.findViewById(R.id.register_submit);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.playButtonSound(getActivity());
                register();
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    emailValidate();
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    passwordValidate();
            }
        });

        confirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    confirmPasswordValidate();
            }
        });

        UIHelper.setupAccountButton(register, AccountFragment.GREEN_COLOR, Color.BLACK);

        view.findViewById(R.id.main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).hideSoftKeyboard();
            }
        });


        String userEmail = UserManager.getInstance().getUsername(getActivity());
        email.setText(userEmail);

        return view;
    }

    public void emailValidate() {
        final Runnable errorReady = new Runnable() {
            @Override
            public void run() {
                emailStatus.setImageResource(R.drawable.error_48);
                emailStatus.setVisibility(View.VISIBLE);
                emailProgress.setVisibility(View.INVISIBLE);
            }
        };

        emailProgress.setVisibility(View.VISIBLE);
        emailStatus.setVisibility(View.INVISIBLE);
        emailError.setText("");

        if (TextUtils.isEmpty(email.getText())) {
            errorReady.run();
            emailError.setText(R.string.register_password_required);
            emailOK = false;
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
            errorReady.run();
            emailError.setText(R.string.register_mail_not_valid);
            emailOK = false;
            return;
        }

        UserManager.getInstance().checkValidMail(email.getText().toString(), new UserManager.CheckMailValidCallback() {
            @Override
            public void success() {
                emailStatus.setImageResource(R.drawable.correct_48);
                emailStatus.setVisibility(View.VISIBLE);
                emailProgress.setVisibility(View.INVISIBLE);
                emailOK = true;
            }

            @Override
            public void failed(String error) {
                errorReady.run();
                emailError.setText(error);
                emailOK = false;
            }
        });
    }

    public void passwordValidate() {
        final Runnable errorReady = new Runnable() {
            @Override
            public void run() {
                passwordStatus.setImageResource(R.drawable.error_48);
                passwordStatus.setVisibility(View.VISIBLE);
            }
        };

        passwordError.setText("");

        if (TextUtils.isEmpty(password.getText())) {
            errorReady.run();
            passwordError.setText(R.string.register_password_required);
            passwordOK = false;
            return;
        }

        if (password.getText().length() < 6) {
            errorReady.run();
            passwordError.setText(R.string.register_password_too_short);
            passwordOK = false;
            return;
        }

        UserManager.getInstance().checkValidPassword(password.getText().toString(), new UserManager.CheckPasswordCallback() {
            @Override
            public void success() {
                passwordStatus.setImageResource(R.drawable.correct_48);
                passwordStatus.setVisibility(View.VISIBLE);
                passwordOK = true;
            }

            @Override
            public void failed(String error) {
                errorReady.run();
                passwordError.setText(error);
                passwordOK = false;
            }
        });

        if (confirmPassword.getText().length() > 0)
            confirmPasswordValidate();
    }

    public void confirmPasswordValidate() {
        confirmPasswordError.setText("");
        confirmPasswordStatus.setVisibility(View.INVISIBLE);

        if (confirmPassword.getText().length() > 0) {
            if (password.getText().toString().equals(confirmPassword.getText().toString())) {
                confirmPasswordStatus.setImageResource(R.drawable.correct_48);
                confirmPasswordOK = true;
            } else {
                confirmPasswordError.setText(R.string.register_password_confirm_not_match);
                confirmPasswordStatus.setImageResource(R.drawable.error_48);
                confirmPasswordOK = false;
            }
            confirmPasswordStatus.setVisibility(View.VISIBLE);
        }
    }

    public boolean validation() {
        emailValidate();
        passwordValidate();
        confirmPasswordValidate();
        return emailOK && passwordOK && confirmPasswordOK;
    }

    public void register() {
        if (!validation())
            return;

        ((MainActivity) getActivity()).showLoadingLayout();
        ((MainActivity) getActivity()).hideSoftKeyboard();
        UserManager.getInstance().register(email.getText().toString(),
                password.getText().toString(),
                fullname.getText().toString(),
                countryList.get(country.getSelectedItemPosition()).ISO2,
                new UserManager.RegisterCallback() {
                    @Override
                    public void success() {
                        try {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage(R.string.register_successful_message)
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ((AccountFragment) getParentFragment()).showLoginTab(email.getText().toString(), password.getText().toString());
                                        }
                                    })
                                    .show();
                            ((MainActivity) getActivity()).hideLoadingLayout();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failed() {
                        try {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage(R.string.register_unsuccessful_message)
                                    .setPositiveButton(R.string.ok, null)
                                    .show();
                            ((MainActivity) getActivity()).hideLoadingLayout();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private class CountriesAdaptor extends BaseAdapter implements SpinnerAdapter {
        List<Country> countries;

        public CountriesAdaptor(List<Country> list) {
            countries = list;
        }

        @Override
        public int getCount() {
            return countries.size();
        }

        @Override
        public Object getItem(int position) {
            return countries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AlburaghTextView textView = new AlburaghTextView(getActivity(), null);
            textView.setPadding(5, 2, 5, 4);
            textView.setGravity(Gravity.LEFT);

            if (position == 0)
                textView.setTextColor(Color.parseColor("#666666"));

            textView.setTextSize(16.5f);

            if (AppHelper.LANGUAGE.equals("ar"))
                textView.setText(countries.get(position).arabicName);
            else if (AppHelper.LANGUAGE.equals("fa"))
                textView.setText(countries.get(position).farsiName);

            return textView;
        }
    }
}
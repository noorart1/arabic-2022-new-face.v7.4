package com.alburagh.alburagh.Fragments.Account;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alburagh.alburagh.Helpers.AppHelper;
import com.alburagh.alburagh.Helpers.Setting;
import com.alburagh.alburagh.Helpers.UIHelper;
import com.alburagh.alburagh.Helpers.UserManager;
import com.alburagh.alburagh.IAB.IabHelper;
import com.alburagh.alburagh.IAB.IabResult;
import com.alburagh.alburagh.MainActivity;
import com.alburagh.alburagh.Models.Subscription;
import com.alburagh.alburagh.R;

public class AccountSubscriptionFragment extends Fragment {

    public AccountSubscriptionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_account_subscription, container, false);

        final MainActivity mainActivity = (MainActivity) getActivity();
        showLayouts(view, true, false, false, false);

        if (mainActivity.BILLING_NOT_SUPPORTED) {
            showLayouts(view, false, false, false, true);
        } else {
            UIHelper.setupAccountTextButton((TextView) view.findViewById(R.id.sub_restore_button));
            UIHelper.setupAccountBuy((Button) view.findViewById(R.id.sub_monthly_buy_button), Color.WHITE);
            UIHelper.setupAccountBuy((Button) view.findViewById(R.id.sub_annual_buy_button), Color.WHITE);
            UIHelper.setupAccountBuy((Button) view.findViewById(R.id.sub_6month_buy_button), Color.WHITE);

            view.findViewById(R.id.sub_restore_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIHelper.playButtonSound(getActivity());
                }
            });

            view.findViewById(R.id.sub_monthly_buy_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UserManager.getInstance().isLoggedIn) {
                        try {
                            UIHelper.playButtonSound(getActivity());
                            showLayouts(view, true, false, false, false);
                            mainActivity.buySubscription("monthly_subs", Subscription.Type.MONTHLY, new MainActivity.PurchaseCallback() {
                                @Override
                                public void onSubscriptionPurchased(String token) {
                                    showLayouts(view, false, false, true, false);
                                    Log.i("s", "onSubscriptionPurchased: " + token);
                                    Setting.getInstance().setVariable("sub_token", token);
                                }

                                @Override
                                public void failed(IabResult result) {
                                    int message = R.string.purchase_unsuccessful;
                                    switch (result.getResponse()) {
                                        case -1002:
                                        case -1003:
                                        case -1004:
                                        case -1005:
                                        case -1006:
                                        case -1007:
                                        case -1008:
                                        case -1009:
                                        case -1010:
                                            break;
                                    }

                                    new AlertDialog.Builder(getActivity())
                                            .setMessage(message)
                                            .setPositiveButton(R.string.ok, null)
                                            .show();

                                    showLayouts(view, false, true, false, false);

                                }
                            });
                        } catch (Exception e) {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage(R.string.purchase_error)
                                    .setPositiveButton(R.string.ok, null)
                                    .show();

                            showLayouts(view, false, true, false, false);
                        }
                    } else {
                        new android.app.AlertDialog.Builder(getActivity())
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
                    }
                }
            });

            view.findViewById(R.id.sub_6month_buy_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UserManager.getInstance().isLoggedIn) {
                        try {
                            UIHelper.playButtonSound(getActivity());
                            showLayouts(view, true, false, false, false);
                            mainActivity.buySubscription("6month_subs", Subscription.Type.MONTHLY, new MainActivity.PurchaseCallback() {
                                @Override
                                public void onSubscriptionPurchased(String token) {
                                    showLayouts(view, false, false, true, false);
                                    Setting.getInstance().setVariable("sub_token", token);
                                }

                                @Override
                                public void failed(IabResult result) {
                                    int message = R.string.purchase_unsuccessful;
                                    switch (result.getResponse()) {
                                        case -1002:
                                        case -1003:
                                        case -1004:
                                        case -1005:
                                        case -1006:
                                        case -1007:
                                        case -1008:
                                        case -1009:
                                        case -1010:
                                            break;
                                    }

                                    new AlertDialog.Builder(getActivity())
                                            .setMessage(message)
                                            .setPositiveButton(R.string.ok, null)
                                            .show();

                                    showLayouts(view, false, true, false, false);
                                }
                            });
                        } catch (Exception e) {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage(R.string.purchase_error)
                                    .setPositiveButton(R.string.ok, null)
                                    .show();

                            showLayouts(view, false, true, false, false);
                        }
                    } else {
                        new android.app.AlertDialog.Builder(getActivity())
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
                    }
                }
            });

            view.findViewById(R.id.sub_annual_buy_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UserManager.getInstance().isLoggedIn) {
                        try {
                            UIHelper.playButtonSound(getActivity());
                            showLayouts(view, true, false, false, false);
                            mainActivity.buySubscription("yearly_subs", Subscription.Type.ANNUAL, new MainActivity.PurchaseCallback() {
                                @Override
                                public void onSubscriptionPurchased(String token) {
                                    showLayouts(view, false, false, true, false);
                                    Setting.getInstance().setVariable("sub_token", token);

                                }

                                @Override
                                public void failed(IabResult result) {
                                    int message = R.string.purchase_unsuccessful;
                                    switch (result.getResponse()) {
                                        case -1002:
                                        case -1003:
                                        case -1004:
                                        case -1005:
                                        case -1006:
                                        case -1007:
                                        case -1008:
                                        case -1009:
                                        case -1010:
                                            break;
                                    }

                                    new AlertDialog.Builder(getActivity())
                                            .setMessage(message)
                                            .setPositiveButton(R.string.ok, null)
                                            .show();

                                    showLayouts(view, false, true, false, false);
                                }
                            });
                        } catch (Exception e) {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage(R.string.purchase_error)
                                    .setPositiveButton(R.string.ok, null)
                                    .show();

                            showLayouts(view, false, true, false, false);
                        }
                    } else {
                        new android.app.AlertDialog.Builder(getActivity())
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
                    }
                }
            });

            mainActivity.initIAB(new MainActivity.InitIABCallback() {
                @Override
                public void onReceivedInventory(Subscription subscription, String monthlyPrice, String annualPrice) {
                    try {
                        if (IabHelper.subsexist) {
                            showLayouts(view, false, false, true, false);
                            Log.i("aaa", "onReceivedInventory: aren " + subscription);
                            AppHelper.SubscriptionExist = true;
                        } else {
                            showLayouts(view, false, true, false, false);
                            Log.i("aaa", "onReceivedInventory: are " + subscription);
                            AppHelper.SubscriptionExist = false;
                        }
                    } catch (Exception e) {
                        Log.d(AppHelper.TAG, e.toString());
                    }
                }

                @Override
                public void failed(IabResult result) {
                    try {
                        int message = R.string.purchase_service_unavailable;
                        switch (result.getResponse()) {
                            case 1:
                            case 2:
                            case 4:
                            case 5:
                            case 7:
                            case 8:
                                break;
                            case 3:
                            case 6:
                                message = R.string.purchase_service_unavailable;
                                new AlertDialog.Builder(getActivity())
                                        .setMessage(message)
                                        .setPositiveButton(R.string.ok, null)
                                        .show();
                                break;
                        }

                        if (IabHelper.subsexist) {
                            showLayouts(view, false, false, true, false);
                        } else {
                            showLayouts(view, false, true, false, false);
                        }
                    } catch (Exception e) {
                        Log.d(AppHelper.TAG, e.toString());
                    }
                }
            });

            showPrice(view, mainActivity.monthlyPr, mainActivity.yearlyPr, mainActivity.sixMonthPr);
        }

        return view;
    }

    public void showLayouts(View layout, boolean loading, boolean buy, boolean bought, boolean notsupported) {
        layout.findViewById(R.id.loading_layout).setVisibility(loading ? View.VISIBLE : View.GONE);
        layout.findViewById(R.id.buy_layout).setVisibility(buy ? View.VISIBLE : View.GONE);
        layout.findViewById(R.id.bought_layout).setVisibility(bought ? View.VISIBLE : View.GONE);
        layout.findViewById(R.id.notsupported_layout).setVisibility(notsupported ? View.VISIBLE : View.GONE);
    }

    public void showPrice(View layout, String monthlyPrice, String annualPrice, String sixmonthPrice) {
        ((Button) layout.findViewById(R.id.sub_monthly_buy_button)).setText(monthlyPrice);
        ((Button) layout.findViewById(R.id.sub_annual_buy_button)).setText(annualPrice);
        ((Button) layout.findViewById(R.id.sub_6month_buy_button)).setText(sixmonthPrice);
    }
}
package com.alburagh.alburagh.Helpers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.ViewGroup;

import com.alburagh.alburagh.IAB.IabHelper;
import com.alburagh.alburagh.IAB.IabResult;
import com.alburagh.alburagh.IAB.Purchase;
import com.alburagh.alburagh.MainActivity;
import com.alburagh.alburagh.Models.Subscription;
import com.alburagh.alburagh.Models.User;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;


public class UserManager {
    private static UserManager instance = null;

    private UserManager() { }

    public static UserManager getInstance() {
        if (instance == null)
            instance = new UserManager();
        return instance;
    }

    public boolean isLoggedIn;
    public User user;

    public void setUserExpire(long expire) {
        Setting.getInstance().setVariable("user_expire", String.valueOf(expire));

        long difference = expire - System.currentTimeMillis();
        difference /= 1000;

        user.expire = String.valueOf(expire);
        user.expireDays = (int) TimeUnit.SECONDS.toDays(difference);
        user.expireHours = (int) (TimeUnit.SECONDS.toHours(difference) - (user.expireDays * 24));
        // user.expireMinutes = (int) (TimeUnit.SECONDS.toSeconds(difference) - (TimeUnit.SECONDS.toMinutes(difference) * 60));
    }


    //==============================================================================================
    //== Account
    //==============================================================================================

    public void autoLogin() {
        String lastMail = Setting.getInstance().getVariable("last_mail");
        String lastPass = Setting.getInstance().getVariable("last_pass");

        login(lastMail, lastPass, new LoginCallback() {
            @Override
            public void success(User user) {
                UserManager.getInstance().isLoggedIn = true;
                UserManager.getInstance().user = user;

                UserManager.getInstance().setUserExpire(Long.valueOf(user.expire) * 1000);
            }

            @Override
            public void failed() {

            }
        });
    }

    public void login(String mail, String password, final LoginCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("mail", mail);
        params.put("password", password);

        client.post(WebService.getWebServiceUrl() + "/login", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                    if (object.getInt("success") == 1) {
                        User user = new User();
                        user.userID = object.getInt("userid");
                        user.fullname = object.getString("fullname");
                        user.country = object.getString("country");
                        user.mail = object.getString("mail");
                        user.created = object.getString("created");

                        String expire = object.getString("expire_timestamp");
                        user.expire = (expire.equals("null")) ? "0" : expire;
                        callback.success(user);
                    } else
                        callback.failed();

                } catch (Exception e) {
                    callback.failed();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.failed();
            }
        });
    }

    public void logout() {
        user = null;
        isLoggedIn = false;
    }

    public void register(String mail, String password, String fullname, String country, final RegisterCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("mail", mail);
        params.put("password", password);
        params.put("fullname", fullname);
        params.put("country", country);

        client.post(WebService.getWebServiceUrl() + "/register", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                    if (object.getInt("success") == 1)
                        callback.success();
                    else
                        callback.failed();

                } catch (Exception e) {
                    callback.failed();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.failed();
            }
        });
    }

    public void changePassword(String mail, String oldPass, String newPass, final ChangePasswordCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("mail", mail);
        params.put("old_pass", oldPass);
        params.put("new_pass", newPass);

        client.post(WebService.getWebServiceUrl() + "/change_pass", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                    if (object.getInt("success") == 1)
                        callback.success();
                    else
                        callback.failed();

                } catch (Exception e) {
                    callback.failed();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.failed();
            }
        });
    }

    public void resetPassword(String mail, final ResetPasswordCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("mail", mail);

        client.post(WebService.getWebServiceUrl() + "/reset_pass", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                    if (object.getInt("success") == 1)
                        callback.success();
                    else
                        callback.failed();

                } catch (Exception e) {
                    callback.failed();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.failed();
            }
        });
    }

    public void checkValidMail(String mail, final CheckMailValidCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.add("mail", mail);

        client.post(WebService.getWebServiceUrl() + "/check_mail_valid", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                    Log.d("sasd", "onRegSuccess: "+responseBody);
                    if (object.getInt("success") == 1) {
                        callback.success();
                    }
                    else {
                        callback.failed("Error...");
                    }

                } catch (Exception e) {
                    callback.failed("Error...");
                    Log.d("sasd", "onRegunSuccess: "+responseBody);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Log.e("sasd", "onRegfail: "+error);
                callback.failed("Error...");
            }
        });
    }

    public void checkValidPassword(String password, CheckPasswordCallback callback) {
        if (password.length() >= 6)
            callback.success();
        else
            callback.failed("Error...");
    }

    public List getUserReadBooks() {
        return null;
    }

    public void setBookRead(String mail, String bookFolder) {
    }

    public void syncUserReadBooks() {
    }

    public void sendStats(int userId, String client, String type, String value, final SendStateCallback callback) {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("user_id", String.valueOf(userId));
        params.add("lang", AppHelper.LANGUAGE);
        params.add("client", client);
        params.add("type", type);
        params.add("value", value);

        httpClient.post(WebService.getWebServiceUrl() + "/add_stats", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                    if (object.getInt("success") == 1)
                        callback.success();
                    else
                        callback.failed();

                } catch (Exception e) {
                    callback.failed();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.failed();
            }
        });
    }


    public String getUsername(Context context) {
        AccountManager manager = AccountManager.get(context);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            return possibleEmails.get(0);
        }
        return null;
    }


    //==============================================================================================
    //== Code Subscription
    //==============================================================================================

    public void checkCode(String code, final CheckCodeCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("code", code);

        client.post(WebService.getInstance().getWebServiceUrl() + "/check_code", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                    if (object.getInt("success") == 1)
                        callback.checkCode(true);
                    else
                        callback.checkCode(false);

                } catch (Exception e) {
                    callback.checkCode(false);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.checkCode(false);
            }
        });
    }

    public void submitCode(String mail, String code, final SubmitCodeCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("mail", mail);
        params.put("code", code);

        client.post(WebService.getWebServiceUrl() + "/submit_code", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                    if (object.getInt("success") == 1)
                        callback.success(object.getInt("expire_timestamp"));
                    else
                        callback.failed();

                } catch (Exception e) {
                    callback.failed();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.failed();
            }
        });
    }

    public void userCodeSubscriptionRemainginDays(String mail, final RemainingDaysCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("mail", mail);

        client.post(WebService.getWebServiceUrl() + "/check_code", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                    if (object.getInt("success") == 1)
                        callback.remainingDays(true);
                    else
                        callback.remainingDays(false);

                } catch (Exception e) {
                    callback.remainingDays(false);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.remainingDays(false);
            }
        });
    }

    public boolean checkCodeSubscriptionAvailable() {
        String expireString = Setting.getInstance().getVariable("user_expire");
        if (expireString == null || expireString.length() == 0)
            return false;
        else {
            long expire = Long.valueOf(expireString);
            return expire > System.currentTimeMillis();
        }

    }


    //==============================================================================================
    //== Subscription
    //==============================================================================================

    public static final String CURRENT_MONTHLY_SUB = "current_monthly";
    public static final String CURRENT_6MONTH_SUB = "current_6month_sub";
    public static final String CURRENT_ANNUAL_SUB = "current_annual_sub";

    public void checkSubscriptionStatus(MainActivity activity, final SubscriptionStatusCallback callback) {
        activity.initIAB(new MainActivity.InitIABCallback() {
            @Override
            public void onReceivedInventory(Subscription subscription, String monthlyPrice, String yearlyPrice) {
                Log.i("helper", "onReceivedInventory: " + IabHelper.subsexist);
                if (IabHelper.subsexist)
                    callback.available();
                else {
                    if (checkCodeSubscriptionAvailable())
                        callback.available();
                    else
                        callback.notAvailable();
                }
            }

            @Override
            public void failed(IabResult result) {
                if (IabHelper.subsexist) {
                    callback.available();
                } else {
                    if (checkCodeSubscriptionAvailable())
                        callback.available();
                    else {
                        Subscription monthlySub = new Gson().fromJson(Setting.getInstance().getVariable(UserManager.CURRENT_MONTHLY_SUB), Subscription.class);
                        Subscription annualSub = new Gson().fromJson(Setting.getInstance().getVariable(UserManager.CURRENT_ANNUAL_SUB), Subscription.class);
                        Subscription sixmonthSub = new Gson().fromJson(Setting.getInstance().getVariable(UserManager.CURRENT_6MONTH_SUB), Subscription.class);


                        if (monthlySub != null) {
                            Date date = new Date(monthlySub.endTime);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
                                callback.available();
                                return;
                            }
                        } else if (annualSub != null) {
                            Date date = new Date(annualSub.endTime);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
                                callback.available();
                                return;
                            }
                        } else if (sixmonthSub != null) {
                            Date date = new Date(sixmonthSub.endTime);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
                                callback.available();
                                return;
                            }
                        }

                        callback.notAvailable();
                    }
                }

            }
        });
    }

    public void onlinePurchaseValidation(Subscription.Type type, final String token, final CheckSubscriptionCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = String.format("%s/bazaar/get_purchase_status/%s/%s",
                WebService.getWebServiceUrl(), AppHelper.subscriptionTypeToSku(type), token);

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                    if (object.has("validUntilTimestampMsec")) {
                        Subscription subscription = new Subscription();
                        subscription.purchaseTime = object.getLong("initiationTimestampMsec");
                        subscription.autoRenewing = object.getBoolean("autoRenewing");
                        subscription.endTime = object.getLong("validUntilTimestampMsec");
                        subscription.token = token;
                        callback.success(subscription);
                    } else
                        callback.failed();

                } catch (Exception e) {
                    callback.failed();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.failed();
            }
        });
    }


    public void autoCheckSubscription(MainActivity context) {
        // UserManager.getInstance().checkSubscriptionStatus(context, new UserManager.SubscriptionStatusCallback() {
        //     @Override
        //     public void available() {
        //         AppHelper.SubscriptionExist = true;
        //     }
        //
        //     @Override
        //     public void notAvailable() {
        //         AppHelper.SubscriptionExist = false;
        //     }
        // });

        context.initIAB(new MainActivity.InitIABCallback() {
            @Override
            public void onReceivedInventory(Subscription subscription, String monthlyPrice, String annualPrice) {
                AppHelper.SubscriptionExist = subscription != null;
            }

            @Override
            public void failed(IabResult result) {
                Log.d("Error", result.getMessage());
            }
        });
    }


    //==============================================================================================
    //== Interfaces
    //==============================================================================================

    // Todo: separate server errors (wrong pass) from general errors (Connection error)

    public interface LoginCallback {
        void success(User user);

        void failed();
    }

    public interface RegisterCallback {
        void success();

        void failed();
    }

    public interface ChangePasswordCallback {
        void success();

        void failed();
    }

    public interface ResetPasswordCallback {
        void success();

        void failed();
    }

    public interface CheckMailValidCallback {
        void success();

        void failed(String error);
    }

    public interface CheckPasswordCallback {
        void success();

        void failed(String error);
    }

    public interface CheckCodeCallback {
        void checkCode(boolean success);
    }

    public interface SubmitCodeCallback {
        void success(long expireTimestamp);

        void failed();
    }

    public interface RemainingDaysCallback {
        void remainingDays(boolean success);
    }

    public interface SubscriptionStatusCallback {
        void available();

        void notAvailable();
    }

    public interface SendStateCallback {
        void success();

        void failed();
    }

    public interface CheckSubscriptionCallback {
        void success(Subscription subscription);

        void failed();
    }
}
package com.alburagh.alburagh.Helpers;

import static com.alburagh.alburagh.Helpers.AppHelper.TAG;

import android.content.pm.LauncherApps;
import android.util.Log;

import com.alburagh.alburagh.Models.Settings;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class WebService {
    private static WebService instance = null;
    public static WebService getInstance() {
        if (instance == null)
            instance = new WebService();
        return instance;
    }
    private WebService () {}

    public Boolean checkConnection () {
        return true;
    }

    private static String AR_WEB_SERVICE_ADDRESS = "https://alburagh.com/app/abrest";
    //private static String FA_WEB_SERVICE_ADDRESS = "https://alburagh.com/app/abrest";

    static String getWebServiceUrl() {
        if (AppHelper.LANGUAGE.equals("fa"))
            return AR_WEB_SERVICE_ADDRESS;
        else if (AppHelper.LANGUAGE.equals("ar"))
            return AR_WEB_SERVICE_ADDRESS;

        return null;
    }

    private String getWebServiceAddress(String call) {
        return getWebServiceUrl() + "/" + call;
    }



    //==============================================================================================
    //== Get data from web service
    //==============================================================================================

    public void getSettings(final SettingsCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getWebServiceAddress("get_settings"), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody, "UTF-8"));
                    Settings settings = new Settings();
                    settings.lastVersion = object.getInt("last_ver");
                    settings.realPrice = object.getString("real_price");
                    settings.discountedPrice = object.getString("discounted_price");
                    callback.success(settings);
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

    public void getContentCount(final ContentsCountCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getWebServiceAddress("content_count"), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jsonObject.getInt("success") == 1)
                        callback.contentsCount(true, jsonObject.getInt("count"));
                    else
                        callback.contentsCount(false, 0);
                } catch (Exception e) {
                    callback.contentsCount(false, 0);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.contentsCount(false, 0);
            }
        });
    }

    public void getBooksCount(final BooksCountCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getWebServiceAddress("content_count/book"), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jsonObject.getInt("success") == 1)
                        callback.booksCount(true, jsonObject.getInt("count"));
                    else
                        callback.booksCount(false, 0);
                } catch (Exception e) {
                    callback.booksCount(false, 0);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.booksCount(false, 0);
            }
        });
    }

    public void getGamesCount(final GamesCountCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getWebServiceAddress("content_count/game"), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jsonObject.getInt("success") == 1)
                        callback.booksCount(true, jsonObject.getInt("count"));
                    else
                        callback.booksCount(false, 0);
                } catch (Exception e) {
                    callback.booksCount(false, 0);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.booksCount(false, 0);
            }
        });
    }

    public void getContentList(boolean compressed, final ContentListCallback callback) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setMaxRetriesAndTimeout(6, 5000);
            String address = compressed ? getWebServiceAddress("content_list") : getWebServiceAddress("content_list");
            client.get(address, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        callback.success(new String(responseBody, "UTF-8"));
                    } catch (Exception e) {
                        callback.failed(null);
                        e.printStackTrace();
                    }
                }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.failed(error);
                error.printStackTrace();
                Log.d("ALBURAGH", "Get Content List Error...");
            }
        });
    }

    public void isNewVersionAvailable(final CheckNewVersionCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getWebServiceAddress("check_new_version"), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody, "UTF-8"));
                    if (object.getInt("success") == 1)
                        callback.checkNewVersion(true);
                    else
                        callback.checkNewVersion(false);
                } catch (Exception e) {
                    callback.checkNewVersion(false);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.checkNewVersion(false);
                Log.d("ALBURAGH", "Check New Version Error...");
            }
        });
    }

    public void addBookRead(String mail, List<String> books, final AddUserBookReadCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("mail", mail);
        params.put("books", new Gson().toJson(books));

        client.post(getWebServiceAddress("add_user_read_books"), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody, "UTF-8"));
                    if (object.getInt("success") == 1) {
                        JSONArray readBooks = object.getJSONArray("read_books");
                        List<String> readBooksArray = new ArrayList<>();
                        for (int i = 0; i < readBooks.length(); i++)
                            readBooksArray.add(readBooks.getString(i));

                        callback.addUserBookRead(true, object.getInt("num_read_books"), readBooksArray);
                    } else
                        callback.addUserBookRead(false, 0, null);
                } catch (Exception e) {
                    callback.addUserBookRead(false, 0, null);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.addUserBookRead(false, 0, null);
            }
        });
    }

    public void sendMail(String to, String subject, String message, final SendMailCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("to", to);
        params.put("subject", subject);
        params.put("message", message);

        client.post(getWebServiceAddress("send_mail"), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody, "UTF-8"));
                    if (object.getInt("success") == 1)
                        callback.sendMail(true);
                    else
                        callback.sendMail(false);
                } catch (Exception e) {
                    callback.sendMail(false);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.sendMail(false);
            }
        });
    }

    public void rateContent(int contentID, float rate, final RateCallback callback) {
        String url = String.format(Locale.US, "%s/rate/%d/%.1f", getWebServiceUrl(), contentID, rate);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody, "UTF-8"));
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

    public void increaseReadings(int contentID, final IncreaseReadCountCallback callback) {
        String url = String.format(Locale.US, "%s/increase_readings/%d", getWebServiceUrl(), contentID);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody, "UTF-8"));
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

    public void increaseDownloads(int contentID, final IncreaseDownloadCountCallback callback) {
        String url = String.format(Locale.US, "%s/increase_download/%d", getWebServiceUrl(), contentID);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody, "UTF-8"));
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



    //==============================================================================================
    //== Interfaces
    //==============================================================================================

    public interface SettingsCallback {
        void success(Settings settings);
        void failed();
    }

    interface ContentsCountCallback {
        void contentsCount(boolean success, int count);
    }

    interface BooksCountCallback {
        void booksCount(boolean success, int count);
    }

    interface GamesCountCallback {
        void booksCount(boolean success, int count);
    }

    interface ContentListCallback {
        void success(String json);
        void failed(Throwable error);
    }

    interface CheckNewVersionCallback {
        void checkNewVersion(boolean available);
    }

    interface AddUserBookReadCallback {
        void addUserBookRead(boolean success, int readBooksCount, List<String> readBooks);
    }

    interface SendMailCallback {
        void sendMail(boolean success);
    }

    public interface RateCallback {
        void success();
        void failed();
    }

    public interface IncreaseDownloadCountCallback {
        void success();
        void failed();
    }

    public interface IncreaseReadCountCallback {
        void success();
        void failed();
    }
}
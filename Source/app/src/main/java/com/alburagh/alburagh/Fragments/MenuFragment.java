package com.alburagh.alburagh.Fragments;


import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alburagh.alburagh.Helpers.AnimationHelper;
import com.alburagh.alburagh.Helpers.AppHelper;
import com.alburagh.alburagh.Helpers.ContentManager;
import com.alburagh.alburagh.Helpers.DownloadManager;
import com.alburagh.alburagh.Helpers.HttpHandler;
import com.alburagh.alburagh.Helpers.UIHelper;
import com.alburagh.alburagh.MainActivity;
import com.alburagh.alburagh.Models.Content;
import com.alburagh.alburagh.Models.Quran;
import com.alburagh.alburagh.R;
import com.duowan.mobile.netroid.NetroidError;
import com.filippudak.ProgressPieView.ProgressPieView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MenuFragment extends Fragment {

    public MenuFragment() {}

    private static String URL = "https://alburagh.com/app/abrest/content_list";
    public Quran quran = new Quran();
    ImageButton booksButton,audioBooksButton,
            quranButton,gamesButton,homeButton;
    public DownloadManager.DownloadListener listener;

    private String rootDirexists = Environment.getExternalStorageDirectory().toString();
    private File contentDirexists = new File(rootDirexists + "/Android/data/"+ MainActivity.PACKAGE_NAME +"/contents/content_list.json");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_menu, container, false);
        final MainActivity mainActivity = (MainActivity) getActivity();
        Animation slide_in_up = AnimationUtils.loadAnimation(getActivity(),R.anim.slide_out_up);
        slide_in_up.setDuration(1500);
        view.findViewById(R.id.container).startAnimation(slide_in_up);
        CheckAndGetData();
        final ProgressPieView pb = view.findViewById(R.id.download_progresshm);

        if (quran.downloadStatus == Content.DownloadStatus.ADDED_TO_QUEUE)
            pb.setVisibility(View.VISIBLE);
        else if (quran.downloadStatus == Content.DownloadStatus.DOWNLOADING)
            pb.setVisibility(View.VISIBLE);

        quranButton         = view.findViewById(R.id.quran_button);
        booksButton         = view.findViewById(R.id.books_button);
        audioBooksButton    = view.findViewById(R.id.audio_books_button);
        gamesButton         = view.findViewById(R.id.games_button);
        homeButton          = view.findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.showHome(false);
                UIHelper.playButtonSound(getActivity());
            }
        });
        audioBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.showSoundBooks();
                UIHelper.playButtonSound(getActivity());
            }
        });
        booksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.showBooks();
                UIHelper.playButtonSound(getActivity());
            }
        });
        gamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.showGames();
                UIHelper.playButtonSound(getActivity());
            }
        });

        quranButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quran.downloadStatus == Content.DownloadStatus.DOWNLOADING) {

                } else {

                    if (quran.filename != null) {
                        quran.isQuran = true;
                        contentClicked(quran, true);
                    } else {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(getResources().getString(R.string.content_download_error_title))
                                .setMessage(String.format(getResources().getString(R.string.content_download_error_message), quran.title))
                                .setPositiveButton(getResources().getString(R.string.content_download_again), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        quranButton.callOnClick();
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.cancel), null)
                                .show();
                    }
                    listener = new DownloadManager.DownloadListener() {
                        @Override
                        public void added(Content content) { }

                        @Override
                        public void started(Content content) {
                            if (content.isQuran) {
                                quran.downloadStatus = Content.DownloadStatus.ADDED_TO_QUEUE;
                                pb.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void progressed(Content content, float percent) {
                            if (content.isQuran) {
                                try {
                                    int progress = Math.round(percent * 100);
                                    Log.i(TAG, "progressed: " + progress);
                                    pb.setProgress(progress);
                                    quran.downloadStatus = Content.DownloadStatus.DOWNLOADING;
                                    pb.setVisibility(View.VISIBLE);

                                    if (progress == 100) { }
                                } catch (Exception e) {
                                    Log.d("ALBURAGH", e.toString());
                                }
                            }
                        }

                        @Override
                        public void error(Content content, NetroidError error) {
                            if (content.isQuran) {
                                quran.downloadStatus = Content.DownloadStatus.NOT_DOWNLOADED;
                                new AlertDialog.Builder(getActivity())
                                        .setTitle(getResources().getString(R.string.content_download_error_title))
                                        .setMessage(String.format(getResources().getString(R.string.content_download_error_message), quran.title))
                                        .setPositiveButton(getResources().getString(R.string.content_download_again), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                DownloadManager.getInstance().download(quran, getActivity());
                                                DownloadManager.getInstance().requestQueue.start();
                                            }
                                        })
                                        .setNegativeButton(getResources().getString(R.string.cancel), null)
                                        .show();
                                pb.setVisibility(View.GONE);

                            }
                        }

                        @Override
                        public void success(Content content) {
                            if (content.isQuran) {
                                quran.downloadStatus = Content.DownloadStatus.DOWNLOADED;
                                Log.i(TAG, "finished: Download Finished.");
                                Toast.makeText(getActivity().getApplicationContext(), R.string.quran_download_ended, Toast.LENGTH_LONG).show();
                                pb.setVisibility(View.GONE);

                                ContentManager.getInstance().saveContentVersion(content);
                            }
                        }

                        @Override
                        public void finished(Content content) {
                            if (content.isQuran) { }
                        }
                    };

                    DownloadManager.getInstance().setListener(listener);
                }
            }
        });
        listener = new DownloadManager.DownloadListener() {
            @Override
            public void added(Content content) { }

            @Override
            public void started(Content content) {
                if (content.isQuran) {
                    quran.downloadStatus = Content.DownloadStatus.ADDED_TO_QUEUE;
                    pb.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void progressed(Content content, float percent) {
                if (content.isQuran) {
                    try {
                        int progress = Math.round(percent * 100);
                        Log.i(TAG, "progressed: " + progress);
                        pb.setProgress(progress);
                        quran.downloadStatus = Content.DownloadStatus.DOWNLOADING;
                        pb.setVisibility(View.VISIBLE);

                        if (progress == 100) { }
                    } catch (Exception e) {
                        Log.d("ALBURAGH", e.toString());
                    }
                }
            }

            @Override
            public void error(Content content, NetroidError error) {
                if (content.isQuran) {
                    quran.downloadStatus = Content.DownloadStatus.NOT_DOWNLOADED;
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getResources().getString(R.string.content_download_error_title))
                            .setMessage(String.format(getResources().getString(R.string.content_download_error_message), quran.title))
                            .setPositiveButton(getResources().getString(R.string.content_download_again), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DownloadManager.getInstance().download(quran, getActivity());
                                    DownloadManager.getInstance().requestQueue.start();
                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.cancel), null)
                            .show();
                    pb.setVisibility(View.GONE);
                }
            }

            @Override
            public void success(Content content) {
                if (content.isQuran) {
                    quran.downloadStatus = Content.DownloadStatus.DOWNLOADED;
                    Log.i(TAG, "finished: Download Finished.");
                    Toast.makeText(getActivity().getApplicationContext(), R.string.quran_download_ended, Toast.LENGTH_LONG).show();
                    pb.setVisibility(View.GONE);

                    ContentManager.getInstance().saveContentVersion(content);
                }
            }

            @Override
            public void finished(Content content) {
                if (content.isQuran) { }
            }
        };

        DownloadManager.getInstance().setListener(listener);







        return view;
    }

    public void contentClicked(final Content content, boolean versionCheck) {
        if (isContentDownloaded(content) && versionCheck) {
            int version = getSavedContentVersion(content);
            if (content.contentVersion > version) {
                String message;
                message = getResources().getString(R.string.quran_update_message);

                new AlertDialog.Builder(getActivity())
                        .setMessage(message)
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateContent(content, getActivity());
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                contentClicked(content, false);
                            }
                        })
                        .show();

                return;
            } else if (isContentDownloaded(content))
                ((MainActivity) getActivity()).showQuran(content);
        } else if (isContentDownloaded(content)) {
            ((MainActivity) getActivity()).showQuran(content);
        } else {
            DownloadManager.getInstance().download(content, getActivity());
            DownloadManager.getInstance().requestQueue.start();
        }

        UIHelper.playButtonSound(getActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver observer = view.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    observer.removeOnGlobalLayoutListener(this);
                else
                    observer.removeGlobalOnLayoutListener(this);

                UIHelper.ScreenOptions so = new UIHelper.ScreenOptions(getActivity(), view);
                if (so.height / so.width < so.imageHeight / so.imageWidth) {
                    View container = view.findViewById(R.id.container);
                    container.setBackgroundResource(R.drawable.home_bg);
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) container.getLayoutParams();
                    int width = (int) ((container.getHeight() * so.imageWidth) / so.imageHeight);
                    layoutParams.width = width;
                    container.setLayoutParams(layoutParams);
                    so = new UIHelper.ScreenOptions(getActivity(), container, width, container.getHeight());
                }

                TypedValue outValue = new TypedValue();
                getResources().getValue(R.dimen.button_scale, outValue, true);
                float buttonScale = outValue.getFloat();

                UIHelper.fitViewToBackground(so, view.findViewById(R.id.home_button), 50, 50, UIHelper.intArrayToPoint(getActivity(), R.array.menu_home), buttonScale);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.books_button), 125, 125, UIHelper.intArrayToPoint(getActivity(), R.array.menu_books), buttonScale);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.games_button), 125, 125, UIHelper.intArrayToPoint(getActivity(), R.array.menu_games), buttonScale);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.quran_button), 125, 125, UIHelper.intArrayToPoint(getActivity(), R.array.menu_quran), buttonScale);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.audio_books_button), 125, 125,
                        UIHelper.intArrayToPoint(getActivity(), R.array.menu_audio_books), buttonScale);


                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.home_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.books_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.games_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.audio_books_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.quran_button));

                initAnimations(so, view);
            }
        });
    }
    public void initAnimations(UIHelper.ScreenOptions so, View layout) {
        // Animate giraffe to right edge to avoid shrinking for less wider screens

        final List<View> buttons = new ArrayList<>();
        buttons.add(layout.findViewById(R.id.games_button));
        buttons.add(layout.findViewById(R.id.books_button));
        buttons.add(layout.findViewById(R.id.quran_button));
        buttons.add(layout.findViewById(R.id.audio_books_button));
        buttons.add(layout.findViewById(R.id.home_button));
        AnimationHelper.getInstance().buttonsAnimation(buttons);

    }

    //==============================================================================================
    //== Get Contents
    //==============================================================================================

    private class GetContactsOffline extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String jsonStr;
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(contentDirexists));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            } catch (IOException e) {
                //You'll need to add proper error handling here
            }
            // Making a request to url and getting response
            jsonStr = text.toString();


            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray qurans = jsonObj.getJSONArray("qurans");

                    // looping through All Contacts

                    JSONObject c = qurans.getJSONObject(0);


                    quran.contentID = c.getInt("id");
                    quran.contentVersion = c.getInt("version");
                    quran.date = new Date(quran.timestamp * 1000L);
                    quran.desk = c.getString("description");
                    quran.filename = c.getString("file");
                    quran.folderName = c.getString("folder");
                    quran.image = c.getString("image");
                    quran.lang = c.getString("lang");
                    quran.order = c.getInt("order");
                    quran.rate = (float) c.getDouble("rate");
                    quran.readCount = c.getInt("read_count");
                    quran.timestamp = c.getInt("date");
                    quran.title = c.getString("title");
                    quran.isQuran = true;
                    quran.isBook = false;


                    if (isContentDownloaded(quran))
                        quran.downloadStatus = Content.DownloadStatus.DOWNLOADED;
                    else if (isContentDownloading(quran))
                        quran.downloadStatus = Content.DownloadStatus.DOWNLOADING;
                    else
                        quran.downloadStatus = Content.DownloadStatus.NOT_DOWNLOADED;

                        /*
                        {"id”:12
                        "title":”Quran"
                        "description":"Quran Quran Quran Quran Quran Quran Quran Quran Quran Quran Quran”
                        "read_count”:1
                        "folder":"quran_fa”,
                        "rate":1,
                        "file":"http:\/\/alwaheh.com\/content\/qurans\/files\/quran_fa.zip”,
                        "image":"http:\/\/alwaheh.com\/content\/games\/images\/makan_morghha_cover_fa.jpg”,
                        "order":1000,"lang":"fa”,
                        "compatibility":"1”,
                        "version":"1”,
                        "published":1,
                        "file_size":"58.81 MB”,
                        "date":1488013227,
                        "readings":1,
                        "samples":[]}

                         */


                } catch (final JSONException e) {
                    // getActivity().runOnUiThread(new Runnable() {
                    //     @Override
                    //     public void run() {
                    //     }
                    // });

                }
            } else {
                // getActivity().runOnUiThread(new Runnable() {
                //     @Override
                //     public void run() {
                //     }
                // });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // pDialog.dismiss();
        }

    }

    private class GetContactsOnline extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(URL);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    if (contentDirexists.exists()) {
                        boolean deleted = contentDirexists.delete();
                        deleted = true;
                        File contentFile = new File(AppHelper.getContentFolder(), "content_list.json");
                        AppHelper.filePutContents(contentFile, jsonStr);
                    } else {
                        File contentFile = new File(AppHelper.getContentFolder(), "content_list.json");
                        AppHelper.filePutContents(contentFile, jsonStr);
                    }
                } catch (final JSONException e) {
                    Log.i(TAG, "doInBaackground: " + e);
                }
            } else {
                // getActivity().runOnUiThread(new Runnable() {
                //     @Override
                //     public void run() {
                //     }
                // });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // pDialog.dismiss();
        }
    }

    //==============================================================================================
    //== Check Internet Connection & Get data
    //==============================================================================================

    public boolean isNetworkOnline() {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;
    }

    public void CheckAndGetData() {
        Log.i(TAG, "onCreateViewcontent2: ");
        if (contentDirexists.exists()) {
            if (isNetworkOnline()) {
                Log.i(TAG, "onCreateViewcontent3: ");
                new GetContactsOffline().execute();
                new GetContactsOnline().execute();
            } else {
                Log.i(TAG, "onCreateViewcontent4: ");
                new GetContactsOffline().execute();
            }
        } else {
            if (isNetworkOnline()) {
                Log.i(TAG, "onCreateViewcontent5: ");

                new GetContactsOnline().execute();
            }
        }
    }
//==============================================================================================
    //== Downloads
    //==============================================================================================

    public boolean isContentDownloading(Content content) {

        return content.folderName.equals(content.folderName);
    }

    public void updateContent(Content content, Context context) {
        content.deleteLastVersionBeforeUnzip = true;
        DownloadManager.getInstance().download(content, context);
        DownloadManager.getInstance().requestQueue.start();
    }

    public boolean isContentDownloaded(Content content) {
        File file = new File(AppHelper.getContentFolder(), String.format("%s/content.json", content.folderName));
        return file.exists();
    }

    public int getSavedContentVersion(Content content) {
        File contentFolder = new File(AppHelper.getContentFolder(), content.folderName);
        File versionFile = new File(contentFolder, "ver.txt");
        if (versionFile.exists()) {
            String version = AppHelper.fileGetContents(versionFile);
            if (version.matches("-?\\d+(\\.\\d+)?"))
                return Integer.parseInt(version);
        }

        return 1;
    }

}
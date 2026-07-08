package com.alburagh.alburagh.Fragments;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alburagh.alburagh.Helpers.AppHelper;
import com.alburagh.alburagh.Helpers.UIHelper;
import com.alburagh.alburagh.Helpers.UIHelper.ScreenOptions;
import com.alburagh.alburagh.Helpers.UserManager;
import com.alburagh.alburagh.Helpers.WebService;
import com.alburagh.alburagh.MainActivity;
import com.alburagh.alburagh.Models.ABMediaPlayer;
import com.alburagh.alburagh.Models.Book;
import com.alburagh.alburagh.Models.BookIndex;
import com.alburagh.alburagh.Models.Content;
import com.alburagh.alburagh.Models.Game;
import com.alburagh.alburagh.Models.SoundBook;
import com.alburagh.alburagh.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ContentFragment extends Fragment {

    WebView webView;
    View transitionView;

    public String contentPath;
    public BookIndex bookIndex;
    public String gameHTML;

    public MediaPlayer musicPlayer;
    boolean resumeMusicPlayer = false;

    public MediaPlayer narrationPlayer;
    boolean resumeNarrationPlayer = false;
    private boolean narrationState = true;

    public List<ABMediaPlayer> soundPlayers;

    boolean resumeWebView = false;
    public Content content;

    private boolean togglingMenu;

    public CountDownTimer freeUseTimer;
    public Long beginTime;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_content, container, false);
        webView = (WebView) view.findViewById(R.id.web_view);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccess(true);
        transitionView = view.findViewById(R.id.transition);
        //lang = new File(AppHelper.getContentFolder(), content.folderName).getAbsolutePath();
        content = new Gson().fromJson(getArguments().getString("content"), Content.class);
        contentPath = new File(AppHelper.getContentFolder(), content.folderName).getAbsolutePath();
        showContent(view);

        final MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.toggleMusicOnContentOpen();

        beginTime = System.currentTimeMillis();

        view.findViewById(R.id.narration_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageButton button = (ImageButton) view;
                if (narrationPlayer != null) {
                    if (narrationState) {
                        try {
                            narrationPlayer.pause();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        button.setImageResource(R.drawable.narration_off_bmb);
                    } else {
                        try {
                            narrationPlayer.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        button.setImageResource(R.drawable.narration_on_bmb);
                    }
                }

                narrationState = !narrationState;
            }
        });

        view.findViewById(R.id.narration_button_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton button = (ImageButton) v;
                if (narrationPlayer != null) {
                    if (narrationState) {
                        try {
                            narrationPlayer.pause();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        button.setImageResource(R.drawable.narration_off_button_over_content);
                    } else {
                        try {
                            narrationPlayer.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        button.setImageResource(R.drawable.narration_on_button_over_content);
                    }
                }

                narrationState = !narrationState;
            }
        });

        view.findViewById(R.id.books_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToBooks();
            }
        });

        view.findViewById(R.id.books_button_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        view.findViewById(R.id.games_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToGames();
            }
        });

        view.findViewById(R.id.menu_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(view);
                UIHelper.playButtonSound(getActivity());
            }
        });

        view.findViewById(R.id.menu_background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenu(view);
                UIHelper.playButtonSound(getActivity());
            }
        });

        view.findViewById(R.id.rate_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuLayout(view.findViewById(R.id.rate_layout));
                UIHelper.playButtonSound(getActivity());
            }
        });

        view.findViewById(R.id.index_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuLayout(view.findViewById(R.id.index_layout));
                UIHelper.playButtonSound(getActivity());
            }
        });

        view.findViewById(R.id.index_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager pager = (ViewPager) view.findViewById(R.id.index_pager);
                if (pager.getChildCount() > pager.getCurrentItem())
                    pager.setCurrentItem(pager.getCurrentItem() + 1);

                UIHelper.playButtonSound(getActivity());
            }
        });

        view.findViewById(R.id.index_prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager pager = (ViewPager) view.findViewById(R.id.index_pager);
                if (pager.getCurrentItem() > 0)
                    pager.setCurrentItem(pager.getCurrentItem() - 1);

                UIHelper.playButtonSound(getActivity());
            }
        });

        view.findViewById(R.id.email_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppHelper.shareApp(getActivity());
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver observer = view.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    observer.removeOnGlobalLayoutListener(this);
                else
                    observer.removeGlobalOnLayoutListener(this);

                TypedValue outValue = new TypedValue();
                getResources().getValue(R.dimen.button_scale, outValue, true);
                float buttonScale = outValue.getFloat();

                ScreenOptions so = new ScreenOptions(getActivity(), view);
                UIHelper.fitLayoutToBackground(so, view.findViewById(R.id.buttons_background), 1390, 445, 235, 617);

                UIHelper.fitViewToBackground(so, view.findViewById(R.id.narration_button), 90, 90, new Point(470, 930), 1.0f);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.rate_button), 90, 90, new Point(670, 930), 1.0f);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.games_button), 90, 90, new Point(870, 930), 1.0f);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.books_button), 90, 90, new Point(1070, 930), 1.0f);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.email_button), 90, 90, new Point(1270, 930), 1.0f);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.index_button), 90, 90, new Point(1470, 930), 1.0f);

                UIHelper.fitSize(so, view.findViewById(R.id.narration_button_new), 90, 90);
                UIHelper.fitSize(so, view.findViewById(R.id.books_button_new), 90, 90);

                Point menuButtonCenter = new Point(450, 1040);
                float ratio = so.height / so.width;
                if (ratio < 0.75 && ratio >= 0.65)
                    menuButtonCenter = new Point(340, 1040);
                else if (ratio <= 0.65)
                    menuButtonCenter = new Point(300, 1040);

                UIHelper.fitViewToBackground(so, view.findViewById(R.id.menu_button), 95, 95, menuButtonCenter, 1.0f);

                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.menu_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.narration_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.narration_button_new));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.rate_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.games_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.books_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.books_button_new));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.email_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.index_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.rate_send_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.index_next));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.index_prev));
                Log.d(TAG, "onGlobalLayout: "+getContentType());
                if (getContentType() == Book.class) {
                    initMenu(view);
                    initIndex(view);
                }else if (getContentType() == SoundBook.class) {
                    initMenu(view);
                    view.findViewById(R.id.menu_background).setVisibility(View.GONE);
                    view.findViewById(R.id.splash).setVisibility(View.GONE);
                    view.findViewById(R.id.rate_layout).setVisibility(View.GONE);
                    view.findViewById(R.id.transition).setVisibility(View.GONE);
                    view.findViewById(R.id.index_layout).setVisibility(View.GONE);
                    view.findViewById(R.id.buttons_layout).setVisibility(View.GONE);
                    view.findViewById(R.id.buttons_background).setVisibility(View.GONE);
                    view.findViewById(R.id.menu_button).setVisibility(View.GONE);
//                    view.findViewById(R.id.books_button_new).setVisibility(View.GONE);
//                    view.findViewById(R.id.narration_button_new).setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.menu_background).setVisibility(View.GONE);
                    view.findViewById(R.id.splash).setVisibility(View.GONE);
                    view.findViewById(R.id.rate_layout).setVisibility(View.GONE);
                    view.findViewById(R.id.transition).setVisibility(View.GONE);
                    view.findViewById(R.id.index_layout).setVisibility(View.GONE);
                    view.findViewById(R.id.buttons_layout).setVisibility(View.GONE);
                    view.findViewById(R.id.buttons_background).setVisibility(View.GONE);
                    view.findViewById(R.id.menu_button).setVisibility(View.GONE);
                    view.findViewById(R.id.books_button_new).setVisibility(View.GONE);
                    view.findViewById(R.id.narration_button_new).setVisibility(View.GONE);
                }
            }
        });
    }

    public void sendContentStats() {
        String type = (getContentType() == Book.class) ? "book_use" : "game_use";
        int userId = UserManager.getInstance().isLoggedIn ? UserManager.getInstance().user.userID : -1;
        String client = String.format("android %s", AppHelper.VERSION);
        Long time = System.currentTimeMillis() - beginTime;
        String value = String.format(Locale.US, "%d|%d", content.contentID, time / 1000);
        UserManager.getInstance().sendStats(userId, client, type, value, new UserManager.SendStateCallback() {
            @Override
            public void success() {}

            @Override
            public void failed() {}
        });
    }

    @Override
    public void onDestroyView() {
        sendContentStats();
        stopAllAudios();

        if (webView != null) {
            if (Build.VERSION.SDK_INT < 18)
                webView.clearView();
            else
                webView.loadUrl("about:blank");

            webView = null;
        }

        transitionView = null;
        musicPlayer = null;
        narrationPlayer = null;
        soundPlayers = null;
        content = null;
        //==========================================================================================
        // FreeUseTimer
        //==========================================================================================
        if (freeUseTimer != null) {
            freeUseTimer.cancel();
            freeUseTimer = null;
        }
        //==========================================================================================


        super.onDestroyView();
    }


    @Override
    public void onPause() {
        super.onPause();

        try {
            if (webView != null) {
                webView.onPause(); // use pauseTimers and resumeTimers instead
                resumeWebView = true;
            }

            if (narrationPlayer != null && narrationPlayer.isPlaying()) {
                resumeNarrationPlayer = true;
                narrationPlayer.pause();
            }

            if (musicPlayer != null && musicPlayer.isPlaying()) {
                resumeMusicPlayer = true;
                musicPlayer.pause();
            }

            if (soundPlayers != null) {
                for (ABMediaPlayer soundPlayer : soundPlayers)
                    soundPlayer.player.release();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        try {
            if (resumeWebView)
                webView.onResume();

            if (resumeNarrationPlayer) {
                narrationPlayer.start();
                resumeNarrationPlayer = false;
            }

            if (resumeMusicPlayer) {
                musicPlayer.start();
                resumeMusicPlayer = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void backToBooks() {
        prepareForClose();
        ((MainActivity) getActivity()).toggleMusicOnContentClose();
        ((MainActivity) getActivity()).showBooks();
        UIHelper.playButtonSound(getActivity());
    }

    public void backToGames() {
        prepareForClose();
        ((MainActivity) getActivity()).toggleMusicOnContentClose();
        ((MainActivity) getActivity()).showGames();
        UIHelper.playButtonSound(getActivity());
    }

    public void backToHome() {
        prepareForClose();
        ((MainActivity) getActivity()).toggleMusicOnContentClose();
        ((MainActivity) getActivity()).showHome(true);
        UIHelper.playButtonSound(getActivity());
    }

    public void goBack() {
        ((MainActivity) getActivity()).setRateData(MainActivity.RateDataTypes.CONTENT_USED);
        ((MainActivity) getActivity()).rateApp();
        ((MainActivity) getActivity()).getSupportFragmentManager().popBackStack();

//        if (getContentType() == Book.class)
//            backToBooks();
//        else if (content.isQuran)
//            backToHome();
//        else if (content.isQuran)
//            backToHome();
//        else
//            backToGames();
    }

    public Class getContentType() {
        if (content instanceof Book || content.isBook)
            return Book.class;
        if (content instanceof SoundBook || content.isSoundBook)
            return SoundBook.class;
        else if (content instanceof Game)
            return Game.class;

        return null;
    }

    public void stopAllAudios() {
        if (narrationPlayer != null) {
            narrationPlayer.release();
            narrationPlayer = null;
        }

        if (musicPlayer != null) {
            musicPlayer.release();
            musicPlayer = null;
        }

        if (soundPlayers != null)
            for (ABMediaPlayer player : soundPlayers) {
                player.player.release();
                player.player = null;
            }

        soundPlayers = new ArrayList<>();
    }

    public void prepareForClose() {
        stopAllAudios();
    }

    public void parseContentsJson() {
        File contentFile = new File(contentPath, "content.json");
        String contentJsonString = AppHelper.fileGetContents(contentFile);

        bookIndex = new BookIndex();

        try {
            JSONObject jsonObject = new JSONObject(contentJsonString);

            if (getContentType() == Book.class) {
                JSONArray indexArray = jsonObject.getJSONArray("index");

                if (bookIndex.index == null)
                    bookIndex.index = new ArrayList<>();

                for (int i = 0; i < indexArray.length(); i++) {
                    JSONObject indexJson = indexArray.getJSONObject(i);
                    BookIndex.IndexItem indexItem = new BookIndex.IndexItem();
                    indexItem.thumb = indexJson.getString("thumb");
                    indexItem.html = indexJson.getString("html");
                    bookIndex.index.add(indexItem);
                }

            } else
                gameHTML = jsonObject.getString("html");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showContent(View layout) {
        parseContentsJson();

        if (getContentType() == Book.class) {
            showPage(0);
            final View splash = layout.findViewById(R.id.splash);
            splash.animate().withLayer().alpha(0.0f).setDuration(1000).setStartDelay(1000)
                    .setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
                @Override
                public void run() {
                    splash.setVisibility(View.GONE);
                }
            });
        } else
            showGame();
    }


    public void startFreeTimer() {
        if (freeUseTimer != null)
            return;

        if (UserManager.getInstance().isLoggedIn) {
            if (AppHelper.FREE_FOR_REGISTERED.contains(content.folderName))
                return;
        }

        UserManager.getInstance().checkSubscriptionStatus((MainActivity) getActivity(), new UserManager.SubscriptionStatusCallback() {
            @Override
            public void available() {
                // Do nothing
            }

            @Override
            public void notAvailable() {
                freeUseTimer = new CountDownTimer(30000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        // Do nothing
                    }

                    @Override
                    public void onFinish() {
                        ((MainActivity) getActivity()).showAccountWarning(false);
                    }
                }.start();
            }
        });
    }

    //==============================================================================================
    // Book
    //==============================================================================================

    private boolean showPageInProgress;

    public void showPage(final int pageIndex) {
        if (showPageInProgress)
            return;

        startFreeTimer();
        realShowPage(pageIndex);

//        if (AppHelper.env != AppHelper.AlburaghENV.BazaarFree) {
//            if (pageIndex < 3) {
//                realShowPage(pageIndex);
//                return;
//            }
//
//            UserManager.getInstance().checkSubscriptionStatus((MainActivity) getActivity(),
//                    new UserManager.SubscriptionStatusCallback() {
//                        @Override
//                        public void available() {
//                            realShowPage(pageIndex);
//                        }
//
//                        @Override
//                        public void notAvailable() {
//                            ((MainActivity) getActivity()).showAccountWarning(true);
//                        }
//                    });
//        } else
//            realShowPage(pageIndex);
    }

    private void realShowPage(int pageIndex) {
        stopAllAudios();

        showPageInProgress = true;

        File htmlFile = new File(contentPath, bookIndex.index.get(pageIndex).html);
        final String htmlPath = "file://" + htmlFile.getPath();

        transitionView.setVisibility(View.VISIBLE);
        transitionView.animate().withLayer().alpha(1.0f).setDuration(800).withEndAction(new Runnable() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void run() {
                clearWebView();
                webView.loadUrl(htmlPath);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.addJavascriptInterface(new WebViewJavascriptInterface(getActivity()), "alburagh");
                webView.getSettings().setAllowFileAccessFromFileURLs(true);

                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                    }
                });

                transitionView.animate().withLayer().alpha(0.0f).setDuration(800).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            transitionView.setVisibility(View.GONE);
                            showPageInProgress = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void clearWebView() {
        if (webView != null) {
            if (Build.VERSION.SDK_INT < 18)
                webView.clearView();
            else
                webView.loadUrl("about:blank");
        }
    }


    //==============================================================================================
    // Narration
    //==============================================================================================


    private boolean isNarrationPused;
    private String narrationPath;
    private float narrationVolume;

    public void playPageNarration() {
        if (narrationPlayer != null) {
            narrationPlayer.release();
            narrationPlayer = null;
        }

        narrationPlayer = new MediaPlayer();
        try {
            narrationPlayer.setDataSource(getActivity(), Uri.parse(String.format("%s/%s", contentPath, narrationPath)));
            narrationPlayer.prepare();
            narrationPlayer.setLooping(false);
            narrationPlayer.setVolume(1.0f, 1.0f);
            narrationPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (narrationState)
                        narrationPlayer.start();
                }
            });
            narrationPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    mp = null;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //==============================================================================================
    // Menu
    //==============================================================================================

    int menuPositionOffset = 800;
    int layoutPositionOffset = 1500;
    View currentMenuLayout = null;
    boolean animatingMenuLayout = false;

    public void initMenu(View layout) {
        layout.findViewById(R.id.buttons_background).animate().translationYBy(menuPositionOffset).withLayer().setDuration(0).start();
        layout.findViewById(R.id.buttons_layout).animate().translationYBy(menuPositionOffset).withLayer().setDuration(0).start();
        layout.findViewById(R.id.menu_background).setAlpha(0.0f);
        layout.findViewById(R.id.menu_background).setVisibility(View.GONE);

        layout.findViewById(R.id.rate_layout).animate().translationYBy(-layoutPositionOffset).withLayer().setDuration(0).start();

        initRate(layout);
    }

    public void showMenu(final View view) {
        if (togglingMenu)
            return;

        togglingMenu = true;
        view.findViewById(R.id.menu_button).setEnabled(false);

        view.findViewById(R.id.buttons_background).animate().translationYBy(-menuPositionOffset).withLayer().setDuration(500).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                togglingMenu = false;
                view.findViewById(R.id.menu_button).setEnabled(true);
            }
        }).start();
        view.findViewById(R.id.buttons_layout).animate().translationYBy(-menuPositionOffset).withLayer().setDuration(500).setInterpolator(new AccelerateInterpolator()).start();
        view.findViewById(R.id.menu_background).setVisibility(View.VISIBLE);
        view.findViewById(R.id.menu_background).animate().alpha(1.0f).withLayer().setDuration(400).setInterpolator(new AccelerateInterpolator()).start();
    }

    public void hideMenu(final View view) {
        if (togglingMenu)
            return;

        togglingMenu = true;
        view.findViewById(R.id.buttons_layout).animate().translationYBy(menuPositionOffset).withLayer().setDuration(500).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                togglingMenu = false;
            }
        }).start();

        view.findViewById(R.id.buttons_background).animate().translationYBy(menuPositionOffset).withLayer().setDuration(500).setInterpolator(new AccelerateInterpolator()).start();
        view.findViewById(R.id.menu_background).animate().alpha(0.0f).withLayer().setDuration(400).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                view.findViewById(R.id.menu_background).setVisibility(View.GONE);
            }
        });

        hideMenuLayout();
    }

    public void showMenuLayout(final View layout) {
        if (animatingMenuLayout)
            return;

        animatingMenuLayout = true;
        if (currentMenuLayout != null) {
            if (currentMenuLayout.equals(layout))
                return;

            currentMenuLayout.animate().withLayer().translationYBy(-layoutPositionOffset).setDuration(500).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
                @Override
                public void run() {
                    layout.animate().withLayer().translationYBy(layoutPositionOffset).setDuration(500).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            animatingMenuLayout = false;
                        }
                    });
                }
            });
        } else {
            animatingMenuLayout = true;
            layout.animate().withLayer().translationYBy(layoutPositionOffset).setDuration(500).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
                @Override
                public void run() {
                    animatingMenuLayout = false;
                }
            });
        }

        currentMenuLayout = layout;
    }

    public void hideMenuLayout() {
        animatingMenuLayout = true;
        if (currentMenuLayout != null) {
            currentMenuLayout.animate().withLayer().translationYBy(-layoutPositionOffset).setDuration(500).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
                @Override
                public void run() {
                    animatingMenuLayout = false;
                }
            });
            currentMenuLayout = null;
        }
    }


    //==============================================================================================
    // Rate
    //==============================================================================================

    List<ImageView> stars;
    int currentRate = 4;

    public void initRate(final View layout) {
        stars = Arrays.asList(
                (ImageView) layout.findViewById(R.id.rate_star_01),
                (ImageView) layout.findViewById(R.id.rate_star_02),
                (ImageView) layout.findViewById(R.id.rate_star_03),
                (ImageView) layout.findViewById(R.id.rate_star_04),
                (ImageView) layout.findViewById(R.id.rate_star_05)
        );

        LinearLayout rateButtons = (LinearLayout) layout.findViewById(R.id.rate_buttons);
        rateButtons.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE)
                    setRateStar(event.getX() / v.getWidth());

                return false;
            }
        });

        setRateStar(currentRate);

        layout.findViewById(R.id.rate_send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenu(layout);
                UIHelper.playButtonSound(getActivity());
                WebService.getInstance().rateContent(content.contentID, currentRate, new WebService.RateCallback() {
                    @Override
                    public void success() {
                        try {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage(R.string.rate_thanks_message)
                                    .setPositiveButton(R.string.ok, null)
                                    .show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failed() {
                        // Do nothing
                    }
                });
            }
        });
    }

    public void setRateStar(float rate) {
        for (int i = 0; i < stars.size(); i++) {
            if (i <= Math.floor(rate * 5))
                stars.get(i).setImageResource(R.drawable.rate_active);
            else
                stars.get(i).setImageResource(R.drawable.rate_inactive);
        }
    }


    //==============================================================================================
    // Index
    //==============================================================================================

    public void initIndex(final View layout) {
        List<Fragment> pages = new ArrayList<>();
        int pageCount = (int) Math.ceil(bookIndex.index.size() / 6.0f);
        for (int i = 0; i < pageCount; i++) {
            IndexPageFragment index = new IndexPageFragment();
            Gson gson = new Gson();
            Bundle bundle = new Bundle();
            bundle.putInt("start", i * 6);
            bundle.putString("bookIndex", gson.toJson(bookIndex));
            bundle.putString("path", contentPath);
            index.setArguments(bundle);

            index.setShowPageCallback(new IndexPageFragment.IndexCallback() {
                @Override
                public void indexClicked(int index) {
                    showPage(index);
                    hideMenu(layout);
                }

                @Override
                public void close() {
                    hideMenu(layout);
                }
            });

            pages.add(index);
        }

        Collections.reverse(pages);

        layout.findViewById(R.id.index_layout).animate().translationYBy(-layoutPositionOffset).withLayer().setDuration(0).start();

        IndexPagerAdaptor adapter = new IndexPagerAdaptor(getActivity().getSupportFragmentManager(), pages);
        ViewPager indexPager = (ViewPager) layout.findViewById(R.id.index_pager);
        indexPager.setAdapter(adapter);
        indexPager.setCurrentItem(pageCount);
    }

    private class IndexPagerAdaptor extends FragmentStatePagerAdapter {
        private List<Fragment> fragments;

        public IndexPagerAdaptor(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }


    //==============================================================================================
    // Game
    //==============================================================================================

    private void showGame() {
        stopAllAudios();

        File htmlFile = new File(contentPath, gameHTML);
        final String htmlPath = "file://" + htmlFile.getPath();

        clearWebView();
        webView.loadUrl(htmlPath);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebViewJavascriptInterface(getActivity()), "alburagh");
        webView.getSettings().setAllowFileAccessFromFileURLs(true);

        startFreeTimer();
    }


    //==============================================================================================
    // Content Callbacks
    //==============================================================================================

    // TODO: finish it if needed
    public void pauseWebView() {
        webView.loadUrl("javascript:alburagh_pause_all()");
    }

    // TODO: finish it if needed
    public void setResumeWebView() {
        webView.loadUrl("javascript:alburagh_resume_all()");
    }

    // TODO: Fix Volumes
    public class WebViewJavascriptInterface {
        private Context context;

        public WebViewJavascriptInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void openPage(final int index) {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    showPage(index);
                }
            });
        }

        @JavascriptInterface
        public void bookFinished() {


        }

        @JavascriptInterface
        public void bookExit() {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    if (content.isBook) {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.showBooks();
                        stopAllAudios();
                        mainActivity.toggleMusicOnContentClose();
                    } else if (content.isQuran) {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.showHome(true);
                        stopAllAudios();
                        mainActivity.toggleMusicOnContentClose();
                    } else if (content.isSoundBook) {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.showSoundBooks();
                        stopAllAudios();
                        mainActivity.toggleMusicOnContentClose();
                    } else {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.showGames();
                        stopAllAudios();
                        mainActivity.toggleMusicOnContentClose();
                    }


                    if (freeUseTimer != null)
                        freeUseTimer.cancel();
                }
            });
        }

        @JavascriptInterface
        public void playMusic(final String path, final float volume) {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    if (musicPlayer != null) {
                        musicPlayer.release();
                        musicPlayer = null;
                    }

                    musicPlayer = new MediaPlayer();
                    try {
                        musicPlayer.setDataSource(getActivity(), Uri.parse(String.format("%s/%s", contentPath, path)));
                        musicPlayer.prepare();
                        musicPlayer.setLooping(true);
                        musicPlayer.setVolume(1.0f, 1.0f);
                        musicPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                musicPlayer.start();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                                mp = null;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @JavascriptInterface
        public void stopMusic() {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    if (musicPlayer != null) {
                        musicPlayer.release();
                        musicPlayer = null;
                    }
                }
            });
        }

        @JavascriptInterface
        public void playNarration(final String path, final float volume) {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    narrationPath = path;
                    narrationVolume = volume;
                    playPageNarration();
                }
            });
        }

        @JavascriptInterface
        public void stopNarration() {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    if (narrationPlayer != null) {
                        narrationPlayer.release();
                        narrationPlayer = null;
                    }
                }
            });
        }

        @JavascriptInterface
        public void playSound(String path, float volume) {
            for (final ABMediaPlayer player : soundPlayers) {
                if (player.tag.equals(path))
                    return;
            }

            final ABMediaPlayer newPlayer = new ABMediaPlayer();
            newPlayer.player = new MediaPlayer();
            try {
                newPlayer.player.setDataSource(getActivity(), Uri.parse(String.format("%s/%s", contentPath, path)));
                newPlayer.player.prepare();
                newPlayer.tag = path;
                newPlayer.player.setVolume(1.0f, 1.0f);
                newPlayer.player.setLooping(false);
                newPlayer.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        newPlayer.player.start();
                    }
                });

                soundPlayers.add(newPlayer);

                newPlayer.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        for (ABMediaPlayer abMediaPlayer : soundPlayers) {
                            if (abMediaPlayer.player.equals(mp)) {
                                mp.release();
                                mp = null;
                                soundPlayers.remove(abMediaPlayer);
                                break;
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
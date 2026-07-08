package com.alburagh.alburagh;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.alburagh.alburagh.Fragments.AboutFragment;
import com.alburagh.alburagh.Fragments.Account.AccountFragment;
import com.alburagh.alburagh.Fragments.Account.AccountWarningFragment;
import com.alburagh.alburagh.Fragments.BooksFragment;
import com.alburagh.alburagh.Fragments.ContentDetailsFragment;
import com.alburagh.alburagh.Fragments.ContentFragment;
import com.alburagh.alburagh.Fragments.FavoriteFragment;
import com.alburagh.alburagh.Fragments.FreeFragment;
import com.alburagh.alburagh.Fragments.GamesFragment;
import com.alburagh.alburagh.Fragments.HomeFragment;
import com.alburagh.alburagh.Fragments.LockFragment;
import com.alburagh.alburagh.Fragments.MenuFragment;
import com.alburagh.alburagh.Fragments.PhoneFragment;
import com.alburagh.alburagh.Fragments.SettingFragment;
import com.alburagh.alburagh.Fragments.SoundBookFragment;
import com.alburagh.alburagh.Helpers.AppHelper;
import com.alburagh.alburagh.Helpers.Setting;
import com.alburagh.alburagh.Helpers.UserManager;
import com.alburagh.alburagh.Helpers.WebService;
import com.alburagh.alburagh.IAB.IabHelper;
import com.alburagh.alburagh.IAB.IabResult;
import com.alburagh.alburagh.IAB.Inventory;
import com.alburagh.alburagh.IAB.Purchase;
import com.alburagh.alburagh.IAB.SkuDetails;
import com.alburagh.alburagh.Models.Book;
import com.alburagh.alburagh.Models.Content;
import com.alburagh.alburagh.Models.Subscription;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "";
    public MediaPlayer musicPlayer;
    public Long beginTime;
    public String yearlyPr, monthlyPr, sixMonthPr;
    public boolean BILLING_NOT_SUPPORTED;
    public Boolean unlocked = false;
    public Activity thisActivity;
    public static String PACKAGE_NAME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PACKAGE_NAME = getApplicationContext().getPackageName();
        setWindowOptions();
        thisActivity = this;
        Setting.getInstance().setContext(this);

        beginTime = System.currentTimeMillis();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        AppHelper.deleteUncompletedDownloads();
        showHome(true);

        UserManager.getInstance().autoLogin();
        UserManager.getInstance().autoCheckSubscription(this);

        File file = this.getBaseContext().getExternalFilesDir("cache");
        if (!file.exists())
            file.mkdir();



        //setRateData(RateDataTypes.FIRST_TIME);


        /*
        //==========================================================================================
        //  Check For Update one more in day
        //==========================================================================================
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        formattedDate = formattedDate.trim();

        String rootDir = Environment.getExternalStorageDirectory().toString();
        File contentDir = new File(rootDir + "/alburagh/alburagh");
        File updFile = new File(contentDir, "upd.txt");
        String updtxt = "";

        if (updFile.exists()) {

            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(updFile));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            }
            catch (IOException e) {

            }
            updtxt = text.toString().trim();

            if (!formattedDate.contains(updtxt)){
                VersionChecker versionChecker = new VersionChecker();
                String lastestVersion = "";
                try {
                    lastestVersion = versionChecker.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if(lastestVersion != getString(R.string.version_name)){
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage(R.string.new_version_available_message)
                            .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .setNegativeButton(R.string.later, null)
                            .show();
                }

                updFile.delete();
                File contentFile = new File(contentDir, "upd.txt");
                AppHelper.filePutContents(contentFile, formattedDate);
            }else{
                //Do Nothings...
            }

        }else{

            VersionChecker versionChecker = new VersionChecker();
            String lastestVersion = "";
            try {
                lastestVersion = versionChecker.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if(lastestVersion != getString(R.string.version_name)){
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(R.string.new_version_available_message)
                        .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(R.string.later, null)
                        .show();
            }
            File contentFile = new File(contentDir, "upd.txt");
            AppHelper.filePutContents(contentFile, formattedDate);

        }

        //==========================================================================================
        */
    }

    @Override
    protected void onStart() {
        super.onStart();
        playMusicIfNecessary();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseMusic(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setWindowOptions();
        playMusicIfNecessary();

        if (Build.VERSION.SDK_INT >= 23) {
            if (!AppHelper.checkedForPermission) {
                AppHelper.checkedForPermission = true;
                checkForPermission();
            }
        } else
            afterWritePermission();
    }

    private static long back_pressed;

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_place_holder);

        if (fragment instanceof HomeFragment) {
            if (back_pressed + 3000 > System.currentTimeMillis()) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            } else
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(R.string.message_to_exit)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();

        } else if (fragment instanceof BooksFragment ||
                fragment instanceof GamesFragment ||
                fragment instanceof SettingFragment)
            showHome(false);

        else if (fragment instanceof ContentFragment)
            ((ContentFragment) fragment).goBack();

        else if (fragment instanceof ContentDetailsFragment)
            getSupportFragmentManager().popBackStack();

        else if (fragment instanceof FavoriteFragment)
            getSupportFragmentManager().popBackStack();

        else
            showHome(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void playMusicIfNecessary() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_place_holder);
        if (currentFragment.getClass() != ContentFragment.class) {
            if (Setting.getInstance().preferences.getBoolean("music_state", true))
                playMusic();
        }
    }

    private void setWindowOptions() {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion >= 19) {
            final int flags_19 = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            getWindow().getDecorView().setSystemUiVisibility(flags_19);
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                        decorView.setSystemUiVisibility(flags_19);
                }
            });
        } else {
            final int flags_16 = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;

            getWindow().getDecorView().setSystemUiVisibility(flags_16);
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                        decorView.setSystemUiVisibility(flags_16);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sendContentState();
    }

    public void sendContentState() {
        int userId = UserManager.getInstance().isLoggedIn ? UserManager.getInstance().user.userID : -1;
        String client = String.format("android %s", AppHelper.VERSION);
        Long time = System.currentTimeMillis() - beginTime;
        UserManager.getInstance().sendStats(userId, client, "app_use",
                String.valueOf(time / 1000), new UserManager.SendStateCallback() {
                    @Override
                    public void success() { }

                    @Override
                    public void failed() { }
                });
    }

    public void hideSoftKeyboard() {
        if (findViewById(R.id.main) != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(findViewById(R.id.main).getWindowToken(), 0);
        }
    }


    //==============================================================================================
    //== Navigation
    //==============================================================================================

    public void showLoadingLayout() {
        findViewById(R.id.loading_layout).setBackgroundColor(Color.argb(160, 0, 0, 0));
        findViewById(R.id.loading_layout).setVisibility(View.VISIBLE);
    }

    public void hideLoadingLayout() {
        findViewById(R.id.loading_layout).setVisibility(View.GONE);
    }

    public void showHome(boolean firstTime) {
        findViewById(R.id.mainbg).setBackgroundColor(Color.rgb(68, 118, 187));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_place_holder, new HomeFragment());
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        if (!firstTime)
            transaction.addToBackStack("home");
        transaction.commit();
    }

    public void showAbout() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_place_holder, new AboutFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("about")
                .commit();
    }

    public void showBooks() {
        findViewById(R.id.mainbg).setBackgroundColor(Color.rgb(29, 159, 253));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_place_holder, new BooksFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("books")
                .commit();
    }

    public void showSoundBooks() {
        findViewById(R.id.mainbg).setBackgroundColor(Color.rgb(29, 159, 253));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_place_holder, new SoundBookFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("sound_books")
                .commit();
    }

    public void showGames() {
        findViewById(R.id.mainbg).setBackgroundColor(Color.rgb(82, 33, 125));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_place_holder, new GamesFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("games")
                .commit();
    }

    public void showMenu() {
        findViewById(R.id.mainbg).setBackgroundColor(Color.rgb(82, 33, 125));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_place_holder, new MenuFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("menu")
                .commit();
    }

    public void showSettings() {
        findViewById(R.id.mainbg).setBackgroundColor(Color.rgb(102, 44, 146));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_place_holder, new SettingFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("settings")
                .commit();
    }

    Fragment favoriteReferrer;

    public void showFavoriteContent(Fragment referrer) {
        favoriteReferrer = referrer;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_place_holder, new FavoriteFragment())
                .addToBackStack("favorite")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void favoriteBack() {
        if (favoriteReferrer != null) {
            String FragmentName = favoriteReferrer.toString();
            Log.i("favoriteBack: ", "favoriteBack: " + favoriteReferrer + " " + FragmentName);
            if (FragmentName.contains("HomeFragment"))
                findViewById(R.id.mainbg).setBackgroundColor(Color.rgb(68, 118, 187));
            else if (FragmentName.contains("BooksFragment"))
                findViewById(R.id.mainbg).setBackgroundColor(Color.rgb(29, 159, 253));
            else if (FragmentName.contains("GamesFragment"))
                findViewById(R.id.mainbg).setBackgroundColor(Color.rgb(82, 33, 125));

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_place_holder, favoriteReferrer)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
    }

    public void showBook(Content content) {
        ContentFragment contentFragment = new ContentFragment();

        Bundle bundle = new Bundle();
        bundle.putString("content", new Gson().toJson(content));
        contentFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_place_holder, contentFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("book")
                .commit();

        WebService.getInstance().increaseReadings(content.contentID, new WebService.IncreaseReadCountCallback() {
            @Override
            public void success() { }

            @Override
            public void failed() { }
        });
    }

    public void playGame(Content content) {
        ContentFragment contentFragment = new ContentFragment();

        Bundle bundle = new Bundle();
        bundle.putString("content", new Gson().toJson(content));
        contentFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_place_holder, contentFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("game")
                .commit();
    }

    public void showSoundBook(Content content) {
        ContentFragment contentFragment = new ContentFragment();

        Bundle bundle = new Bundle();
        bundle.putString("content", new Gson().toJson(content));
        contentFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_place_holder, contentFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("soundBook")
                .commit();

        WebService.getInstance().increaseReadings(content.contentID, new WebService.IncreaseReadCountCallback() {
            @Override
            public void success() { }

            @Override
            public void failed() { }
        });
    }

    public void showAccountView(Runnable onClose) {
        if (unlocked) {
            unlocked = false;
            AccountFragment accountFragment = new AccountFragment();
            accountFragment.onClose = onClose;

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_place_holder, new AccountFragment())
                    .addToBackStack("account")
                    .commit();
        } else
            showLock();
    }

    public void showQuran(Content content) {
        ContentFragment contentFragment = new ContentFragment();

        Bundle bundle = new Bundle();
        bundle.putString("content", new Gson().toJson(content));
        contentFragment.setArguments(bundle);

        bundle.getString("content");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_place_holder, contentFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("quran")
                .commit();
    }

    public void showDetails(Book book) {
        Bundle bundle = new Bundle();
        bundle.putString("book", new Gson().toJson(book));
        ContentDetailsFragment fragment = new ContentDetailsFragment();
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_place_holder, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("details")
                .commit();
    }

    public void showAccountWarning(boolean closeable) {
        AccountWarningFragment fragment = new AccountWarningFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("closeable", closeable);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_place_holder, fragment)
                .addToBackStack("account_warning")
                .commit();
    }

    public void showFree() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_place_holder, new FreeFragment())
                .addToBackStack("free")
                .commit();
    }

    public void showLock() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_place_holder, new LockFragment())
                .addToBackStack("lock")
                .commit();
    }

    public void showPhone() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_place_holder, new PhoneFragment())
                .addToBackStack("Phone")
                .commit();
    }


    //==============================================================================================
    //== Music
    //==============================================================================================

    public void initMusic() {
        if (musicPlayer == null) {
            musicPlayer = MediaPlayer.create(this, R.raw.background);
            musicPlayer.setLooping(true);
        }
    }

    public void playMusic() {
        if (musicPlayer == null)
            initMusic();
        musicPlayer.start();
        Setting.getInstance().editor.putBoolean("music_state", true).commit();
    }

    public void pauseMusic(Boolean setState) {
        if (musicPlayer != null) {
            musicPlayer.pause();
            if (setState)
                Setting.getInstance().editor.putBoolean("music_state", false).commit();
        }
    }

    public void toggleMusicOnContentOpen() {
        if (musicPlayer != null && musicPlayer.isPlaying()) {
            musicPlayer.pause();
            Setting.getInstance().setVariable("resume_music_on_content_close", "true");
        } else
            Setting.getInstance().setVariable("resume_music_on_content_close", "false");
    }

    public void toggleMusicOnContentClose() {
        if (Setting.getInstance().getVariable("resume_music_on_content_close").equals("true")) {
            if (musicPlayer != null)
                musicPlayer.start();
        }
    }


    //==============================================================================================
    //== In-app Billing
    //==============================================================================================

    IabHelper mHelper;

    boolean mSubscribedToInfiniteAccess = false;
    boolean mAutoRenewEnabled = false;

    static final int RC_REQUEST = 10001;

    private InitIABCallback initIABCallback;
    private PurchaseCallback purchaseCallback;

    public void initIAB(final InitIABCallback callback) {
        Log.d(AppHelper.TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, AppHelper.getPublicKey());
        mHelper.enableDebugLogging(true);

        initIABCallback = callback;

        Log.d(AppHelper.TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d("start", "In-app Billing setup failed: " +
                            result);
                    BILLING_NOT_SUPPORTED = true;
                } else {
                    Log.d("start", "In-app Billing is set up OK");
                    Log.d(AppHelper.TAG, "Setup successful. Querying inventory.");
                    String[] moreSKUs = {AppHelper.SKU_MONTHLY_SUBSCRIBED, AppHelper.SKU_ANNUAL_SUBSCRIBED,
                            AppHelper.SKU_6MONTH_SUBSCRIBED};
                    mHelper.queryInventoryAsync(true, Arrays.asList(moreSKUs), mGotInventoryListener);
                    BILLING_NOT_SUPPORTED = false;
                }

                if (mHelper == null)
                    return;
            }
        });
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(final IabResult result, final Inventory inventory) {
            Log.d(AppHelper.TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) {
                initIABCallback.failed(result);
                return;
            }

            // Is it a failure?
            if (result.isFailure()) {
                initIABCallback.failed(result);
                initIABCallback = null;
                return;
            }

            Log.d(AppHelper.TAG, "Query inventory was successful.");

            // First find out which subscription is auto renewing

            monthlyPr = inventory.getSkuDetails(AppHelper.SKU_MONTHLY_SUBSCRIBED).getPrice();
            sixMonthPr = inventory.getSkuDetails(AppHelper.SKU_6MONTH_SUBSCRIBED).getPrice();
            yearlyPr = inventory.getSkuDetails(AppHelper.SKU_ANNUAL_SUBSCRIBED).getPrice();
            Log.i("s", "onQueryInventoryFinished: " + monthlyPr + " " + yearlyPr + " " + sixMonthPr);

            Purchase monthlySub = inventory.getPurchase(AppHelper.SKU_MONTHLY_SUBSCRIBED);
            Purchase annualSub = inventory.getPurchase(AppHelper.SKU_ANNUAL_SUBSCRIBED);
            Purchase sixmonthSub = inventory.getPurchase(AppHelper.SKU_6MONTH_SUBSCRIBED);

            if (monthlySub == null && annualSub == null) {
                if (initIABCallback != null)
                    initIABCallback.failed(result);
            } else {
                if (monthlySub == null)
                    Setting.getInstance().removeVariable(UserManager.CURRENT_MONTHLY_SUB);
                else {
                    UserManager.getInstance().onlinePurchaseValidation(Subscription.Type.MONTHLY,
                            monthlySub.getToken(), new UserManager.CheckSubscriptionCallback() {
                                @Override
                                public void success(Subscription subscription) {
                                    subscription.type = Subscription.Type.MONTHLY;
                                    processInventory(subscription, inventory, result);
                                }

                                @Override
                                public void failed() {
                                    if (initIABCallback != null)
                                        initIABCallback.failed(result);
                                }
                            });
                }

                if (annualSub == null)
                    Setting.getInstance().removeVariable(UserManager.CURRENT_ANNUAL_SUB);
                else {
                    UserManager.getInstance().onlinePurchaseValidation(Subscription.Type.ANNUAL,
                            annualSub.getToken(), new UserManager.CheckSubscriptionCallback() {
                                @Override
                                public void success(Subscription subscription) {
                                    subscription.type = Subscription.Type.ANNUAL;
                                    processInventory(subscription, inventory, result);
                                }

                                @Override
                                public void failed() {
                                    if (initIABCallback != null)
                                        initIABCallback.failed(result);
                                }
                            });
                }
            }
        }
    };

    public void processInventory(Subscription subscription, Inventory inventory, IabResult result) {
        SkuDetails monthlySkuDetails = inventory.getSkuDetails(AppHelper.SKU_MONTHLY_SUBSCRIBED);
        String monthlyPrice = (monthlySkuDetails != null) ? monthlySkuDetails.getPrice() : null;

        SkuDetails annualSkuDetails = inventory.getSkuDetails(AppHelper.SKU_ANNUAL_SUBSCRIBED);
        String annualPrice = (annualSkuDetails != null) ? annualSkuDetails.getPrice() : null;

        SkuDetails sixmonthSkuDetails = inventory.getSkuDetails(AppHelper.SKU_6MONTH_SUBSCRIBED);
        String sixmonthPrice = (sixmonthSkuDetails != null) ? sixmonthSkuDetails.getPrice() : null;

        subscription.price = (subscription.type == Subscription.Type.MONTHLY) ? monthlyPrice : annualPrice;

        // TODO: verify payload...
        // The user is subscribed if either subscription exists, even if neither is auto renewing
        // mSubscribedToInfiniteAccess = (monthlySub != null && verifyDeveloperPayload(monthlySub)) ||
        //        ((annualSub != null) && verifyDeveloperPayload(annualSub));

        if (initIABCallback != null) {
            if (result.getResponse() == 1) {
                initIABCallback.onReceivedInventory(null, monthlyPrice, annualPrice);
            } else {
                if (subscription.type == Subscription.Type.MONTHLY)
                    Setting.getInstance().setVariable(UserManager.CURRENT_MONTHLY_SUB, new Gson().toJson(subscription));
                else if (subscription.type == Subscription.Type.ANNUAL)
                    Setting.getInstance().setVariable(UserManager.CURRENT_MONTHLY_SUB, new Gson().toJson(subscription));

                initIABCallback.onReceivedInventory(subscription, monthlyPrice, annualPrice);
            }

            initIABCallback = null;
        }
    }

    public interface InitIABCallback {
        void onReceivedInventory(Subscription subscription, String monthlyPrice, String annualPrice);

        void failed(IabResult result);
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(AppHelper.TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            if (mHelper == null)
                return;

            if (result.isFailure()) {
                purchaseCallback.failed(result);
                setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                purchaseCallback.failed(result);
                setWaitScreen(false);
                return;
            }

            Log.d(AppHelper.TAG, "Purchase successful.");

            if (purchase.getSku().equals(AppHelper.SKU_MONTHLY_SUBSCRIBED)
                    || purchase.getSku().equals(AppHelper.SKU_ANNUAL_SUBSCRIBED)) {
                Log.d(AppHelper.TAG, "Subscription purchased.");
                mSubscribedToInfiniteAccess = true;

                if (purchaseCallback != null) {
                    purchaseCallback.onSubscriptionPurchased(purchase.getToken());
                    purchaseCallback = null;
                }
            }
        }
    };

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }

    public void buySubscription(String payload, Subscription.Type type, PurchaseCallback callback) {
        purchaseCallback = callback;
        String sku = (type == Subscription.Type.MONTHLY) ? AppHelper.SKU_MONTHLY_SUBSCRIBED : AppHelper.SKU_ANNUAL_SUBSCRIBED;
        mHelper.launchPurchaseFlow(this, payload, IabHelper.ITEM_TYPE_SUBS, RC_REQUEST, mPurchaseFinishedListener, payload);
    }

    public interface PurchaseCallback {
        void onSubscriptionPurchased(String token);
        void failed(IabResult result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(AppHelper.TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null)
            return;

        if (!mHelper.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
        else
            Log.d(AppHelper.TAG, "onActivityResult handled by IABUtil.");
    }

    void setWaitScreen(boolean set) {
        if (set)
            showLoadingLayout();
        else
            hideLoadingLayout();
    }


    //==============================================================================================
    //== Rate Application
    //==============================================================================================

    public enum RateDataTypes {
        APP_OPEN,
        CONTENT_USED,
        FIRST_TIME,
        LATER,
        RATED,
    }

    private class RateData {
        int appRuns = 0;
        int contentUsed = 0;
        long firstTime = 0;
        boolean rated = false;
    }

    public void setRateData(RateDataTypes type) {
        RateData rateData = new Gson().fromJson(Setting.getInstance().getVariable("rate_data"), RateData.class);
        if (rateData != null) {
            if (type == RateDataTypes.APP_OPEN)
                rateData.appRuns += 1;
            else if (type == RateDataTypes.CONTENT_USED)
                rateData.contentUsed += 1;
            else if (type == RateDataTypes.FIRST_TIME && rateData.firstTime == 0)
                rateData.firstTime = System.currentTimeMillis();
            else if (type == RateDataTypes.LATER)
                rateData.firstTime = 0;
            else if (type == RateDataTypes.RATED)
                rateData.rated = true;

        } else
            rateData = new RateData();

        Setting.getInstance().setVariable("rate_data", new Gson().toJson(rateData));
    }

    public void rateApp() {
        RateData rateData = new Gson().fromJson(Setting.getInstance().getVariable("rate_data"), RateData.class);
        if (rateData != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(rateData.firstTime);
            calendar.add(Calendar.DAY_OF_YEAR, 1);

            if (calendar.getTimeInMillis() < System.currentTimeMillis() && rateData.appRuns >= 2
                    && rateData.contentUsed > 4 && !rateData.rated)
                realRateApp();
        }
    }

    public void realRateApp() {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(R.string.rate_message)
                .setPositiveButton(R.string.rate_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_EDIT);
                            intent.setData(Uri.parse("bazaar://details?id=" + getPackageName()));
                            intent.setPackage("com.farsitel.bazaar");
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        setRateData(RateDataTypes.RATED);
                    }
                })
                .setNegativeButton(R.string.rate_later, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setRateData(RateDataTypes.LATER);
                    }
                })
                .setNeutralButton(R.string.rate_newer, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setRateData(RateDataTypes.RATED);
                    }
                })
                .show();
    }


    //==============================================================================================
    //== Permission
    //==============================================================================================

    private final int ALBURAGH_PERMISSION_CODE = 101;

    public void checkForPermission() {
        List<String> permissions = new ArrayList<>();
        Log.i("checkForPermission: ", "checkForPermission: " + permissions.isEmpty());
        int writePermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int accountPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);

        Boolean android11Access = false;
        if(Build.VERSION.SDK_INT >= 30) android11Access = Environment.isExternalStorageManager();
        Log.i(TAG, "checkForPermission3: "+android11Access.toString());
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED && !android11Access) {
                /*new AlertDialog.Builder(this)
                        .setTitle(R.string.title_files_permission)
                        .setMessage(R.string.write_permission_message)
                        .setPositiveButton(R.string.grant_permission_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(Build.VERSION.SDK_INT >= 30) {
                                    if (!Environment.isExternalStorageManager()) {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }else if (writePermissionCheck != PackageManager.PERMISSION_GRANTED)
                                        ActivityCompat.requestPermissions(thisActivity,
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                ALBURAGH_PERMISSION_CODE);
                                }
                            }
                        })
                        .setNegativeButton(R.string.later, null)
                        .show();*/
            } else {
                afterWritePermission();
            }
//
//            if (checkSelfPermission(Manifest.permission.GET_ACCOUNTS)
//                    != PackageManager.PERMISSION_GRANTED) {
//                new AlertDialog.Builder(this)
//                        .setTitle(R.string.title_accounts_permission)
//                        .setMessage(R.string.request_get_account_permission_message)
//                        .setPositiveButton(R.string.grant_permission_button, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                permissions.add(Manifest.permission.GET_ACCOUNTS);
//                                if (!permissions.isEmpty()) {
//                                    ActivityCompat.requestPermissions(thisActivity,
//                                            new String[]{Manifest.permission.GET_ACCOUNTS},
//                                            ALBURAGH_PERMISSION_CODE);
//                                }else afterAccountPermission();
//                            }
//                        })
//                        .setNegativeButton(R.string.permit_permission_button, null)
//                        .show();
//            } else
//                afterAccountPermission();

        }



        Log.i("checkForPermission: ", "checkForPermission: " + permissions.isEmpty());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        /*
        if (Build.VERSION.SDK_INT >= 23) {
            switch (requestCode) {
                case ALBURAGH_PERMISSION_CODE: {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        new AlertDialog.Builder(this)
                                .setMessage(R.string.write_permission_message)
                                .setPositiveButton(R.string.grant_permission_button, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        checkForPermission();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .show();
                    } else
                        afterWritePermission();

                    if (checkSelfPermission(Manifest.permission.GET_ACCOUNTS)
                            != PackageManager.PERMISSION_GRANTED) {
                        new AlertDialog.Builder(this)
                                .setMessage(R.string.request_get_account_permission_message)
                                .setPositiveButton(R.string.grant_permission_button, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        checkForPermission();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .show();
                    } else
                        afterAccountPermission();
                }
            }
        } else
            afterWritePermission();

         */
    }

    public void afterWritePermission() {
        AppHelper.firstTimeFixes(this);
    }

    public void afterAccountPermission() { }

}

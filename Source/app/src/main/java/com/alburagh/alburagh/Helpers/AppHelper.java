package com.alburagh.alburagh.Helpers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import com.alburagh.alburagh.MainActivity;
import com.alburagh.alburagh.Models.Country;
import com.alburagh.alburagh.Models.Subscription;
import com.alburagh.alburagh.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AppHelper {
    private static AppHelper instance = null;

    private AppHelper() { }

    public static AppHelper getInstance() {
        if (instance == null)
            instance = new AppHelper();
        return instance;
    }

    public static final String VERSION = "2.0";
    public static final String LANGUAGE = "ar";
    public static final String TAG = "ALBURAGH";

    public static final String BAZAAR_URL = "https://cafebazaar.ir/app/com.alburagh.alburagh/?l=fa";

    public static boolean gotContentListFromServer = false;
    public static boolean homeSoundPlayed = false;

    public static boolean checkedForPermission;

    public static List<String> FREE_FOR_REGISTERED = new ArrayList<String>() {{
        add("felfeli_01_diko_ar");
        add("aktob_02_malek_falah_ar");
        add("find_differences_01_ar");
        add("find_hidden_boy_ar");
    }};


    //==============================================================================================
    //== Application
    //==============================================================================================

    static final String CONTENT_LIST_FILENAME = "content_list.json";

    public static File getContentFolder() {
        String rootDir = Environment.getExternalStorageDirectory().toString();

        File contentDir = new File(rootDir +  "/Android/data/"+MainActivity.PACKAGE_NAME+"/contents");
        if (!contentDir.exists()) {
            try {
                if (contentDir.mkdirs())
                    return contentDir;
                else
                    return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        File noMedia = new File(contentDir, ".nomedia");
        try {
            if (!noMedia.exists())
                noMedia.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentDir;
    }

    static public List<Country> getCountryList(Context context, String lang) {
        String filename = String.format("country_list_%s.json", lang);
        String content = getAssetTextFile(context, filename);

        try {
            JSONArray countriesJson = new JSONArray(content);
            List<Country> countries = new ArrayList<>();
            for (int i = 0; i < countriesJson.length(); i++) {
                JSONObject countryJson = countriesJson.getJSONObject(i);
                Country country = new Country();
                country.englishName = countryJson.getString("en");
                country.arabicName = countryJson.getString("ar");
                country.farsiName = countryJson.getString("fa");
                country.ISO2 = countryJson.getString("iso_2");
                country.ISO3 = countryJson.getString("iso_3");
                countries.add(country);
            }

            return countries;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static MediaPlayer soundPlayer;

    static public void playSound(Context context, int id, float volume) {
        if (soundPlayer != null) {
            soundPlayer.release();
            soundPlayer = null;
        }

        soundPlayer = MediaPlayer.create(context, id);
        soundPlayer.setVolume(volume, volume);
        soundPlayer.start();
    }

    public static void shareApp(Context context) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        share.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_title));
        share.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.link_share));

        context.startActivity(Intent.createChooser(share, "Share link!"));
    }

    public static void firstTimeFixes(Context context) {
        String firstTime = Setting.getInstance().getVariable("first_run");
        if (!firstTime.equals("1")) {
            // Delete content list file if exists
            File contentListFile = new File(getContentFolder(), "content_list.json");
            if (contentListFile.exists())
                contentListFile.delete();
        }

        Setting.getInstance().setVariable("first_run", "1");
    }


    //==============================================================================================
    //== Subscription
    //==============================================================================================

    public static String SKU_MONTHLY_SUBSCRIBED = "monthly_subs";
    public static String SKU_6MONTH_SUBSCRIBED = "6month_subs";
    public static String SKU_ANNUAL_SUBSCRIBED = "yearly_subs";

    public enum AlburaghENV {
        GooglePlay,
        Bazaar,
    }

    private static final String PUBLIC_KEY_GOOGLE_PLAY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgUnAPZgNUuiRyyjrrotmUl+sBV/3BGZakfHBsVfu8/iO6BgRXBTR/FErn6cZdpxWxRlBPYwSUbMwy0Kd6v95JPOio8l4SUn5FikxrnQXRgbMBM7RGwGOsoPJrJpq9/O3u/ckVWstTygoARwswAQroteVQ+A3assyLCV+Qbk/IrQhwtNOXciICv+sa8Q8wxEPlJpBc2L6x7//3mbqKxHS81UqdLIbFcxQUk3OcuuvspvXTVQWJcDHYdzRpFIWMAaDSn7GVj8nLSbvF5iSNI1uDBm9pyFSvNUcSgJole7NzWYPc8+3tVSaheqjUxdxZ2+QnzpyVi+F1PbgHRoJWT83wwIDAQAB";
    private static final String PUBLIC_KEY_CAFE_BAZAAR = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwDKXoyjzOw8IHwPr6kPChDENRnBzIGdNjREasC/L/pWv/wnawIBrRQu9hiHuWOoPcNjRXM66rg5wyyzm/uLahg1nRrFGmmsWoXXCJPM0c65Ft2KrFkPuDqcMi3dMYx5kUjdxyzcoeapZQ48zplhPpegC36s75QncGxrdMUeWaHai2D3VCN1ieDKpG19td0OtnsCKAtWM8sAHjp6+MQUX4ImH0863fPIOFBkeX3934UCAwEAAQ==";

    public static final AlburaghENV env = AlburaghENV.GooglePlay;
    public static boolean SubscriptionExist = false;

    public static String getPublicKey() {
        if (env == AlburaghENV.Bazaar)
            return PUBLIC_KEY_CAFE_BAZAAR;
        else
            return PUBLIC_KEY_GOOGLE_PLAY;
    }

    public static String subscriptionTypeToSku(Subscription.Type type) {
        if (type == Subscription.Type.MONTHLY)
            return AppHelper.SKU_MONTHLY_SUBSCRIBED;
        else if (type == Subscription.Type.ANNUAL)
            return AppHelper.SKU_ANNUAL_SUBSCRIBED;
        else if (type == Subscription.Type.SIXMONTH)
            return AppHelper.SKU_6MONTH_SUBSCRIBED;

        return null;
    }


    //==============================================================================================
    //== Encrypt / Decrypt
    //==============================================================================================

    public static String base64Decode(String text) {
        return null;
    }

    public static String gzipDecode(String text) {
        return null;
    }


    //==============================================================================================
    //== Validation
    //==============================================================================================

    public static Boolean isMailAddressValid(String mail) {
        return false;
    }


    //==============================================================================================
    //== Convention
    //==============================================================================================

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }


    //==============================================================================================
    //== IO
    //==============================================================================================

    public static String fileGetContents(File file) {
        int length = (int) file.length();
        byte[] bytes = new byte[length];

        try {
            FileInputStream inputStream = new FileInputStream(file);
            inputStream.read(bytes);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(bytes);
    }

    public static boolean filePutContents(File file, String content) {
        if (file.exists())
            file.delete();

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void fileCopy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    static public void extractBookZipFile(String zipFile) throws IOException {
        System.out.println(zipFile);
        int BUFFER = 2048;
        File file = new File(zipFile);

        ZipFile zip = new ZipFile(file);
        String newPath = zipFile.substring(0, zipFile.length() - 4);

        new File(newPath).mkdir();
        Enumeration zipFileEntries = zip.entries();

        while (zipFileEntries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();


            String currentEntry = entry.getName();
            File destFile = new File(newPath, currentEntry);

            if(destFile.getCanonicalPath().startsWith(newPath)){
                SecurityException securityException = new SecurityException();
                securityException.printStackTrace();
            }
            File destinationParent = destFile.getParentFile();

            destinationParent.mkdirs();

            if (!entry.isDirectory()) {
                BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
                int currentByte;
                byte data[] = new byte[BUFFER];

                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

                while ((currentByte = is.read(data, 0, BUFFER)) != -1)
                    dest.write(data, 0, currentByte);

                dest.flush();
                dest.close();
                is.close();
            }

            if (currentEntry.endsWith(".zip"))
                extractBookZipFile(destFile.getAbsolutePath());
        }
    }

    static public String getAssetTextFile(Context context, String filename) {
        String content = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open(filename)));
            String mLine;
            while ((mLine = reader.readLine()) != null)
                content += mLine;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return content;
        }
    }

    static public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    static public void deleteUncompletedDownloads() {
        try {
            File folder = getContentFolder();
            if (folder != null) {
                File files[] = folder.listFiles();
                for (File file : files) {
                    String filename = file.getName();
                    String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
                    if (extension.equals("tmp") || extension.equals("zip"))
                        file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
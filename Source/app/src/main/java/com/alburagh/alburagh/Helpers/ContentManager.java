package com.alburagh.alburagh.Helpers;

import static com.alburagh.alburagh.Helpers.AppHelper.TAG;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.alburagh.alburagh.MainActivity;
import com.alburagh.alburagh.Models.Book;
import com.alburagh.alburagh.Models.Content;
import com.alburagh.alburagh.Models.Game;
import com.alburagh.alburagh.Models.SoundBook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContentManager {
    private static ContentManager instance = null;
    private ContentManager(){}
    public static ContentManager getInstance(){
        if (instance == null)
            instance = new ContentManager();
        return instance;
    }



    //==============================================================================================
    //== Content List
    //==============================================================================================

    public void getContentList (final ContentListCallback callback) {
        String contentListString = loadLocalContentList();
        if (contentListString != null)
            callback.success(jsonToContentList(contentListString), true);
        String rootDirexists = Environment.getExternalStorageDirectory().toString();
        File contentDirexists = new File(rootDirexists + "/Android/data/"+ MainActivity.PACKAGE_NAME +"/contents/content_list.json");
        Log.d(TAG, "getContentList: "+contentDirexists.exists());
        if(!contentDirexists.exists()){
            WebService.getInstance().getContentList(false, new WebService.ContentListCallback() {
                @Override
                public void success(String json) {
                    callback.success(jsonToContentList(json), false);
                    saveLocalContentList(json);
                    AppHelper.gotContentListFromServer = true;
                }

                @Override
                public void failed(Throwable error) {
                    callback.failed(error);
                }
            });
        }else if (!AppHelper.gotContentListFromServer) {
            WebService.getInstance().getContentList(false, new WebService.ContentListCallback() {
                @Override
                public void success(String json) {
                    callback.success(jsonToContentList(json), false);
                    saveLocalContentList(json);
                    AppHelper.gotContentListFromServer = true;
                }

                @Override
                public void failed(Throwable error) {
                    Log.d(TAG, "failed: sssssss");
                    callback.failed(error);
                }
            });
        }
    }

    public interface ContentListCallback {
        void success(ContentList contentList, boolean cached);
        void failed(Throwable error);
    }

    public void saveLocalContentList(String content) {
        File contentFile = new File(AppHelper.getContentFolder(), AppHelper.CONTENT_LIST_FILENAME);
        AppHelper.filePutContents(contentFile, content);
    }

    public String loadLocalContentList() {
        File contentListFile = new File(AppHelper.getContentFolder(), AppHelper.CONTENT_LIST_FILENAME);
        if (contentListFile.exists()) {
            return AppHelper.fileGetContents(contentListFile);
        } else
            return null;
    }

    public List getDownloadedContentList () {
        return null;
    }

    public List getFavoratedContentList () {
        return null;
    }

    public ContentList jsonToContentList(String json) {
        ContentList contentList = new ContentList();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray books = jsonObject.getJSONArray("books");
            JSONArray games = jsonObject.getJSONArray("games");
            JSONArray soundBooks = jsonObject.getJSONArray("sound_books");
            for (int i = 0; i < books.length(); i++) {
                contentList.books.add(jsonToBook(books.getJSONObject(i)));
            }
            for (int i = 0; i < games.length(); i++) {
                contentList.games.add(jsonToGame(games.getJSONObject(i)));
            }
            for (int i = 0; i < soundBooks.length(); i++) {
                Log.d(TAG, "jsonToContentList: "+soundBooks.length());
                contentList.soundBooks.add(jsonToSoundBook(soundBooks.getJSONObject(i)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return contentList;
    }

    public class ContentList {
        public List<Book> books;
        public List<Game> games;
        public List<SoundBook> soundBooks;

        public ContentList () {
            books = new ArrayList<>();
            games = new ArrayList<>();
            soundBooks = new ArrayList<>();
        }
    }



    //==============================================================================================
    //== Favorite
    //==============================================================================================

    public List<String> getFavoriteContents() {
        Set<String> favoriteContents = Setting.getInstance().preferences.getStringSet("favorite_contents", null);
        List<String> favoriteContentsList = (favoriteContents != null) ? new ArrayList<>(favoriteContents) : null;
        if (favoriteContentsList == null)
            favoriteContentsList = new ArrayList<>();

        return favoriteContentsList;
    }

    public boolean isContentFavorite(Content content) {
        List<String> favoriteContents = getFavoriteContents();
        return favoriteContents.contains(content.folderName);
    }

    public void favoriteContent(Content content) {
        List<String> favoriteContents = getFavoriteContents();

        if (!favoriteContents.contains(content.folderName))
            favoriteContents.add(content.folderName);

        Set<String> favoriteContentSet = new HashSet<>(favoriteContents);
        Setting.getInstance().editor
                .putStringSet("favorite_contents", favoriteContentSet)
                .commit();
    }

    public void unFavoriteContent(Content content) {
        List<String> favoriteContents = getFavoriteContents();

        if (favoriteContents.contains(content.folderName))
            favoriteContents.remove(content.folderName);

        Set<String> favoriteContentSet = new HashSet<>(favoriteContents);
        Setting.getInstance().editor
                .putStringSet("favorite_contents", favoriteContentSet)
                .commit();
    }



    //==============================================================================================
    //== Downloads
    //==============================================================================================

    public List<Content> downloadingContents = new ArrayList<>();

    public void addDownloadingContent(Content content) {
        for (int i = 0; i < downloadingContents.size(); i++) {
            if (downloadingContents.get(i).folderName.equals(content.folderName))
                return;
        }

        downloadingContents.add(content);
    }

    public void removeDownloadingContent(Content content) {
        for (int i = 0; i < downloadingContents.size(); i++) {
            if (downloadingContents.get(i).folderName.equals(content.folderName))
                downloadingContents.remove(i);
        }
    }

    public boolean isContentDownloading(Content content) {
        for (int i = 0; i < downloadingContents.size(); i++) {
            if (downloadingContents.get(i).folderName.equals(content.folderName))
                return true;
        }

        return false;
    }

    public boolean isContentDownloaded(Content content) {
        File file = new File(AppHelper.getContentFolder(), String.format("%s/content.json", content.folderName));
        return file.exists();
    }


    DeleteCallback deleteCallback;
    public void deleteDownloadedContent(Content content, DeleteCallback callback) {
        File file = new File(AppHelper.getContentFolder(), content.folderName);
        if (file.exists() && file.isDirectory()) {
            deleteCallback = callback;
            new AsyncDeleteContent().execute(content);
        }
    }

    private class AsyncDeleteContent extends AsyncTask<Content, Void, Content> {
        @Override
        protected Content doInBackground(Content... params) {
            Content content = params[0];
            try {
                File file = new File(AppHelper.getContentFolder(), content.folderName);
                AppHelper.deleteRecursive(file);
            } catch (Exception e) {
                deleteCallback.failed();
                deleteCallback = null;
                e.printStackTrace();
            }

            return content;
        }

        @Override
        protected void onPostExecute(Content content) {
            deleteCallback.success();
            deleteCallback = null;
        }
    }

    public interface DeleteCallback {
        void success();
        void failed();
    }



    //==============================================================================================
    //== Misc
    //==============================================================================================

    // Todo: find a way to make one method to replace jsonToBook and jsonToGame

    public Book jsonToBook (JSONObject json) throws JSONException {
        Book book = new Book();

        book.contentID = json.getInt("id");
        book.title = json.getString("title");
        book.desk = json.getString("description");
        book.lang = json.getString("lang");
        book.filename = json.getString("file");
        book.folderName = json.getString("folder");
        book.timestamp = json.getInt("date");
        book.date = new Date(book.timestamp * 1000L);
        book.contentVersion = json.getInt("version");
        book.order = json.getInt("order");
        book.readCount = json.getInt("readings");

        JSONArray samples = json.getJSONArray("samples");
        book.samples = new ArrayList<>();
        for (int i = 0; i < samples.length(); i++)
            book.samples.add(samples.getString(i));

        book.image = json.getString("cover");
        book.rate = (float) json.getDouble("rate");
        book.author = json.getString("author");
        book.publisher = json.getString("publisher");
        book.illistrator = json.getString("illistrator");
        book.ageGroup = json.getString("age_group");
        book.fileSize = json.getString("file_size");

        book.series = json.getString("series");
        if (book.series.equals("null"))
            book.series = "";

        if (isContentDownloaded(book))
            book.downloadStatus = Content.DownloadStatus.DOWNLOADED;
        else if (isContentDownloading(book))
            book.downloadStatus = Content.DownloadStatus.DOWNLOADING;
        else
            book.downloadStatus = Content.DownloadStatus.NOT_DOWNLOADED;

        return book;
    }

    public SoundBook jsonToSoundBook (JSONObject json) throws JSONException {
        SoundBook soundBook = new SoundBook();

        soundBook.contentID = json.getInt("id");
        soundBook.title = json.getString("title");
        soundBook.desk = json.getString("description");
        soundBook.lang = json.getString("lang");
        soundBook.filename = json.getString("file");
        soundBook.folderName = json.getString("folder");
        soundBook.timestamp = json.getInt("date");
        soundBook.date = new Date(soundBook.timestamp * 1000L);
        soundBook.contentVersion = json.getInt("version");
        soundBook.order = json.getInt("order");
        soundBook.readCount = json.isNull("readings") ? 0 : json.getInt("readings");

//        JSONArray samples = json.getJSONArray("samples");
//        soundBook.samples = new ArrayList<>();
//        for (int i = 0; i < samples.length(); i++)
//            soundBook.samples.add(samples.getString(i));

        soundBook.image = json.getString("image");

        if (isContentDownloaded(soundBook))
            soundBook.downloadStatus = Content.DownloadStatus.DOWNLOADED;
        else if (isContentDownloading(soundBook))
            soundBook.downloadStatus = Content.DownloadStatus.DOWNLOADING;
        else
            soundBook.downloadStatus = Content.DownloadStatus.NOT_DOWNLOADED;

        return soundBook;
    }
    public Game jsonToGame (JSONObject json) throws JSONException {
        Game game = new Game();

        game.contentID = json.getInt("id");
        game.title = json.getString("title");
        game.desk = json.getString("description");
        game.lang = json.getString("lang");
        game.filename = json.getString("file");
        game.folderName = json.getString("folder");
        game.timestamp = json.getInt("date");
        game.date = new Date(game.timestamp * 1000L);
        game.contentVersion = json.getInt("version");
        game.order = json.getInt("order");
        game.readCount = json.isNull("readings") ? 0 : json.getInt("readings");

        JSONArray samples = json.getJSONArray("samples");
        game.samples = new ArrayList<>();
        for (int i = 0; i < samples.length(); i++)
            game.samples.add(samples.getString(i));

        game.image = json.getString("image");

        if (isContentDownloaded(game))
            game.downloadStatus = Content.DownloadStatus.DOWNLOADED;
        else if (isContentDownloading(game))
            game.downloadStatus = Content.DownloadStatus.DOWNLOADING;
        else
            game.downloadStatus = Content.DownloadStatus.NOT_DOWNLOADED;

        return game;
    }

    public void saveContentVersion (Content content) {
        File contentFolder = new File(AppHelper.getContentFolder(), content.folderName);
        File versionFile = new File(contentFolder, "ver.txt");

        if (versionFile.exists())
            versionFile.delete();

        AppHelper.filePutContents(versionFile, Integer.toString(content.contentVersion));
    }

    public int getSavedContentVersion (Content content) {
        File contentFolder = new File(AppHelper.getContentFolder(), content.folderName);
        File versionFile = new File(contentFolder, "ver.txt");
        if (versionFile.exists()) {
            String version = AppHelper.fileGetContents(versionFile);
            if (version.matches("-?\\d+(\\.\\d+)?"))
                return Integer.parseInt(version);
        }

        return 1;
    }

    public void updateContent(Content content, Context context) {
        content.deleteLastVersionBeforeUnzip = true;
        DownloadManager.getInstance().download(content, context);
        DownloadManager.getInstance().requestQueue.start();
    }

}
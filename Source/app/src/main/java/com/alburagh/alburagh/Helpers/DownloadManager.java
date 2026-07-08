package com.alburagh.alburagh.Helpers;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.alburagh.alburagh.Models.Content;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Network;
import com.duowan.mobile.netroid.RequestQueue;
import com.duowan.mobile.netroid.cache.DiskCache;
import com.duowan.mobile.netroid.stack.HurlStack;
import com.duowan.mobile.netroid.toolbox.BasicNetwork;
import com.duowan.mobile.netroid.toolbox.FileDownloader;

import java.io.File;
import java.io.IOException;

import cz.msebera.android.httpclient.protocol.HTTP;

public class DownloadManager {
    private static DownloadManager instance = null;
    private DownloadManager () {
        Network network = new BasicNetwork(new HurlStack(HTTP.USER_AGENT), HTTP.UTF_8);
        requestQueue = new RequestQueue(network, 4, new DiskCache(Environment.getDownloadCacheDirectory()));
        fileDownloader = new FileDownloader(requestQueue, 3);
    }
    public static DownloadManager getInstance () {
        if(instance == null)
            instance = new DownloadManager();
        return instance;
    }

    public RequestQueue requestQueue;
    private FileDownloader fileDownloader;
    private DownloadListener listener;

    public void setListener(DownloadListener listener) {
        this.listener = listener;
    }

    public void unsetListener() {
        this.listener = null;
    }

    public void download(final Content content, Context context) {

        final String storePath = String.format("%s/%s", AppHelper.getContentFolder().getAbsoluteFile(),
                content.filename.substring(content.filename.lastIndexOf('/') + 1));

        if (listener != null)
            listener.added(content);

        fileDownloader.add(storePath, content.filename, new Listener<Void>() {
            @Override
            public void onPreExecute() {
                if (listener != null)
                    listener.started(content);

                ContentManager.getInstance().addDownloadingContent(content);
            }

            @Override
            public void onSuccess(Void response) {
                content.storePath = storePath;

                if (content.deleteLastVersionBeforeUnzip) {
                    ContentManager.getInstance().deleteDownloadedContent(content, new ContentManager.DeleteCallback() {
                        @Override
                        public void success() {
                            Log.d(TAG, "success: "+content.title);
                            new ContentUnzip().execute(content);
                            content.deleteLastVersionBeforeUnzip = false;
                        }

                        @Override
                        public void failed() {
                            content.deleteLastVersionBeforeUnzip = false;
                        }
                    });
                } else {
                    new ContentUnzip().execute(content);
                    Log.d(TAG, "success: sss "+content.title);
                }
            }

            @Override
            public void onProgressChange(long fileSize, long downloadedSize) {
                if (listener != null)
                    listener.progressed(content, (float) downloadedSize / (float) fileSize);
            }

            @Override
            public void onError(NetroidError error) {
                if (listener != null)
                    listener.error(content, error);
            }

            @Override
            public void onFinish() {
                if (listener != null)
                    listener.finished(content);

                ContentManager.getInstance().removeDownloadingContent(content);
            }

            @Override
            public void onRetry() {
                super.onRetry();
            }
        });
    }

    private class ContentUnzip extends AsyncTask<Content, Void, Content> {

        @Override
        protected Content doInBackground(Content... params) {
            try {
                AppHelper.extractBookZipFile(params[0].storePath);
                File zipFile = new File(params[0].storePath);
                if (zipFile.exists())
                    zipFile.delete();

            } catch (IOException e) {
                listener.error(params[0], null);
                e.printStackTrace();
            }

            return params[0];
        }

        @Override
        protected void onPostExecute(Content content) {
            if (listener != null)
                listener.success(content);
        }
    }

    public interface DownloadListener {
        void added(Content content);
        void started(Content content);
        void progressed(Content content, float percent);
        void error(Content content, NetroidError error);
        void success(Content content);
        void finished(Content content);
    }
}

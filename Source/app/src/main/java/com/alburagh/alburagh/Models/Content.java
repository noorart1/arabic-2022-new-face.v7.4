package com.alburagh.alburagh.Models;

import java.util.Date;
import java.util.List;

public class Content {

    public enum DownloadStatus {
        NOT_DOWNLOADED,
        ADDED_TO_QUEUE,
        DOWNLOADING,
        DOWNLOADED
    }

    public int contentID;
    public String title;
    public String desk;
    public String lang;
    public float rate;
    public String image;
    public List<String> samples;
    public String filename;
    public String fileSize = "14.46 MB";
    public String folderName;
    public int timestamp;
    public int contentVersion;
    public int order;
    public int readCount;

    public Date date;

    public String storePath;
    public Content.DownloadStatus downloadStatus;

    public boolean isBook;
    public boolean isSoundBook;
    public boolean isQuran;
    public boolean deleteLastVersionBeforeUnzip;
}
package com.alburagh.alburagh.Models;

import java.util.List;

public class BookIndex {
    public List<IndexItem> index;
    public static class IndexItem {
        public String thumb;
        public String html;
    }
}
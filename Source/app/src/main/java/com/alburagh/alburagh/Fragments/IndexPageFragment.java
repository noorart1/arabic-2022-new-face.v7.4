package com.alburagh.alburagh.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alburagh.alburagh.Models.BookIndex;
import com.alburagh.alburagh.R;
import com.google.gson.Gson;

import java.io.File;

public class IndexPageFragment extends Fragment {
    public IndexPageFragment() {}

    BookIndex index;
    int start = 0;
    String path;
    IndexCallback callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index_page, container, false);

        Gson gson = new Gson();
        index = gson.fromJson(getArguments().getString("bookIndex"), BookIndex.class);
        start = getArguments().getInt("start");
        path = getArguments().getString("path");

        int[] ids = {R.id.thumb_01, R.id.thumb_02, R.id.thumb_03, R.id.thumb_04, R.id.thumb_05, R.id.thumb_06};
        int[] num = {R.id.num_01, R.id.num_02, R.id.num_03, R.id.num_04, R.id.num_05, R.id.num_06};

        for (int i = start; i < start + 6; i++) {
            if (i < index.index.size() && i < start + 6) {
                Bitmap thumb = BitmapFactory.decodeFile(new File(path, index.index.get(i).thumb).getAbsolutePath());
                ((ImageView) view.findViewById(ids[i - start])).setImageBitmap(thumb);
                view.findViewById(ids[i - start]).setVisibility(View.VISIBLE);

                final int index = i;
                view.findViewById(ids[i - start]).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null)
                            callback.indexClicked(index);
                    }
                });

                ((View)view.findViewById(num[i - start]).getParent()).setVisibility(View.VISIBLE);
                ((TextView)view.findViewById(num[i - start])).setText(String.valueOf(i + 1));
            } else {
                view.findViewById(ids[i - start]).setVisibility(View.INVISIBLE);
                ((View)view.findViewById(num[i - start]).getParent()).setVisibility(View.INVISIBLE);
            }
        }

        view.findViewById(R.id.main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null)
                    callback.close();
            }
        });

        return view;
    }

    public void setShowPageCallback(IndexCallback callback) {
        this.callback = callback;
    }

    public interface IndexCallback {
        void indexClicked(int index);
        void close();
    }
}
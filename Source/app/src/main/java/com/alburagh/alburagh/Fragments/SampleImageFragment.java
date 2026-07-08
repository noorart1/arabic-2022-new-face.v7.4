package com.alburagh.alburagh.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alburagh.alburagh.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class SampleImageFragment extends Fragment {

    public SampleImageFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sample_image, container, false);

        final String url = getArguments().getString("url");
        final ImageView sample = (ImageView) view.findViewById(R.id.sample);

        Picasso.with(getActivity())
                .load(url)
                .placeholder(R.drawable.sample_placeholder)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(sample, new Callback() {
                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onError() {
                        Picasso.with(getActivity())
                                .load(url)
                                .placeholder(R.drawable.sample_placeholder)
                                .into(sample);
                    }
                });

        return view;
    }
}
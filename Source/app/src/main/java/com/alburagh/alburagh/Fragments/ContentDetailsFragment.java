package com.alburagh.alburagh.Fragments;

import android.graphics.Point;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alburagh.alburagh.Helpers.ContentManager;
import com.alburagh.alburagh.Helpers.DownloadManager;
import com.alburagh.alburagh.Helpers.UIHelper;
import com.alburagh.alburagh.MainActivity;
import com.alburagh.alburagh.Models.Book;
import com.alburagh.alburagh.R;
import com.androidpagecontrol.PageControl;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ContentDetailsFragment extends Fragment {

    public ContentDetailsFragment() {}

    Book book;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content_details, container, false);
        final ImageView background = view.findViewById(R.id.background);
        final RelativeLayout detailsLayout = view.findViewById(R.id.details_layout);
        final RelativeLayout coverLayout = view.findViewById(R.id.cover_layout);
        final ImageButton downloadReadButton = view.findViewById(R.id.download_read_button);
        final ImageButton samplesButton = view.findViewById(R.id.samples_button);
        final ViewPager samplesPager = view.findViewById(R.id.samples_pager);
        final PageControl pageControl = view.findViewById(R.id.page_control);
        final ImageButton returnButton = view.findViewById(R.id.back_button);

        book = new Gson().fromJson(getArguments().getString("book"), Book.class);

        RelativeLayout mainLayout = view.findViewById(R.id.main_layout);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Details
        TextView series = view.findViewById(R.id.series);
        TextView title = view.findViewById(R.id.title);
        TextView publisher = view.findViewById(R.id.publisher);
        TextView illustrator = view.findViewById(R.id.illustrator);
        TextView ageGroup = view.findViewById(R.id.age_group);
        TextView fileSize = view.findViewById(R.id.file_size);
        TextView description = view.findViewById(R.id.description);
        TextView readCount = view.findViewById(R.id.read_count);
        TextView author = view.findViewById(R.id.author);

        series.setText(book.series);
        author.setText(book.author);
        title.setText(book.title);
        publisher.setText(book.publisher);
        illustrator.setText(book.illistrator);
        ageGroup.setText(book.ageGroup);
        fileSize.setText(book.fileSize);
        description.setText(book.desk);
        readCount.setText(String.valueOf(book.readCount));

        // Cover
        final ImageView cover = view.findViewById(R.id.cover);
        try {

            Picasso.with(getActivity())
                    .load(book.image)
                    .placeholder(R.id.books_place_holder)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(cover, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Picasso.with(getActivity())
                                    .load(book.image)
                                    .placeholder(R.id.books_place_holder)
                                    .into(cover);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Buttons
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                background.setImageResource(R.drawable.details_front);
                detailsLayout.setVisibility(View.VISIBLE);
                coverLayout.setVisibility(View.VISIBLE);
                downloadReadButton.setVisibility(View.VISIBLE);
                samplesButton.setVisibility(View.VISIBLE);
                samplesPager.setVisibility(View.GONE);
                returnButton.setVisibility(View.GONE);
                pageControl.setVisibility(View.GONE);

                UIHelper.playButtonSound(getActivity());
            }
        });

        samplesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                background.setImageResource(R.drawable.details_back);
                detailsLayout.setVisibility(View.GONE);
                coverLayout.setVisibility(View.GONE);
                downloadReadButton.setVisibility(View.GONE);
                samplesButton.setVisibility(View.GONE);
                samplesPager.setVisibility(View.VISIBLE);
                returnButton.setVisibility(View.VISIBLE);
                pageControl.setVisibility(View.VISIBLE);

                UIHelper.playButtonSound(getActivity());
            }
        });

        if (ContentManager.getInstance().isContentDownloaded(book))
            downloadReadButton.setImageResource(R.drawable.read_button);
        else
            downloadReadButton.setImageResource(R.drawable.download_button);

        downloadReadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();

                if (ContentManager.getInstance().isContentDownloaded(book)) {
                    mainActivity.getSupportFragmentManager().popBackStack();
                    book.isBook = true;
                    mainActivity.showBook(book);
                } else {
                    DownloadManager.getInstance().download(book, getActivity());
                    DownloadManager.getInstance().requestQueue.start();
                    mainActivity.getSupportFragmentManager().popBackStack();
                }

                UIHelper.playButtonSound(getActivity());
            }
        });

        // Rating
        LinearLayout ratingLayout = view.findViewById(R.id.rating_layout);
        ImageView ratingImage = ratingLayout.findViewById(R.id.rating_star_sample);

        for (int i = 0; i < 5; i++) {
            ImageView ratingStar = new ImageView(getActivity());
            ratingStar.setLayoutParams(ratingImage.getLayoutParams());
            ratingStar.setVisibility(View.VISIBLE);

            if (Math.round(book.rate) > i)
                ratingStar.setImageResource(R.drawable.rate_active);
            else
                ratingStar.setImageResource(R.drawable.rate_inactive);

            ratingLayout.addView(ratingStar);
        }

        // Samples
        SamplesPagerAdaptor samplesPagerAdaptor = new SamplesPagerAdaptor(
                getActivity().getSupportFragmentManager(), book.samples);
        samplesPager.setAdapter(samplesPagerAdaptor);
        pageControl.setViewPager(samplesPager);

        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                ViewTreeObserver observer = view.getViewTreeObserver();
                observer.removeOnGlobalLayoutListener(this);

                View detailsBG = view.findViewById(R.id.background);

                TypedValue outValue = new TypedValue();
                getResources().getValue(R.dimen.button_scale, outValue, true);
                float buttonScale = outValue.getFloat();

                UIHelper.ScreenOptions so = new UIHelper.ScreenOptions(getActivity(), view);
                UIHelper.fitSize(so, detailsBG, R.drawable.details_front);

                UIHelper.fitLayoutToBackground(so, view.findViewById(R.id.details_layout), 760, 695, 724, 171);
                UIHelper.fitLayoutToBackground(so, view.findViewById(R.id.cover_layout), 292, 570, 405, 280);
                UIHelper.fitLayoutToBackground(so, view.findViewById(R.id.page_control), 222, 30, 815, 940);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.download_read_button), 220, 55, new Point(700, 942), 1.0f);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.samples_button), 220, 55, new Point(1200, 942), 1.0f);
                UIHelper.fitLayoutToBackground(so, view.findViewById(R.id.samples_pager), 846, 634, 537, 268);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.back_button), 180, 50, new Point(1200, 960), 1.0f);

                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.back_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.download_read_button));
                UIHelper.setupButtonEffects((ImageButton) view.findViewById(R.id.samples_button));
            }
        });
    }

    public class SamplesPagerAdaptor extends FragmentStatePagerAdapter {
        List<String> urls;

        SamplesPagerAdaptor(FragmentManager fm, List<String> urls) {
            super(fm);
            this.urls = urls;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putString("url", urls.get(position));

            SampleImageFragment fragment = new SampleImageFragment();
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public int getCount() {
            return urls.size();
        }
    }
}
package com.alburagh.alburagh.Fragments;

import static com.alburagh.alburagh.Helpers.AppHelper.TAG;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alburagh.alburagh.Helpers.AppHelper;
import com.alburagh.alburagh.Helpers.ContentManager;
import com.alburagh.alburagh.Helpers.DownloadManager;
import com.alburagh.alburagh.Helpers.UIHelper;
import com.alburagh.alburagh.Helpers.UserManager;
import com.alburagh.alburagh.Helpers.WebService;
import com.alburagh.alburagh.MainActivity;
import com.alburagh.alburagh.Models.Book;
import com.alburagh.alburagh.Models.Content;
import com.alburagh.alburagh.Models.Game;
import com.alburagh.alburagh.Models.SoundBook;
import com.alburagh.alburagh.R;
import com.duowan.mobile.netroid.NetroidError;
import com.filippudak.ProgressPieView.ProgressPieView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ContentsFragment extends Fragment {

    public ContentsFragment() { }

    public enum ContentType {
        SOUND_BOOKS,
        BOOKS,
        GAMES
    }

    private ContentType type;
    private boolean favorite;

    public DownloadManager.DownloadListener listener;

    RecyclerView recyclerView;

    boolean cachedContentListReceived = true;

    int viewWidth = 0;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contents, container, false);

        type = (ContentType) getArguments().getSerializable("type");
        favorite = getArguments().getBoolean("favorite", false);

        recyclerView = view.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(),
                getResources().getInteger(R.integer.content_grid_columns),
                GridLayoutManager.VERTICAL, false);
        ((RecyclerView) view.findViewById(R.id.recycler_view)).setLayoutManager(layoutManager);

        listener = new DownloadManager.DownloadListener() {
            @Override
            public void added(Content content) {
                ContentListAdaptor adaptor = (ContentListAdaptor) recyclerView.getAdapter();
                Integer position = adaptor.setContentDownloadStatus(content.contentID,
                        Content.DownloadStatus.ADDED_TO_QUEUE);
                if (position != null)
                    recyclerView.getAdapter().notifyItemChanged(position);
            }

            @Override
            public void started(Content content) {
                ContentListAdaptor adaptor = (ContentListAdaptor) recyclerView.getAdapter();
                Integer position = adaptor.setContentDownloadStatus(content.contentID,
                        Content.DownloadStatus.DOWNLOADING);
                if (position != null)
                    recyclerView.getAdapter().notifyItemChanged(position);
            }

            @Override
            public void progressed(Content content, float percent) {
                try {
                    View view = getViewByContent(content);
                    if (view != null) {
                        ProgressPieView downloadProgress = view.findViewById(R.id.download_progress);
                        downloadProgress.setText(String.format(Locale.US, "%.0f%%", percent * 100));
                        downloadProgress.setProgress(Integer.parseInt(
                                String.format(Locale.US, "%.0f", percent * 100)));
                    }
                } catch (Exception e) {
                    Log.d("ALBURAGH", e.toString());
                }
            }

            @Override
            public void success(Content content) {
                ContentListAdaptor adaptor = (ContentListAdaptor) recyclerView.getAdapter();
                Integer position = adaptor.setContentDownloadStatus(content.contentID, Content.DownloadStatus.DOWNLOADED);
                if (position != null)
                    recyclerView.getAdapter().notifyItemChanged(position);

                ContentManager.getInstance().saveContentVersion(content);
                WebService.getInstance().increaseDownloads(content.contentID, new WebService.IncreaseDownloadCountCallback() {
                    @Override
                    public void success() { }

                    @Override
                    public void failed() { }
                });

                ContentManager.getInstance().saveContentVersion(content);
            }

            @Override
            public void error(final Content content, NetroidError error) {
                ContentListAdaptor adaptor = (ContentListAdaptor) recyclerView.getAdapter();
                Integer position = adaptor.setContentDownloadStatus(content.contentID, Content.DownloadStatus.NOT_DOWNLOADED);
                if (position != null)
                    recyclerView.getAdapter().notifyItemChanged(position);

                new AlertDialog.Builder(getActivity())
                        .setTitle(getResources().getString(R.string.content_download_error_title))
                        .setMessage(String.format(getResources().getString(R.string.content_download_error_message), content.title))
                        .setPositiveButton(getResources().getString(R.string.content_download_again), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DownloadManager.getInstance().download(content, getActivity());
                                DownloadManager.getInstance().requestQueue.start();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), null)
                        .show();
            }

            @Override
            public void finished(Content content) {
                Log.d("ALBURAGH", "Download Finished");
            }
        };

        DownloadManager.getInstance().setListener(listener);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView = null;
        DownloadManager.getInstance().unsetListener();
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                ViewTreeObserver observer = view.getViewTreeObserver();
                observer.removeOnGlobalLayoutListener(this);

                UIHelper.ScreenOptions so = new UIHelper.ScreenOptions(getActivity(), view);
                UIHelper.fitViewToBackground(so, view.findViewById(R.id.favorite_desc),
                        550, 150, new Point(0, 0), 1.0f);

                viewWidth = view.findViewById(R.id.main).getWidth();
                generateUI(view);
            }
        });
    }

    public void generateUI(final View layout) {
        final UIHelper.ScreenOptions so = new UIHelper.ScreenOptions(getActivity(), layout);

        ContentManager.getInstance().getContentList(new ContentManager.ContentListCallback() {
            @Override
            public void success(ContentManager.ContentList contentList, boolean cached) {
                if (cached)
                    cachedContentListReceived = false;
                List<Book> books;
                List<Game> games;
                List<SoundBook> soundBooks;

                if (favorite) {
                    List<String> favoriteContents = ContentManager.getInstance().getFavoriteContents();

                    books = new ArrayList<>();
                    for (Book book : contentList.books) {
                        if (favoriteContents.contains(book.folderName)) {
                            books.add(book);
                        }
                    }

                    games = new ArrayList<>();
                    for (Game game : contentList.games) {
                        if (favoriteContents.contains(game.folderName)) {
                            games.add(game);
                        }
                    }

                    soundBooks = new ArrayList<>();
                    for (SoundBook soundBook : contentList.soundBooks) {
                        if (favoriteContents.contains(soundBook.folderName)) {
                            soundBooks.add(soundBook);
                        }
                    }
                } else {
                    books = contentList.books;
                    games = contentList.games;
                    soundBooks = contentList.soundBooks;
                }

                boolean descVisible;

                switch (type) {
                    case BOOKS:
                        Log.i(TAG, "content load success: "+ books.size());
                        ((RecyclerView) layout.findViewById(R.id.recycler_view)).setAdapter(new ContentListAdaptor(books, so));
                        descVisible = (favorite && books.size() == 0);
                        layout.findViewById(R.id.favorite_desc).setVisibility(descVisible ? View.VISIBLE : View.GONE);
                        break;

                    case GAMES:
                        Log.i(TAG, "games load success: "+ books.size());
                        ((RecyclerView) layout.findViewById(R.id.recycler_view)).setAdapter(new ContentListAdaptor(games, so));
                        descVisible = (favorite && games.size() == 0);
                        layout.findViewById(R.id.favorite_desc).setVisibility(descVisible ? View.VISIBLE : View.GONE);
                        break;

                    case SOUND_BOOKS:
                        Log.i(TAG, "SOUND_BOOKS load success: "+ soundBooks.size());
                        ((RecyclerView) layout.findViewById(R.id.recycler_view)).setAdapter(new ContentListAdaptor(soundBooks, so));
                        descVisible = (favorite && soundBooks.size() == 0);
                        layout.findViewById(R.id.favorite_desc).setVisibility(descVisible ? View.VISIBLE : View.GONE);
                        break;

                    default:
                        break;
                }

                layout.findViewById(R.id.main_loading).setVisibility(View.GONE);
            }

            @Override
            public void failed(Throwable error) {
                Log.e(TAG, "failedfailedfailed: "+cachedContentListReceived, error);
                if (!cachedContentListReceived) {
                    try {
                        new AlertDialog.Builder(getActivity())
                                .setMessage(getResources().getString(R.string.content_list_download_error_message))
                                .setPositiveButton(getResources().getString(R.string.content_list_try_again), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        generateUI(layout);
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.cancel), null)
                                .show();
                    } catch (Exception e) {
                        Log.e(TAG, "failed: ",e );
                    }
                }
            }
        });
    }

    public View getViewByContent(Content content) {
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        ContentListAdaptor adaptor = (ContentListAdaptor) recyclerView.getAdapter();

        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        int lastPosition = layoutManager.findLastVisibleItemPosition();

        for (int i = firstPosition; i <= lastPosition; i++) {
            if (i <= -1)
                continue;

            if (adaptor.contents.get(i).folderName.equals(content.folderName))
                return recyclerView.getChildAt(i - firstPosition);
        }

        return null;
    }

    public void contentClicked(final Content content, boolean versionCheck) {
        if (ContentManager.getInstance().isContentDownloaded(content) && versionCheck) {
            int version = ContentManager.getInstance().getSavedContentVersion(content);
            if (content.contentVersion > version) {
                String message;
                if (content instanceof Book)
                    message = getResources().getString(R.string.book_update_message);
                else
                    message = getResources().getString(R.string.game_update_message);

                new AlertDialog.Builder(getActivity())
                        .setMessage(message)
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ContentManager.getInstance().updateContent(content, getActivity());
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                contentClicked(content, false);
                            }
                        })
                        .show();

                return;
            }
        }
        if (content instanceof Book) {
            final Book book = (Book) content;
            if (ContentManager.getInstance().isContentDownloaded(book)) {
                book.isBook = true;
                ((MainActivity) getActivity()).showBook(book);
            } else {
                ((MainActivity) getActivity()).showDetails(book);
            }
        } else if (content instanceof Game) {
            if (ContentManager.getInstance().isContentDownloaded(content)) {
                ((MainActivity) getActivity()).playGame(content);
            }
            else {
                DownloadManager.getInstance().download(content, getActivity());
                DownloadManager.getInstance().requestQueue.start();
            }
        } else if (content instanceof SoundBook) {
            if (ContentManager.getInstance().isContentDownloaded(content)) {
                content.isSoundBook = true;
                ((MainActivity) getActivity()).showSoundBook(content);
            }
            else {
                DownloadManager.getInstance().download(content, getActivity());
                DownloadManager.getInstance().requestQueue.start();
            }
        }

        AppHelper.playSound(getContext(), R.raw.book, 1.0f);
    }

    public void toggleFavorite(ImageButton button) {
        Content content = (Content) button.getTag();

        if (ContentManager.getInstance().isContentFavorite(content)) {
            ContentManager.getInstance().unFavoriteContent(content);
            button.setImageResource(R.drawable.favorite_icon_inactive);
        } else {
            ContentManager.getInstance().favoriteContent(content);
            button.setImageResource(R.drawable.favorite_icon);
        }

        UIHelper.playButtonSound(getActivity());
    }

    public void deleteContent(final Content content) {
        new AlertDialog.Builder(getActivity())
                .setMessage(String.format(getResources().getString(R.string.content_delete_message), content.title))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "",
                                getResources().getString(R.string.delete_progress_message), true, false);
                        ContentManager.getInstance().deleteDownloadedContent(content, new ContentManager.DeleteCallback() {
                            @Override
                            public void success() {
                                progressDialog.dismiss();
                            }

                            @Override
                            public void failed() {
                                progressDialog.dismiss();
                            }
                        });
                        View contentView = getViewByContent(content);
                        contentView.findViewById(R.id.downloaded_icon).setVisibility(View.INVISIBLE);
                        contentView.findViewById(R.id.delete_button).setVisibility(View.INVISIBLE);

                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

        UIHelper.playButtonSound(getActivity());
    }

    public class ContentListAdaptor extends RecyclerView.Adapter<ContentListAdaptor.ContentViewHolder> {
        List<? extends Content> contents;
        UIHelper.ScreenOptions screenOptions;

        ContentListAdaptor(List<? extends Content> contents, UIHelper.ScreenOptions screenOptions) {
            this.contents = contents;
            this.screenOptions = screenOptions;
        }

        Integer setContentDownloadStatus(int contentID, Content.DownloadStatus status) {
            for (int i = 0; i < contents.size(); i++) {
                if (contents.get(i).contentID == contentID) {
                    contents.get(i).downloadStatus = status;
                    return i;
                }
            }
            return null;
        }

        @Override
        public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_item_layout, parent, false);
            return new ContentViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return contents.size();
        }

        @Override
        public void onBindViewHolder(final ContentViewHolder holder, int i) {
            int columns = getResources().getInteger(R.integer.content_grid_columns);

            holder.title.setText(contents.get(i).title);
            holder.image.setTag(contents.get(i));
            holder.deleteButton.setTag(contents.get(i));
            holder.favoriteButton.setTag(contents.get(i));
            holder.favoriteButton.setImageResource(ContentManager.getInstance().isContentFavorite(
                    contents.get(i)) ? R.drawable.favorite_icon : R.drawable.favorite_icon_inactive);

            final int position = holder.getAdapterPosition();
            Picasso.with(getActivity())
                    .load(contents.get(position).image)
                    .placeholder(R.drawable.book_placeholder)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.image, new Callback() {
                        @Override
                        public void onSuccess() { }

                        @Override
                        public void onError() {
                            Picasso.with(getActivity())
                                    .load(contents.get(position).image)
                                    .placeholder(R.drawable.book_placeholder)
                                    .into(holder.image);
                        }
                    });

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contentClicked((Content) v.getTag(), true);
                }
            });

            holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleFavorite((ImageButton) view);
                }
            });

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteContent((Content) v.getTag());
                }
            });

            if (UserManager.getInstance().isLoggedIn) {
                if (AppHelper.FREE_FOR_REGISTERED.contains(contents.get(i).folderName)) {
                    holder.freeIcon.setVisibility(View.VISIBLE);
                    holder.title.bringToFront();

                    UIHelper.ScreenOptions so = new UIHelper.ScreenOptions(getActivity(), view);
                    UIHelper.fitSize(so, holder.freeIcon, (70 * 3) / columns, (70 * 3) / columns);

                    holder.freeIcon.animate().translationX(-20).setDuration(0).start();
                } else
                    holder.freeIcon.setVisibility(View.GONE);
            }

            UIHelper.setupButtonEffects(holder.favoriteButton);
            UIHelper.setupButtonEffects(holder.deleteButton);

            if (contents.get(i) instanceof Book)
                holder.border.setVisibility(View.VISIBLE);
            else if (contents.get(i) instanceof Game)
                holder.border.setVisibility(View.INVISIBLE);

            switch (contents.get(i).downloadStatus) {
                case NOT_DOWNLOADED:
                    holder.progressLayout.setVisibility(View.GONE);
                    holder.downloadedButton.setVisibility(View.INVISIBLE);
                    holder.deleteButton.setVisibility(View.INVISIBLE);
                    holder.downloadProgress.setProgress(0);
                    break;

                case ADDED_TO_QUEUE:
                    holder.progressLayout.setVisibility(View.VISIBLE);
                    holder.loading.setVisibility(View.VISIBLE);
                    holder.downloadProgress.setVisibility(View.GONE);
                    break;

                case DOWNLOADING:
                    holder.progressLayout.setVisibility(View.VISIBLE);
                    holder.loading.setVisibility(View.GONE);
                    holder.downloadProgress.setVisibility(View.VISIBLE);
                    holder.downloadedButton.setVisibility(View.INVISIBLE);
                    holder.deleteButton.setVisibility(View.INVISIBLE);
                    break;

                case DOWNLOADED:
                    holder.progressLayout.setVisibility(View.GONE);
                    holder.downloadedButton.setVisibility(View.VISIBLE);
                    holder.deleteButton.setVisibility(View.VISIBLE);
                    holder.downloadProgress.setProgress(0);
                    break;
            }

            // Set buttons margin top
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.buttonsLayout.getLayoutParams();
            if (contents.get(i) instanceof Book)
                layoutParams.topMargin = -(((viewWidth / columns) / 3) / 2);
            else if (contents.get(i) instanceof SoundBook)
            layoutParams.topMargin = -(((viewWidth / columns) / 3) / 2);
            else
                layoutParams.topMargin = (int) -(((viewWidth / columns) / 3) * 0.95);

            holder.buttonsLayout.setLayoutParams(layoutParams);

            // Show/Hide New Icon
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.DAY_OF_YEAR, -30);

            Calendar contentDate = Calendar.getInstance();
            contentDate.setTime(contents.get(i).date);

            float factor = 3.0f / columns;
            if (contentDate.getTimeInMillis() > calendar.getTimeInMillis()) {
                holder.newIcon.setVisibility(View.VISIBLE);
                UIHelper.fitSize(screenOptions, holder.newIcon, (int) (45 * factor), (int) (45 * 3.1f * factor));
            } else
                holder.newIcon.setVisibility(View.GONE);
        }

        class ContentViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            ImageView image;
            ImageButton favoriteButton;
            ImageButton deleteButton;
            ImageButton downloadedButton;
            ImageView border;
            ImageView freeIcon;
            ImageView newIcon;
            LinearLayout buttonsLayout;
            RelativeLayout progressLayout;
            ProgressPieView downloadProgress;
            ProgressBar loading;

            ContentViewHolder(View view) {
                super(view);
                title = itemView.findViewById(R.id.content_title);
                image = itemView.findViewById(R.id.content_image);
                favoriteButton = itemView.findViewById(R.id.favorite_button_content);
                deleteButton = itemView.findViewById(R.id.delete_button);
                downloadedButton = itemView.findViewById(R.id.downloaded_icon);
                border = itemView.findViewById(R.id.content_border);
                freeIcon = itemView.findViewById(R.id.free_icon);
                newIcon = itemView.findViewById(R.id.new_icon);
                buttonsLayout = itemView.findViewById(R.id.buttons_layout);
                progressLayout = itemView.findViewById(R.id.progress_layout);
                downloadProgress = itemView.findViewById(R.id.download_progress);
                loading = itemView.findViewById(R.id.loading);
            }
        }
    }
}
package com.efsoft.hangmedia.hangtv.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.hangtv.item.PlayListItem;
import com.efsoft.hangmedia.hangtv.util.Constant;
import com.efsoft.hangmedia.hangtv.util.OnLoadMoreListener;
import com.efsoft.hangmedia.hangtv.youtubeplaylist.YtPlayActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ItemRowHolder> {

    private ArrayList<PlayListItem> dataList;
    private Context mContext;
    private final int VIEW_ITEM = 1;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private String PlayListId;

    public PlayListAdapter(Context context, ArrayList<PlayListItem> dataList, RecyclerView recyclerView, String id) {
        this.dataList = dataList;
        this.mContext = context;
        this.PlayListId = id;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_playlist, parent, false);
            return new ContentViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_progressbar, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == VIEW_ITEM) {
            ContentViewHolder holder = (ContentViewHolder) viewHolder;
            final PlayListItem singleItem = dataList.get(position);
            holder.text.setText(singleItem.getPlaylistName());
            Picasso.get().load(singleItem.getPlaylistImageurl()).placeholder(R.drawable.logo_hang_mobile_black_landscape).into(holder.image);
            holder.text_cat.setText(Constant.PLAY_NAME_TITLE);

            holder.lyt_parent.setOnClickListener(v -> {

                Intent intent = new Intent(mContext, YtPlayActivity.class);
                intent.putExtra("PId", PlayListId);
                intent.putExtra("position", position);
                mContext.startActivity(intent);

            });
            holder.imageShare.setOnClickListener(v -> {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, singleItem.getPlaylistName() + "\nhttps://www.youtube.com/watch?v=" + singleItem.getPlaylistId());
                sendIntent.setType("text/plain");
                mContext.startActivity(sendIntent);

            });
        } else {
            ((ProgressViewHolder) viewHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROG = 0;
        return dataList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        ItemRowHolder(View itemView) {
            super(itemView);
        }
    }

    class ContentViewHolder extends ItemRowHolder {
        ImageView image, imageShare;
        TextView text, text_cat;
        LinearLayout lyt_parent;

        ContentViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            imageShare = itemView.findViewById(R.id.imageShare);
            text_cat = itemView.findViewById(R.id.text_cat);
        }
    }

    class ProgressViewHolder extends ItemRowHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar1);
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        loading = false;
    }

}
package com.efsoft.hangmedia.hangtv.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.hangtv.item.ItemVideo;
import com.efsoft.hangmedia.hangtv.youtubeplaylist.YoutubePlay;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ItemRowHolder> {

    private ArrayList<ItemVideo> dataList, mDataList;
    private Context mContext;
    private ProgressDialog pDialog;

    public VideoAdapter(Context context, ArrayList<ItemVideo> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        mDataList = new ArrayList<>();
        mDataList.addAll(dataList);
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_video, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
        final ItemVideo singleItem = dataList.get(position);
        holder.text.setText(singleItem.getVideoName());
        Picasso.get().load(singleItem.getVideoImage()).placeholder(R.drawable.logo_hang_mobile_black_landscape).into(holder.image);

        holder.lyt_parent.setOnClickListener(v -> {

            Intent i = new Intent(mContext, YoutubePlay.class);
            i.putExtra("id", singleItem.getVideoUrl());
            mContext.startActivity(i);

        });

        holder.imageShare.setOnClickListener(view -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, singleItem.getVideoName() + "\n" + "https://www.youtube.com/watch?v=" + singleItem.getVideoUrl());
            sendIntent.setType("text/plain");
            mContext.startActivity(sendIntent);
        });


    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        ImageView image, imageShare;
        TextView text;
        LinearLayout lyt_parent;

        ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            imageShare = itemView.findViewById(R.id.imageShare);
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        dataList.clear();
        if (charText.length() == 0) {
            dataList.addAll(mDataList);
        } else {
            for (ItemVideo wp : mDataList) {
                if (wp.getVideoName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    dataList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}

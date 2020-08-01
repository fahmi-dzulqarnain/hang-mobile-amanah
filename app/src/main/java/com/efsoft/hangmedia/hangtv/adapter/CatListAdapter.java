package com.efsoft.hangmedia.hangtv.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.hangtv.db.DatabaseHelper;
import com.efsoft.hangmedia.hangtv.item.ItemPlayList;
import com.efsoft.hangmedia.hangtv.util.Constant;
import com.efsoft.hangmedia.hangtv.util.PopUpAds;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by laxmi.
 */
public class CatListAdapter extends RecyclerView.Adapter<CatListAdapter.ItemRowHolder> {

    private ArrayList<ItemPlayList> dataList;
    private Context mContext;
    private boolean isVisible;
    private DatabaseHelper databaseHelper;

    public CatListAdapter(Context context, ArrayList<ItemPlayList> dataList, boolean flag) {
        this.dataList = dataList;
        this.mContext = context;
        this.isVisible = flag;
        databaseHelper = new DatabaseHelper(mContext);
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ca_list_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
        final ItemPlayList singleItem = dataList.get(position);
        holder.text.setText(singleItem.getPlayListName());
        Picasso.get().load(singleItem.getImage()).placeholder(R.drawable.logo_hang_mobile_black).into(holder.image);

        if (databaseHelper.getFavouriteById(String.valueOf(singleItem.getId()))) {
            holder.imageFavourite.setImageResource(R.drawable.ic_favourite_hover);
        } else {
            holder.imageFavourite.setImageResource(R.drawable.ic_favourite_1);
        }

        holder.imageFavourite.setOnClickListener(v -> {
            ContentValues fav = new ContentValues();
            if (databaseHelper.getFavouriteById(String.valueOf(singleItem.getId()))) {
                databaseHelper.removeFavouriteById(String.valueOf(singleItem.getId()));
                holder.imageFavourite.setImageResource(R.drawable.ic_favourite_1);
                Toast.makeText(mContext, mContext.getString(R.string.favourite_remove), Toast.LENGTH_SHORT).show();

            } else {
                fav.put(DatabaseHelper.KEY_ID, String.valueOf(singleItem.getId()));
                fav.put(DatabaseHelper.KEY_TITLE, singleItem.getPlayListName());
                fav.put(DatabaseHelper.KEY_IMAGE, singleItem.getImage());
                fav.put(DatabaseHelper.KEY_PLAYLIST, singleItem.getPlayListUrl());
                databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                holder.imageFavourite.setImageResource(R.drawable.ic_favourite_hover);
                Toast.makeText(mContext, mContext.getString(R.string.favourite_add), Toast.LENGTH_SHORT).show();
            }
        });

        holder.lyt_parent.setOnClickListener(v -> {
            Constant.PLAY_NAME_TITLE = singleItem.getPlayCatName();
            PopUpAds.ShowInterstitialAds(mContext, singleItem.getPlayListUrl(), singleItem.getPlayListName());
        });


    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        ImageView image, imageFavourite;
        TextView text;
        LinearLayout lyt_parent;

        ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            imageFavourite = itemView.findViewById(R.id.imageFavourite);
            text = itemView.findViewById(R.id.text);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
        }
    }
}

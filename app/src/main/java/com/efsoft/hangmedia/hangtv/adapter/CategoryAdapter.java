package com.efsoft.hangmedia.hangtv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.hangtv.item.ItemCategory;
import com.efsoft.hangmedia.hangtv.util.PopUpAdsCat;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by laxmi.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ItemRowHolder> {

    private ArrayList<ItemCategory> dataList;
    private Context mContext;

    public CategoryAdapter(Context context, ArrayList<ItemCategory> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_category, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder holder, final int position) {
        final ItemCategory singleItem = dataList.get(position);
        holder.text.setText(singleItem.getCategoryName());
        Picasso.get().load(singleItem.getCategoryImage()).placeholder(R.drawable.logo_hang_mobile_black).into(holder.image);
        holder.lyt_parent.setOnClickListener(v -> PopUpAdsCat.ShowInterstitialAds(mContext,String.valueOf(singleItem.getCategoryId()),singleItem.getCategoryName()));
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView text;
        LinearLayout lyt_parent;

        ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
        }
    }
}
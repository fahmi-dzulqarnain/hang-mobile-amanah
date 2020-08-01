package com.efsoft.hangmedia.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.model.AtkjSound;
import com.efsoft.hangmedia.model.Program1;

import java.util.List;

public class AtkjSoundAdapter extends RecyclerView.Adapter<AtkjSoundAdapter.MyViewHolder> {

    private List<AtkjSound> atkjSoundList;
    private static Context context;

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView judul, tanggal, url;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            judul = itemView.findViewById(R.id.txtJudulAtkj);
            tanggal = itemView.findViewById(R.id.txtTanggalAtkj);
            url = itemView.findViewById(R.id.txtUrlAtkj);
        }
    }

    public AtkjSoundAdapter(List<AtkjSound> atkjSoundList){
        this.atkjSoundList = atkjSoundList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_atkj_sound, viewGroup, false);
        context = viewGroup.getContext();

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Typeface oSSB = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-SemiBold.ttf");
        Typeface oSR = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");

        AtkjSound atkjSound = atkjSoundList.get(position);

        holder.judul.setText(atkjSound.getJudul());
        holder.judul.setTypeface(oSSB);
        holder.tanggal.setText(atkjSound.getTanggal());
        holder.tanggal.setTypeface(oSR);
        holder.url.setText(atkjSound.getUrl());

    }

    @Override
    public int getItemCount() {
        return atkjSoundList.size();
    }
}

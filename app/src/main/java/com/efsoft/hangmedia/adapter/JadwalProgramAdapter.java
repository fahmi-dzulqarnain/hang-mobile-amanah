package com.efsoft.hangmedia.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.model.JadwalProgram;
import com.efsoft.hangmedia.model.Program2;

import java.util.List;

public class JadwalProgramAdapter extends RecyclerView.Adapter<JadwalProgramAdapter.MyViewHolder> {

    private List<JadwalProgram> jadwalProgramList;
    private Context context;

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView pembahasan, ustadz, jam, menit;
        ImageView isTvLive;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            pembahasan = itemView.findViewById(R.id.txtJudulJadwal);
            ustadz = itemView.findViewById(R.id.txtPemateriJadwal);
            jam = itemView.findViewById(R.id.txtJamJadwal);
            menit = itemView.findViewById(R.id.txtMenitJadwal);
            isTvLive = itemView.findViewById(R.id.imgTvLive);
        }
    }

    public JadwalProgramAdapter(List<JadwalProgram> jadwalProgramList){
        this.jadwalProgramList = jadwalProgramList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_program_schedule, viewGroup, false);
        context = viewGroup.getContext();

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        JadwalProgram jadwalProgram = jadwalProgramList.get(position);

        Typeface monsterratSemibold = Typeface.createFromAsset(context.getAssets(), "Montserrat-SemiBold.ttf");
        Typeface monsterratRegular = Typeface.createFromAsset(context.getAssets(), "Montserrat-Regular.ttf");
        holder.pembahasan.setTypeface(monsterratSemibold);
        holder.ustadz.setTypeface(monsterratRegular);
        holder.jam.setTypeface(monsterratRegular);
        holder.menit.setTypeface(monsterratRegular);

        holder.pembahasan.setText(jadwalProgram.getPembahasan());
        holder.ustadz.setText(jadwalProgram.getUstadz());

        String[] waktu = jadwalProgram.getJam().split(":");

        holder.jam.setText(waktu[0]);
        holder.menit.setText(waktu[1]);

        boolean isLive = jadwalProgram.getIsTvLive();
        if (isLive){
            holder.isTvLive.setImageResource(R.drawable.youtube_logo);
        } else {
            holder.isTvLive.setImageResource(R.drawable.youtube_dark);
        }

    }

    @Override
    public int getItemCount() {
        return jadwalProgramList.size();
    }
}

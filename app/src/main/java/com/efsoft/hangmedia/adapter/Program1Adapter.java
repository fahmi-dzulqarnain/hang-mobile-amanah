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
import com.efsoft.hangmedia.model.Program1;

import java.util.List;

public class Program1Adapter extends RecyclerView.Adapter<Program1Adapter.MyViewHolder> {

    private List<Program1> program1List;
    private Context context;

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView namaProgram, live,time;
        ImageView imgProgram;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            namaProgram = itemView.findViewById(R.id.txtNamaProgram);
            time = itemView.findViewById(R.id.txtTimeProgram);
            live = itemView.findViewById(R.id.txtLiveProgram);
            imgProgram = itemView.findViewById(R.id.imgProgram1);
        }
    }

    public Program1Adapter(List<Program1> program1List){
        this.program1List = program1List;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_program_1, viewGroup, false);
        context = viewGroup.getContext();

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Program1 program1 = program1List.get(position);

        Typeface mSSB, mR;
        mSSB = Typeface.createFromAsset(context.getAssets(), "Montserrat-SemiBold.ttf");
        mR = Typeface.createFromAsset(context.getAssets(), "Montserrat-Regular.ttf");

        holder.namaProgram.setText(program1.getNamaProgram());
        holder.time.setText(program1.getTime());
        holder.live.setText(program1.getLive());
        holder.imgProgram.setImageResource(program1.getImage());

        holder.namaProgram.setTypeface(mSSB);
        holder.time.setTypeface(mR);
        holder.live.setTypeface(mR);

    }

    @Override
    public int getItemCount() {
        return program1List.size();
    }
}

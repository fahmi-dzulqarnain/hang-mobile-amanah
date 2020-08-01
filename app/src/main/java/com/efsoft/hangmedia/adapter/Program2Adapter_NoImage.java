package com.efsoft.hangmedia.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.model.Program2;

import java.util.List;

public class Program2Adapter_NoImage extends RecyclerView.Adapter<Program2Adapter_NoImage.MyViewHolder> {

    private List<Program2> program2List;

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView pemmbahasan, ustadz, jam;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            pemmbahasan = itemView.findViewById(R.id.txtPembahasanProgram3);
            ustadz = itemView.findViewById(R.id.txtUstadzProgram3);
            jam = itemView.findViewById(R.id.txtJamProgram3);
        }
    }

    public Program2Adapter_NoImage(List<Program2> program2List){
        this.program2List = program2List;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_program_3, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Program2 program2 = program2List.get(position);

        holder.pemmbahasan.setText(program2.getPembahasan());
        holder.ustadz.setText(program2.getUstadz());
        holder.jam.setText(program2.getJam());

    }

    @Override
    public int getItemCount() {
        return program2List.size();
    }
}

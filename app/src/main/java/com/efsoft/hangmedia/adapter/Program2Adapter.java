package com.efsoft.hangmedia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.model.Program1;
import com.efsoft.hangmedia.model.Program2;

import java.util.List;

public class Program2Adapter extends RecyclerView.Adapter<Program2Adapter.MyViewHolder> {

    private List<Program2> program2List;

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView pemmbahasan, ustadz, jam;
        ImageView imageUrl;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            pemmbahasan = itemView.findViewById(R.id.txtPembahasanProgram);
            ustadz = itemView.findViewById(R.id.txtUstadzProgram);
            jam = itemView.findViewById(R.id.txtJamProgram);
            imageUrl = itemView.findViewById(R.id.imgProgram2);
        }
    }

    public Program2Adapter(List<Program2> program2List){
        this.program2List = program2List;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_program_2, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Program2 program2 = program2List.get(position);

        Glide.with(holder.imageUrl.getContext()).load(R.drawable.image_loading).into(holder.imageUrl);
        holder.pemmbahasan.setText(program2.getPembahasan());
        holder.ustadz.setText(program2.getUstadz());
        holder.jam.setText(program2.getJam());

        String url = program2.getImage_url();
        String[] fix_url = url.split(" ");
        String img_url = "http://tiviapi.online/hang_program_api/upload/";

        if (fix_url.length > 1){
            img_url = img_url + fix_url[0];

            for (int i = 1; i < fix_url.length; i++) {
                img_url = img_url + "%20" + fix_url[i];
            }
        }

        Glide.with(holder.imageUrl.getContext()).load(img_url).into(holder.imageUrl);

    }

    @Override
    public int getItemCount() {
        return program2List.size();
    }
}

package com.efsoft.hangmedia.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.adapter.Program1Adapter;
import com.efsoft.hangmedia.adapter.Program2Adapter;
import com.efsoft.hangmedia.adapter.Program2Adapter_NoImage;
import com.efsoft.hangmedia.database.NewDatabase;
import com.efsoft.hangmedia.database.ProgramDatabase;
import com.efsoft.hangmedia.listener.RecyclerTouchListener;
import com.efsoft.hangmedia.model.Program1;
import com.efsoft.hangmedia.model.Program2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Program2Activity extends AppCompatActivity {

    private List<Program2> program2List = new ArrayList<>();
    private Program2Adapter program2Adapter;
    private Program2Adapter_NoImage program2Adapter_noImage;
    private ProgramDatabase programDatabase;
    private NewDatabase newDatabase;
    private TextView txtNoJadwal;
    private ImageView imgNoJadwal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program2);

        programDatabase = new ProgramDatabase(this);
        newDatabase = new NewDatabase(this);

        RelativeLayout actionBar = findViewById(R.id.toolbarIdProgram2);
        txtNoJadwal = findViewById(R.id.txtNoJadwal);

        TextView txtActionBar;
        txtActionBar = findViewById(R.id.txtNamaProgram2);
        imgNoJadwal = findViewById(R.id.imgNoProgram);

        Typeface oSSBItc = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");
        txtActionBar.setTypeface(oSSBItc);
        txtNoJadwal.setTypeface(oSSBItc);

        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams)actionBar.getLayoutParams();
        relativeParams.setMargins(0, getStatusBarHeight(), 0, 0);
        actionBar.setLayoutParams(relativeParams);

        Intent intent = getIntent();
        String program = intent.getStringExtra("PROGRAM_KEY");

        RecyclerView recyclerView = findViewById(R.id.rvProgram2);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Program2Activity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RelativeLayout.LayoutParams relativeParamsMain = (RelativeLayout.LayoutParams)recyclerView.getLayoutParams();
        relativeParamsMain.setMargins(0, getStatusBarHeight() + 102, 0, 0);
        recyclerView.setLayoutParams(relativeParamsMain);

        try {
            Cursor res = programDatabase.getProgram2(program);
            res.moveToFirst();

            if (res.getCount() != 0){
                String imageUrl = res.getString(3);

                if (imageUrl.equals("")){
                    program2Adapter_noImage = new Program2Adapter_NoImage(program2List);
                    recyclerView.setAdapter(program2Adapter_noImage);

                    prepareProgram2DataNoImage(program);
                } else {
                    program2Adapter = new Program2Adapter(program2List);
                    recyclerView.setAdapter(program2Adapter);

                    prepareProgram2Data(program);
                }
            }

        } catch (Exception e) {
            Log.d("Program2Activity", "onCreate: " + e.getMessage());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // set title dialog
            builder.setTitle("Ada Masalah pada Aplikasi");

            // set pesan dari dialog
            builder.setMessage("Silakan Laporkan kepada Kami")
                    .setIcon(R.mipmap.ic_launcher)
                    .setCancelable(false)
                    .setPositiveButton("Kirim", (dialog, id) -> new Intent(Intent.ACTION_VIEW,
                            Uri.parse(
                                    "https://api.whatsapp.com/send?phone=+6281901010106&text=Masalah Aplikasi Hang Mobile (Program2Act) " + e.getMessage()
                            )))
                    .setNegativeButton("Tutup", (dialog, id) -> {
                        dialog.cancel();
                    });

            // membuat alert dialog dari builder
            AlertDialog alertDialog = builder.create();

            // menampilkan alert dialog
            alertDialog.show();
        }
    }

    private void prepareProgram2Data(String namaProgram) {

        Cursor res = programDatabase.getProgram2(namaProgram);

        if (res.getCount() == 0) {

            Cursor language = newDatabase.getPrayTime("language");
            language.moveToFirst();

            if(language.getCount() != 0){
                String bahasa = language.getString(0);
                txtNoJadwal.setVisibility(View.VISIBLE);
                imgNoJadwal.setVisibility(View.VISIBLE);
                if (bahasa.equals("id")){
                    txtNoJadwal.setText(R.string.no_schedule);
                } else if (bahasa.equals("en")){
                    txtNoJadwal.setText(R.string.no_schedule_en);
                }
            }

        } else {

            imgNoJadwal.setVisibility(View.INVISIBLE);
            txtNoJadwal.setVisibility(View.INVISIBLE);
            Program2[] program2s = new Program2[res.getCount()];
            res.moveToFirst();

            int i = 0;
            while(!res.isAfterLast()) {
                program2s[i] = new Program2(res.getString(0), res.getString(1)
                                          , res.getString(2), res.getString(3));
                program2List.add(program2s[i]);

                i++;
                res.moveToNext();
            }
            program2Adapter.notifyDataSetChanged();
        }
    }

    private void prepareProgram2DataNoImage(String namaProgram) {

        Cursor res = programDatabase.getProgram2(namaProgram);

        if (res.getCount() == 0) {

            Cursor language = newDatabase.getPrayTime("language");
            language.moveToFirst();

            if(language.getCount() != 0){
                String bahasa = language.getString(0);
                txtNoJadwal.setVisibility(View.VISIBLE);
                imgNoJadwal.setVisibility(View.VISIBLE);
                if (bahasa.equals("id")){
                    txtNoJadwal.setText(R.string.no_schedule);
                } else if (bahasa.equals("en")){
                    txtNoJadwal.setText(R.string.no_schedule_en);
                }
            }

        } else {

            imgNoJadwal.setVisibility(View.INVISIBLE);
            txtNoJadwal.setVisibility(View.INVISIBLE);
            Program2[] program2s = new Program2[res.getCount()];
            res.moveToFirst();

            int i = 0;
            while(!res.isAfterLast()) {
                program2s[i] = new Program2(res.getString(0), res.getString(1)
                        , res.getString(2), res.getString(3));
                program2List.add(program2s[i]);

                i++;
                res.moveToNext();
            }

            program2Adapter_noImage.notifyDataSetChanged();
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}

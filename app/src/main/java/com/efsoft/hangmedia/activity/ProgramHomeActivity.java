package com.efsoft.hangmedia.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.adapter.Program1Adapter;
import com.efsoft.hangmedia.database.ProgramDatabase;
import com.efsoft.hangmedia.listener.RecyclerTouchListener;
import com.efsoft.hangmedia.model.Program1;
import com.efsoft.hangmedia.retrofit.ProgramResponse;
import com.efsoft.hangmedia.retrofit.Retrofitclient;
import com.efsoft.hangmedia.retrofit.TableProgram;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgramHomeActivity extends AppCompatActivity {

    private List<Program1> program1List = new ArrayList<>();
    private Program1Adapter program1Adapter;
    private ProgramDatabase programDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_home);

        programDatabase = new ProgramDatabase(this);

        if (isInternetOn()){
            getJadwalData();
        }

        TextView txtActionBar, txtContentBar;
        txtActionBar = findViewById(R.id.txtPilihProgramHome);
        txtContentBar = findViewById(R.id.txtKeteranganProgram);

        Typeface mSSB, mR;
        mSSB = Typeface.createFromAsset(getAssets(), "Montserrat-SemiBold.ttf");
        mR = Typeface.createFromAsset(getAssets(), "Montserrat-Regular.ttf");
        txtActionBar.setTypeface(mSSB);
        txtContentBar.setTypeface(mR);

        RelativeLayout actionBar = findViewById(R.id.toolbarIdProgramHome);
        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams)actionBar.getLayoutParams();
        relativeParams.setMargins(0, getStatusBarHeight(), 0, 0);
        actionBar.setLayoutParams(relativeParams);

        RecyclerView recyclerView = findViewById(R.id.rvProgramHome);
        program1Adapter = new Program1Adapter(program1List);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(program1Adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView,
                new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        String namaProgram = ((TextView) Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(position))
                                .itemView.findViewById(R.id.txtNamaProgram)).getText().toString();

                        if (namaProgram.equals("Jadwal Siaran Hang Media")){
                            startActivity(new Intent(ProgramHomeActivity.this, ProgramScheduleActivity.class));
                        } else {
                            Intent intent = new Intent(ProgramHomeActivity.this, Program2Activity.class);
                            intent.putExtra("PROGRAM_KEY", namaProgram);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

        prepareProgramData();
    }

    private void prepareProgramData() {

        Program1 program1 = new Program1("Jadwal Siaran Hang Media", "Setiap Hari", "LIVE", R.drawable.p_live_radio);
        program1List.add(program1);

        Program1 program2 = new Program1("Kajian Ba\'da Maghrib", "Senin - Jum\'at Ba\'da Maghrib", "Live Hang FM dan Hang TV", R.drawable.p_kajian_bada_maghrib);
        program1List.add(program2);

        Program1 program3 = new Program1("Risalah Malam", "Setiap Hari Pukul 20:00", "Live Hang 106 FM", R.drawable.p_risalah_malam);
        program1List.add(program3);

        program1Adapter.notifyDataSetChanged();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void getJadwalData(){

        Call<ProgramResponse> call = Retrofitclient.getmInstance().getApi().getProgram();
        call.enqueue(new Callback<ProgramResponse>() {

            @Override
            public void onResponse(@NonNull Call<ProgramResponse> call, @NonNull Response<ProgramResponse> response) {
                ProgramResponse programResponse = response.body();

                if (programResponse != null){
                    if (programResponse.getData().size() != 0){
                        List<TableProgram> data = programResponse.getData();

                        programDatabase.deleteProgram();

                        for (int k = 0; k < programResponse.getData().size(); k++) {
                            programDatabase.insertProgram(data.get(k).getNama_program(), data.get(k).getPembahasan()
                                    , data.get(k).getUstadz(), data.get(k).getJam(), data.get(k).getImage_url());
                        }

                        Log.d("ProgramHomeActivity", "onResponse: " + "Program data downloaded");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProgramResponse> call, @NonNull Throwable t) {
                //TODO Something
            }
        });
    }

    public boolean isInternetOn(){
        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = Objects.requireNonNull(conMgr).getActiveNetworkInfo();
        return netinfo != null;
    }

}

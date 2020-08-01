package com.efsoft.hangmedia.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.adapter.AtkjSoundAdapter;
import com.efsoft.hangmedia.database.AtkjSoundDatabase;
import com.efsoft.hangmedia.listener.RecyclerTouchListener;
import com.efsoft.hangmedia.model.AtkjSound;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AtkjListActivity extends AppCompatActivity {

    private AtkjSoundAdapter atkjSoundAdapter;
    private List<AtkjSound> atkjSoundList = new ArrayList<>();
    private AtkjSoundDatabase atkjSoundDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atkj_list);

        Typeface oSB = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");
        TextView txtTitle = findViewById(R.id.txtTitleAtkj);
        txtTitle.setTypeface(oSB);

        atkjSoundDatabase = new AtkjSoundDatabase(this);
        RecyclerView recyclerView = findViewById(R.id.rvATKJ);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AtkjListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        atkjSoundAdapter = new AtkjSoundAdapter(atkjSoundList);
        recyclerView.setAdapter(atkjSoundAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView,
                new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        String url = ((TextView) Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(position))
                                .itemView.findViewById(R.id.txtUrlAtkj)).getText().toString();
                        String judul = ((TextView) Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(position))
                                .itemView.findViewById(R.id.txtJudulAtkj)).getText().toString();

                        Intent intent = new Intent(AtkjListActivity.this, AtkjSoundActivity.class);
                        intent.putExtra("URL_KEY", url);
                        intent.putExtra("JUDUL_KEY", judul);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

        prepareAtkjData();
    }

    private void prepareAtkjData() {

        Cursor res = atkjSoundDatabase.getAtkj();

        AtkjSound[] atkjSounds = new AtkjSound[res.getCount()];
        res.moveToFirst();

        int i = 0;
        while(!res.isAfterLast()) {
            atkjSounds[i] = new AtkjSound(res.getString(0), res.getString(1)
                    , res.getString(2));
            atkjSoundList.add(atkjSounds[i]);

            i++;
            res.moveToNext();
        }

        atkjSoundAdapter.notifyDataSetChanged();
    }
}

package com.efsoft.hangmedia.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.adapter.JadwalProgramAdapter;
import com.efsoft.hangmedia.model.JadwalProgram;

import java.util.ArrayList;
import java.util.List;

public class JumatFragment extends Fragment {

    private List<JadwalProgram> jadwalProgramList = new ArrayList<>();
    private JadwalProgramAdapter jadwalProgramAdapter;
    private Context context;

    public JumatFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jumat, container, false);

        RecyclerView rvJadwalJumat = view.findViewById(R.id.rvJadwalJumat);
        jadwalProgramAdapter = new JadwalProgramAdapter(jadwalProgramList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvJadwalJumat.setLayoutManager(layoutManager);
        rvJadwalJumat.setItemAnimator(new DefaultItemAnimator());
        rvJadwalJumat.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        rvJadwalJumat.setAdapter(jadwalProgramAdapter);

        if (jadwalProgramList.size() == 0){
            prepareProgramData();
        }

        return view;
    }

    private void prepareProgramData() {

        JadwalProgram program1 = new JadwalProgram("Dzikir Pagi & ATKJ Singkat", "Live Studio 1", "05:00", false);
        jadwalProgramList.add(program1);

        JadwalProgram program2 = new JadwalProgram("Berkah Pagi", "Ustadz Rahmat Nur Hidayat", "06:00", true);
        jadwalProgramList.add(program2);

        JadwalProgram program3 = new JadwalProgram("Kajian Singkat", "Rekaman Asatidzah", "08:30", false);
        jadwalProgramList.add(program3);

        JadwalProgram program4 = new JadwalProgram("Hadits dan Materi Jum\'at", "Rekaman", "10:30", false);
        jadwalProgramList.add(program4);

        JadwalProgram program = new JadwalProgram("Shalat Jum\'at", "Live Mushalla Al-Ikhlash", "12:00", false);
        jadwalProgramList.add(program);

        JadwalProgram program5 = new JadwalProgram("Kajian Tematik", "Rekaman Asatidzah", "13:30", false);
        jadwalProgramList.add(program5);

        JadwalProgram program6 = new JadwalProgram("Murattal Al-Quran", "Rekaman", "14:30", false);
        jadwalProgramList.add(program6);

        JadwalProgram program7 = new JadwalProgram("Kajian Singkat & Dzikir Sore", "Rekaman", "15:00", false);
        jadwalProgramList.add(program7);

        JadwalProgram program8 = new JadwalProgram("Bahasa Arab", "Ust. Achmadi, Lc & Ust. Thaha, Lc", "16:30", true);
        jadwalProgramList.add(program8);

        JadwalProgram program9 = new JadwalProgram("Kajian Ba\'da Maghrib", "Ustadz Abu Ahmad Supiandi", "18:30", true);
        jadwalProgramList.add(program9);

        JadwalProgram program10 = new JadwalProgram("Tafsir Ibnu Katsir", "Ustadz Idris Sulaiman", "20:00", false);
        jadwalProgramList.add(program10);

        JadwalProgram program11 = new JadwalProgram("Berkah Pagi", "Siaran Ulang", "22:00", false);
        jadwalProgramList.add(program11);

        JadwalProgram program12 = new JadwalProgram("Dzikir Malam dan Murattal", "Rekaman", "23:00", false);
        jadwalProgramList.add(program12);

        JadwalProgram program13 = new JadwalProgram("Mutiara Dhuha", "Siaran Ulang", "00:00", false);
        jadwalProgramList.add(program13);

        JadwalProgram program14 = new JadwalProgram("Anda Tanya Kami Jawab", "Siaran Ulang", "02:00", false);
        jadwalProgramList.add(program14);

        JadwalProgram program15 = new JadwalProgram("Menyapa Senja", "Siaran Ulang", "03:00", false);
        jadwalProgramList.add(program15);

        jadwalProgramAdapter.notifyDataSetChanged();
    }

}

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

public class SabtuFragment extends Fragment {

    private List<JadwalProgram> jadwalProgramList = new ArrayList<>();
    private JadwalProgramAdapter jadwalProgramAdapter;
    private Context context;

    public SabtuFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sabtu, container, false);

        RecyclerView rvJadwalSabtu = view.findViewById(R.id.rvJadwalSabtu);
        jadwalProgramAdapter = new JadwalProgramAdapter(jadwalProgramList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvJadwalSabtu.setLayoutManager(layoutManager);
        rvJadwalSabtu.setItemAnimator(new DefaultItemAnimator());
        rvJadwalSabtu.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        rvJadwalSabtu.setAdapter(jadwalProgramAdapter);

        if (jadwalProgramList.size() == 0){
            prepareProgramData();
        }

        return view;
    }

    private void prepareProgramData() {

        JadwalProgram program1 = new JadwalProgram("Dzikir Pagi & ATKJ Singkat", "Rekaman", "05:00", false);
        jadwalProgramList.add(program1);

        JadwalProgram program2 = new JadwalProgram("Berkah Pagi", "Ustadz Abu Humairoh", "06:30", true);
        jadwalProgramList.add(program2);

        JadwalProgram program3 = new JadwalProgram("Kisah dan Teladan", "Ustadz Abu Yazid", "08:30", false);
        jadwalProgramList.add(program3);

        JadwalProgram program4 = new JadwalProgram("Fatwa Ulama", "Syaikh Shalih Al-Fauzan", "10:30", false);
        jadwalProgramList.add(program4);

        JadwalProgram program5 = new JadwalProgram("Taman Remaja", "Ustadz Yusuf Iskandar", "13:30", false);
        jadwalProgramList.add(program5);

        JadwalProgram program6 = new JadwalProgram("Murattal Al-Quran", "Rekaman", "14:30", false);
        jadwalProgramList.add(program6);

        JadwalProgram program7 = new JadwalProgram("Kajian Singkat & Dzikir Sore", "Rekaman", "15:00", false);
        jadwalProgramList.add(program7);

        JadwalProgram program8 = new JadwalProgram("Kajian Muslimah", "Live Mushalla Al-Ikhlaash", "16:00", false);
        jadwalProgramList.add(program8);

        JadwalProgram program9 = new JadwalProgram("Hafizh Anak-anak", "Ustadz Hanifan", "18:30", false);
        jadwalProgramList.add(program9);

        JadwalProgram program10 = new JadwalProgram("Sirah Nabawiyyah", "Ust. Dr. Khalid Basalamah", "20:00", false);
        jadwalProgramList.add(program10);

        JadwalProgram program11 = new JadwalProgram("Berkah Pagi", "Siaran Ulang", "22:00", false);
        jadwalProgramList.add(program11);

        JadwalProgram program12 = new JadwalProgram("Dzikir Malam dan Murattal", "Rekaman", "23:00", false);
        jadwalProgramList.add(program12);

        JadwalProgram program13 = new JadwalProgram("Kisah dan Teladan", "Rekaman", "00:00", false);
        jadwalProgramList.add(program13);

        JadwalProgram program14 = new JadwalProgram("Fatwa Ulama", "Siaran Ulang", "01:00", false);
        jadwalProgramList.add(program14);

        JadwalProgram program15 = new JadwalProgram("Taman Remaja", "Siaran Ulang", "02:00", false);
        jadwalProgramList.add(program15);

        JadwalProgram program16 = new JadwalProgram("Kajian Muslimah", "Siaran Ulang", "03:00", false);
        jadwalProgramList.add(program16);

        jadwalProgramAdapter.notifyDataSetChanged();
    }

}

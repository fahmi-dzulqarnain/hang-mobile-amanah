package com.efsoft.hangmedia.hangtv.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.hangtv.adapter.FavoriteAdapter;
import com.efsoft.hangmedia.hangtv.db.DatabaseHelper;
import com.efsoft.hangmedia.hangtv.item.ItemPlayList;
import com.efsoft.hangmedia.hangtv.util.ItemOffsetDecoration;

import java.util.ArrayList;

public class FavouriteFragment extends Fragment {

    ArrayList<ItemPlayList> mListItem;
    public RecyclerView recyclerView;
    FavoriteAdapter adapter;
    DatabaseHelper databaseHelper;
    TextView text_fav;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.row_fav_recyclerview, container, false);
        setHasOptionsMenu(true);
        mListItem = new ArrayList<>();
        databaseHelper = new DatabaseHelper(getActivity());
        text_fav = rootView.findViewById(R.id.text_fav);
        recyclerView = rootView.findViewById(R.id.vertical_courses_list);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        return rootView;
    }


    private void displayData() {

        adapter = new FavoriteAdapter(getActivity(), mListItem, false);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            text_fav.setVisibility(View.VISIBLE);
        } else {
            text_fav.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListItem = databaseHelper.getFavourite();
        displayData();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
}

package com.oneul.oneul;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.oneul.fragment.HomeFragment;

import java.util.ArrayList;

public class OneulAdapter extends BaseAdapter {
    private final HomeFragment homeFragment;
    private ArrayList<Oneul> oneul = new ArrayList<>();

    public OneulAdapter(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    public void addItem(Oneul oneul) { this.oneul.add(oneul); }

    public void clear() { oneul.clear(); }

    @Override
    public int getCount() {
        return oneul.size();
    }

    @Override
    public Oneul getItem(int position) {
        return oneul.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        OneulView view = new OneulView(homeFragment.getActivity());
        Oneul oneul = this.oneul.get(position);

        view.setoStart(oneul.getoStart());
        view.setoEnd(oneul.getoEnd());
        view.setoTitle(oneul.getoTitle());
        view.setoMemo(oneul.getoMemo());

        return view;
    }

}

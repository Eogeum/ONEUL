package com.oneul.oneul;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oneul.R;

import java.util.ArrayList;

public class OneulAdapter extends RecyclerView.Adapter<OneulHolder> {
    private ArrayList<Oneul> oneul = new ArrayList<>();

    public void addItem(Oneul oneul) {
        this.oneul.add(oneul);
    }

    public void clear() {
        oneul.clear();
    }

    @NonNull
    @Override
    public OneulHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recycler, parent, false);

        return new OneulHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OneulHolder holder, int position) {
        holder.onBind(oneul.get(position));
    }

    @Override
    public int getItemCount() {
        return oneul.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
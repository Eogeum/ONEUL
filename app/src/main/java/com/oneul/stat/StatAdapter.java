package com.oneul.stat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oneul.R;

import java.util.ArrayList;

public class StatAdapter extends RecyclerView.Adapter<StatAdapter.ViewHolder> {

    private final ArrayList<Stat> items;
    private final LayoutInflater mInflater;

    public StatAdapter(Context context, ArrayList<Stat> items) {
        this.mInflater = LayoutInflater.from(context);
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.stat_recycler, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTitle.setText(items.get(position).title_item);
        holder.mTime.setText(items.get(position).time_item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mTime;

        public ViewHolder(View view) {
            super(view);

            mTitle = view.findViewById(R.id.stat_title);
            mTime = view.findViewById(R.id.stat_time);
        }
    }
}
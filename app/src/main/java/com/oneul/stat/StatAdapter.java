package com.oneul.stat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oneul.R;
import com.oneul.extra.Animation;

import java.util.ArrayList;

public class StatAdapter extends RecyclerView.Adapter<StatAdapter.ViewHolder> {
    private final ArrayList<Stat> stat = new ArrayList<>();

    public void addItem(Stat stat) {
        this.stat.add(stat);
    }

    public void clear() {
        stat.clear();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stat_recycler, parent, false));
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTitle.setText(stat.get(position).title_item);
        holder.mTime.setText(stat.get(position).time_item);
//        fixme 24개이상 표현 못함
        holder.image.setColorFilter(Animation.COLORS[position]);
    }

    @Override
    public int getItemCount() {
        return stat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle, mTime;
        ImageView image;

        public ViewHolder(View view) {
            super(view);

            mTitle = view.findViewById(R.id.stat_title);
            mTime = view.findViewById(R.id.stat_time);
            image = view.findViewById(R.id.image);
        }
    }
}
package com.oneul.oneul;

import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
    public void onBindViewHolder(@NonNull final OneulHolder holder, int position) {
        holder.onBind(oneul.get(position));

        holder.t_oMemo.post(new Runnable() {
            @Override
            public void run() {
                Layout layout = holder.t_oMemo.getLayout();

                if (layout != null) {
                    int lines = layout.getLineCount();

                    if (lines > 0) {
                        int ellipsisCount = layout.getEllipsisCount(lines - 1);

                        if (ellipsisCount > 0) {
                            holder.t_oMore.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        holder.t_oMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.equals(holder.t_oMore.getText().toString(), "더보기")) {
                    holder.t_oMemo.setMaxLines(Integer.MAX_VALUE);
                    holder.t_oMemo.setEllipsize(null);
                    holder.t_oMore.setText("닫기");
                } else if (TextUtils.equals(holder.t_oMore.getText().toString(), "닫기")) {
                    holder.t_oMemo.setMaxLines(2);
                    holder.t_oMemo.setEllipsize(TextUtils.TruncateAt.END);
                    holder.t_oMore.setText("더보기");
                }
            }
        });
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
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

public class OneulAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Oneul> oneul = new ArrayList<>();

    public void addItem(Oneul oneul) {
        this.oneul.add(oneul);
    }

    public void clear() {
        oneul.clear();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recycler, parent, false);

        if (viewType == 1) {
            LinearLayout linearLayout = new LinearLayout(parent.getContext());
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(0, 300));

            return new RecyclerView.ViewHolder(linearLayout) {
            };
        }

        return new OneulHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OneulHolder) {
            final OneulHolder oneulHolder = (OneulHolder) holder;

            oneulHolder.onBind(oneul.get(position));

            oneulHolder.t_oMemo.post(new Runnable() {
                @Override
                public void run() {
                    Layout layout = oneulHolder.t_oMemo.getLayout();
                    int lines = layout.getLineCount();

                    if (lines > 0) {
                        int ellipsisCount = layout.getEllipsisCount(lines - 1);

                        if (ellipsisCount > 0) {
                            oneulHolder.t_oMore.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

            oneulHolder.t_oMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.equals(oneulHolder.t_oMore.getText().toString(), "더보기")) {
                        oneulHolder.t_oMemo.setMaxLines(Integer.MAX_VALUE);
                        oneulHolder.t_oMemo.setEllipsize(null);
                        oneulHolder.t_oMore.setText("닫기");
                    } else if (TextUtils.equals(oneulHolder.t_oMore.getText().toString(), "닫기")) {
                        oneulHolder.t_oMemo.setMaxLines(2);
                        oneulHolder.t_oMemo.setEllipsize(TextUtils.TruncateAt.END);
                        oneulHolder.t_oMore.setText("더보기");
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return oneul.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return 1;
        }

        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
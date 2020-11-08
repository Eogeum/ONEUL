package com.oneul.extra;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

public class Animation {
    public static final int[] COLORS = {
            Color.rgb(192, 255, 140), Color.rgb(255, 247, 140),
            Color.rgb(255, 208, 140), Color.rgb(140, 234, 255),
            Color.rgb(255, 140, 157), Color.rgb(217, 80, 138),
            Color.rgb(254, 149, 7), Color.rgb(254, 247, 120),
            Color.rgb(106, 167, 134), Color.rgb(53, 194, 209),
            Color.rgb(193, 37, 82), Color.rgb(255, 102, 0),
            Color.rgb(245, 199, 0), Color.rgb(106, 150, 31),
            Color.rgb(179, 100, 53), Color.rgb(207, 248, 246),
            Color.rgb(148, 212, 212), Color.rgb(136, 180, 187),
            Color.rgb(118, 174, 175), Color.rgb(42, 109, 130),
            Color.rgb(64, 89, 128), Color.rgb(149, 165, 124),
            Color.rgb(217, 184, 162), Color.rgb(191, 134, 134),
            Color.rgb(179, 48, 80), Color.rgb(51, 181, 229),
            rgb("#2ecc71"), rgb("#f1c40f"), rgb("#e74c3c"), rgb("#3498db")};

    //    메모박스 확장
    public static void expand(LinearLayout linearLayout) {
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        slideAnimator(linearLayout, 0, linearLayout.getMeasuredHeight()).start();
    }

    //    메모박스 축소
    public static void collapse(final LinearLayout linearLayout) {
        ValueAnimator animator = slideAnimator(linearLayout, linearLayout.getHeight(), 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                linearLayout.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    public static ValueAnimator slideAnimator(final LinearLayout linearLayout, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
                layoutParams.height = (int) valueAnimator.getAnimatedValue();
                linearLayout.setLayoutParams(layoutParams);
            }
        });

        return animator;
    }
}
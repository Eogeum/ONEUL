package com.oneul.extra;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class Animation {
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
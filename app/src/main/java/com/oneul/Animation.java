package com.oneul;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class Animation {
//    메모 애니메이션
    public static void expand(LinearLayout linearLayout) {
        linearLayout.setVisibility(View.VISIBLE);
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        linearLayout.measure(widthSpec, heightSpec);
        ValueAnimator mAnimator = slideAnimator(linearLayout, 0, linearLayout.getMeasuredHeight());

        mAnimator.start();
    }

    public static void collapse(final LinearLayout linearLayout) {
        int finalHeight = linearLayout.getHeight();
        ValueAnimator mAnimator = slideAnimator(linearLayout, finalHeight, 0);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                linearLayout.setVisibility(View.GONE);
            }
        });

        mAnimator.start();
    }

    public static ValueAnimator slideAnimator(final LinearLayout linearLayout, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (int) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
                layoutParams.height = value;
                linearLayout.setLayoutParams(layoutParams);
            }
        });

        return animator;
    }

//    테두리 애니메이션
//    public static void borderAnimation() {
//        int beforeColor;
//        int afterColor;
//
//        if(((ColorDrawable)mImageView.getBackground()).getColor() == Color.parseColor("#FF0000"))
//        {
//            beforeColor = Color.parseColor("#FF0000");
//            afterColor = Color.parseColor("#0000ED");
//        }
//        else
//        {
//            beforeColor = Color.parseColor("#0000ED");
//            afterColor = Color.parseColor("#FF0000");
//        }
//        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), beforeColor, afterColor);
//        colorAnimation.setDuration(500);
//        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
//        {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animator)
//            {
//                mImageView.setBackgroundColor((int) animator.getAnimatedValue());
//            }
//        });
//        colorAnimation.start();
//    }
}


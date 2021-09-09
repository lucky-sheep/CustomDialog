package com.hunliji.hlj_dialog.xpopup.animator;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.View;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.hunliji.hlj_dialog.xpopup.XPopup;

/**
 * Description: 背景Shadow动画器，负责执行半透明的渐入渐出动画
 * Create by dance, at 2018/12/9
 */
public class ShadowBgAnimator extends PopupAnimator {

    public ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    public int startColor = Color.TRANSPARENT;
    public boolean isZeroDuration = false;
    private ValueAnimator showAnimator;
    private ValueAnimator hideAnimator;

    public ShadowBgAnimator(View target) {
        super(target);
    }

    public ShadowBgAnimator() {
    }

    @Override
    public void initAnimator() {
        targetView.setBackgroundColor(startColor);
    }

    @Override
    public void animateShow() {
        showAnimator = ValueAnimator.ofObject(argbEvaluator, startColor, XPopup.getShadowBgColor());
        showAnimator.addUpdateListener(animation -> targetView.setBackgroundColor((Integer) animation.getAnimatedValue()));
        showAnimator.setInterpolator(new FastOutSlowInInterpolator());
        showAnimator.setDuration(isZeroDuration ? 0 : XPopup.getAnimationDuration()).start();
    }

    public void cancel() {
        if (showAnimator != null) {
            showAnimator.cancel();
        }
        if (hideAnimator != null) {
            hideAnimator.cancel();
        }
    }

    @Override
    public void animateDismiss() {
        hideAnimator = ValueAnimator.ofObject(argbEvaluator, XPopup.getShadowBgColor(), startColor);
        hideAnimator.addUpdateListener(animation -> targetView.setBackgroundColor((Integer) animation.getAnimatedValue()));
        hideAnimator.setInterpolator(new FastOutSlowInInterpolator());
        hideAnimator.setDuration(isZeroDuration ? 0 : XPopup.getDismissAnimationDuration()).start();
    }

    public int calculateBgColor(float fraction) {
        return (int) argbEvaluator.evaluate(fraction, startColor, XPopup.getShadowBgColor());
    }

}

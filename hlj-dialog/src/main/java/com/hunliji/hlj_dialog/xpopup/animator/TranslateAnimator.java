package com.hunliji.hlj_dialog.xpopup.animator;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

import com.hunliji.hlj_dialog.xpopup.XPopup;
import com.hunliji.hlj_dialog.xpopup.enums.PopupAnimation;

/**
 * Description: 平移动画，不带渐变
 * Create by dance, at 2018/12/9
 */
public class TranslateAnimator extends PopupAnimator {
    private AnimatorSet animationSet;
    private AnimatorSet animationDismissSet;

    public TranslateAnimator(View target, PopupAnimation popupAnimation) {
        super(target, popupAnimation);
    }

    @Override
    public void initAnimator() {
        animationSet = new AnimatorSet();
        animationSet.setInterpolator(new DecelerateInterpolator());
        animationSet.setDuration(XPopup.getAnimationDuration());
        animationDismissSet = new AnimatorSet();
        animationDismissSet.setInterpolator(new DecelerateInterpolator());
        animationDismissSet.setDuration(XPopup.getDismissAnimationDuration());
    }

    @Override
    public void animateShow() {
        switch (popupAnimation) {
            case TranslateFromTop:
                slideInTop();
                break;
            case TranslateFromBottom:
                slideInBottom();
                break;
        }
    }

    @Override
    public void animateDismiss() {
        switch (popupAnimation) {
            case TranslateFromTop:
                slideOutTop();
                break;
            case TranslateFromBottom:
                slideOutBottom();
                break;
        }
    }

    @Override
    public void cancel() {
        if (animationSet != null) {
            animationSet.cancel();
        }
        if (animationDismissSet != null) {
            animationDismissSet.cancel();
        }
    }

    private void slideInTop() {
        resetAnim(targetView);
        animationSet.playTogether(
                ObjectAnimator.ofFloat(targetView, "translationY", -targetView.getBottom(), 0f)
        );
        animationSet.start();
    }

    private void slideOutTop() {
        resetAnim(targetView);
        animationDismissSet.playTogether(
                ObjectAnimator.ofFloat(targetView, "translationY", 0f, -targetView.getBottom())
        );
        animationDismissSet.start();
    }

    private void slideInBottom() {
        resetAnim(targetView);
        int distance = ((View) targetView.getParent()).getMeasuredHeight() - targetView.getTop();
        animationSet.playTogether(
                ObjectAnimator.ofFloat(targetView, "translationY", ((float) distance), 0f)
        );
        animationSet.start();
    }

    private void slideOutBottom() {
        resetAnim(targetView);
        int distance = ((View) targetView.getParent()).getMeasuredHeight() - targetView.getTop();
        animationSet.playTogether(
                ObjectAnimator.ofFloat(targetView, "translationY", 0f, ((float) distance))
        );
        animationSet.start();
    }

    public void resetAnim(View view) {
        view.setAlpha(1f);
        view.setScaleX(1f);
        view.setScaleY(1f);
        view.setTranslationX(0f);
        view.setTranslationY(0f);
        view.setRotation(0f);
        view.setRotationX(0f);
        view.setRotationY(0f);
    }
}

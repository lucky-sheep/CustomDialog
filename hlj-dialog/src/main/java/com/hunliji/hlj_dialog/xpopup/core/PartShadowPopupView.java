package com.hunliji.hlj_dialog.xpopup.core;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.hunliji.hlj_dialog.xpopup.animator.PopupAnimator;
import com.hunliji.hlj_dialog.xpopup.animator.TranslateAnimator;
import com.hunliji.hlj_dialog.xpopup.enums.PopupAnimation;
import com.hunliji.hlj_dialog.xpopup.enums.PopupPosition;
import com.hunliji.hlj_dialog.xpopup.util.XPopupUtils;

/**
 * Description: 局部阴影的弹窗，类似于淘宝商品列表的下拉筛选弹窗
 * Create by dance, at 2018/12/21
 */
public abstract class PartShadowPopupView extends AttachPopupView {

    public PartShadowPopupView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        defaultOffsetY = popupInfo.offsetY == 0 ? XPopupUtils.dp2px(getContext(), 0) : popupInfo.offsetY;
        defaultOffsetX = popupInfo.offsetX == 0 ? XPopupUtils.dp2px(getContext(), 0) : popupInfo.offsetX;

        getPopupImplView().setTranslationX(popupInfo.offsetX);
        getPopupImplView().setTranslationY(popupInfo.offsetY);
    }

    @Override
    protected void resetSize() {
        getPopupContentView().setPadding(
                getPaddingLeft(),
                getPaddingTop(),
                getPaddingRight(),
                XPopupUtils.dp2px(getContext(), getBottomPadding()));
    }

    protected int getBottomPadding() {
        return 66;
    }

    @Override
    public void onNavigationBarChange(boolean show) {
        super.onNavigationBarChange(show);
        if (!show) {
            LayoutParams params = (LayoutParams) getPopupContentView().getLayoutParams();
            params.height = XPopupUtils.getWindowHeight(getContext());
            getPopupContentView().setLayoutParams(params);
        }
    }

    @Override
    protected void doAttach() {
        if (popupInfo.getAtView() == null)
            throw new IllegalArgumentException("atView must not be null for PartShadowPopupView！");
        // 指定阴影动画的目标View
        shadowBgAnimator.targetView = getPopupContentView();

        //1. apply width and height
        int rotation = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        MarginLayoutParams params = (MarginLayoutParams) getPopupContentView().getLayoutParams();
        if (rotation == 0) {
            params.width = getMeasuredWidth(); // 满宽
        } else if (rotation == 1 || rotation == 3) {
            params.width = getMeasuredWidth() - (XPopupUtils.isNavigationBarShow(getContext()) ? XPopupUtils.getNavBarHeight(getContext()) : 0);
        }

        //水平居中
        if (popupInfo.isCenterHorizontal && getPopupImplView() != null) {
            getPopupImplView().setTranslationX(XPopupUtils.getWindowWidth(getContext()) / 2f - getPopupContentView().getMeasuredWidth() / 2f);
        }

        //1. 获取atView在屏幕上的位置
        int[] locations = new int[2];
        popupInfo.getAtView().getLocationOnScreen(locations);
        Rect rect = new Rect(locations[0], locations[1], locations[0] + popupInfo.getAtView().getMeasuredWidth(),
                locations[1] + popupInfo.getAtView().getMeasuredHeight());
        int centerY = rect.top + rect.height() / 2;
        if ((centerY > getMeasuredHeight() / 2 || popupInfo.popupPosition == PopupPosition.Top) && popupInfo.popupPosition != PopupPosition.Bottom) {
            // 说明atView在Window下半部分，PartShadow应该显示在它上方，计算atView之上的高度
            params.height = rect.top;
            isShowUp = true;
            params.topMargin = -defaultOffsetY;
            // 同时自定义的impl View应该Gravity居于底部
            View implView = ((ViewGroup) getPopupContentView()).getChildAt(0);
            LayoutParams implParams = (LayoutParams) implView.getLayoutParams();
            implParams.gravity = Gravity.BOTTOM;
            if (getMaxHeight() != 0)
                implParams.height = Math.min(implView.getMeasuredHeight(), getMaxHeight());
            implView.setLayoutParams(implParams);

        } else {
            // atView在上半部分，PartShadow应该显示在它下方，计算atView之下的高度
            params.height = getMeasuredHeight() - rect.bottom;
            // 防止伸到导航栏下面
            if (XPopupUtils.isNavigationBarShow(getContext())) {
                params.height -= XPopupUtils.getNavBarHeight(getContext());
            }
            isShowUp = false;
            params.topMargin = rect.bottom + defaultOffsetY;
            View implView = ((ViewGroup) getPopupContentView()).getChildAt(0);
            LayoutParams implParams = (LayoutParams) implView.getLayoutParams();
            implParams.gravity = Gravity.TOP;
//            int maxHeight = params.height - XPopupUtils.dp2px(getContext(), getBottomPadding());
//            int measuredHeight = implView.getMeasuredHeight();
//            implParams.height = Math.min(measuredHeight, maxHeight);
            implView.setLayoutParams(implParams);
        }
        getPopupContentView().setLayoutParams(params);
        attachPopupContainer.setOnLongClickListener(v -> {
            if (popupInfo.isDismissOnTouchOutside) dismiss();
            return false;
        });
        attachPopupContainer.setOnClickOutsideListener(() -> {
            if (popupInfo.isDismissOnTouchOutside) dismiss();
        });
    }

    //让触摸透过
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (popupInfo.isDismissOnTouchOutside) {
            dismiss();
        }
        return !popupInfo.isDismissOnTouchOutside;
    }

    @Override
    protected PopupAnimator getPopupAnimator() {
        return new TranslateAnimator(getPopupImplView(), isShowUp ?
                PopupAnimation.TranslateFromBottom : PopupAnimation.TranslateFromTop);
    }
}

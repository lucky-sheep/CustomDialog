package com.hunliji.hlj_dialog.xpopup.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hunliji.hlj_dialog.xpopup.XPopup;
import com.hunliji.hlj_dialog.xpopup.animator.EmptyAnimator;
import com.hunliji.hlj_dialog.xpopup.animator.PopupAnimator;
import com.hunliji.hlj_dialog.xpopup.animator.ScaleAlphaAnimator;
import com.hunliji.hlj_dialog.xpopup.animator.ScrollScaleAnimator;
import com.hunliji.hlj_dialog.xpopup.animator.ShadowBgAnimator;
import com.hunliji.hlj_dialog.xpopup.animator.TranslateAlphaAnimator;
import com.hunliji.hlj_dialog.xpopup.animator.TranslateAnimator;
import com.hunliji.hlj_dialog.xpopup.enums.PopupStatus;
import com.hunliji.hlj_dialog.xpopup.util.KeyboardUtils;
import com.hunliji.hlj_dialog.xpopup.util.XPopupUtils;
import com.hunliji.hlj_dialog.xpopup.util.navbar.NavigationBarObserver;
import com.hunliji.hlj_dialog.xpopup.util.navbar.OnNavigationBarListener;

import java.util.ArrayList;
import java.util.Stack;

import static com.hunliji.hlj_dialog.xpopup.enums.PopupAnimation.NoAnimation;


/**
 * Description: 弹窗基类
 * Create by lxj, at 2018/12/7
 */
public abstract class BasePopupView extends FrameLayout implements OnNavigationBarListener {
    private static Stack<BasePopupView> stack = new Stack<>(); //静态存储所有弹窗对象
    public PopupInfo popupInfo;
    protected PopupAnimator popupContentAnimator;
    protected ShadowBgAnimator shadowBgAnimator;
    private int touchSlop;
    public PopupStatus popupStatus = PopupStatus.Dismiss;
    private boolean isCreated = false;

    public BasePopupView(@NonNull Context context) {
        this(context, null);
    }

    public BasePopupView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BasePopupView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        shadowBgAnimator = new ShadowBgAnimator(this);
        //  添加Popup窗体内容View
        View contentView = LayoutInflater.from(context).inflate(getPopupLayoutId(), this, false);
        // 事先隐藏，等测量完毕恢复，避免View影子跳动现象。
        contentView.setAlpha(0);
        addView(contentView);
    }

    private View contentView;

    /**
     * 执行初始化
     */
    protected void init() {
        if (popupStatus == PopupStatus.Prepare || popupStatus == PopupStatus.Showing) return;
        popupStatus = PopupStatus.Prepare;
        contentView = ((Activity) getContext()).findViewById(android.R.id.content);
        //1. 初始化Popup
        if (!isCreated) {
            initPopupContent();
        }
        //apply size dynamic
        resetSize();
        if (!isCreated) {
            isCreated = true;
            onCreate();
            if (popupInfo.xPopupCallback != null) popupInfo.xPopupCallback.onCreated();
        }
        postDelayed(() -> {
            popupStatus = PopupStatus.Showing;
            applySize(false);
            View popupContentView = getPopupContentView();
            popupContentView.setAlpha(1f);
            setVisibility(VISIBLE);
            collectAnimator();
            if (popupInfo.xPopupCallback != null) popupInfo.xPopupCallback.beforeShow();
            doShowAnimation();
            doAfterShow();
            focusAndProcessBackPress();
        }, 50);
    }

    protected void resetSize() {
        XPopupUtils.setWidthHeight(getTargetSizeView(),
                (getMaxWidth() != 0 && getPopupWidth() > getMaxWidth()) ? getMaxWidth() : getPopupWidth(),
                (getMaxHeight() != 0 && getPopupHeight() > getMaxHeight()) ? getMaxHeight() : getPopupHeight()
        );
    }

    private boolean hasMoveUp = false;

    private void collectAnimator() {
        if (popupContentAnimator == null) {
            if (popupInfo.customAnimator != null) {
                popupContentAnimator = popupInfo.customAnimator;
                popupContentAnimator.targetView = getPopupContentView();
            } else {
                popupContentAnimator = genAnimatorByPopupType();
                if (popupContentAnimator == null) {
                    popupContentAnimator = getPopupAnimator();
                }
            }
            shadowBgAnimator.initAnimator();
            if (popupContentAnimator != null) {
                popupContentAnimator.initAnimator();
            }
        }
    }

    @Override
    public void onNavigationBarChange(boolean show) {
        if (!show) {
            applyFull();
        } else {
            applySize(true);
        }
    }

    protected void applyFull() {
        LayoutParams params = (LayoutParams) getLayoutParams();
        params.topMargin = 0;
        params.leftMargin = 0;
        params.bottomMargin = 0;
        params.rightMargin = 0;
        setLayoutParams(params);
    }

    protected void applySize(boolean isShowNavBar) {
        LayoutParams params = (LayoutParams) getLayoutParams();
        int rotation = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        boolean navBarVisible = XPopupUtils.isNavigationBarShow(getContext());
        boolean isNavBarShown = isShowNavBar || navBarVisible;
        if (rotation == 0) {
            params.leftMargin = 0;
            params.rightMargin = 0;
            params.bottomMargin = isNavBarShown ? XPopupUtils.getNavBarHeight(getContext()) : 0;
        } else if (rotation == 1) {
            params.bottomMargin = 0;
            params.rightMargin = isNavBarShown ? XPopupUtils.getNavBarHeight(getContext()) : 0;
            params.leftMargin = 0;
        } else if (rotation == 3) {
            params.bottomMargin = 0;
            params.leftMargin = 0;
            params.rightMargin = isNavBarShown ? XPopupUtils.getNavBarHeight(getContext()) : 0;
        }
        setLayoutParams(params);
    }

    protected boolean isBottom() {
        return false;
    }

    public BasePopupView show() {
        if (getParent() != null) return this;
        final Activity activity = (Activity) getContext();
        popupInfo.decorView = (ViewGroup) activity.getWindow().getDecorView();
        KeyboardUtils.registerSoftInputChangedListener(activity, this, new KeyboardUtils.OnSoftInputChangedListener() {
            @Override
            public void onSoftInputChanged(int height) {
                if (height == 0) { // 说明对话框隐藏
                    XPopupUtils.moveDown(BasePopupView.this);
                    hasMoveUp = false;
                } else {
                    //when show keyboard, move up
                    XPopupUtils.moveUpToKeyboard(height, BasePopupView.this);
                    hasMoveUp = true;
                }
            }
        });
        if (popupInfo.decorView != null) {
            popupInfo.decorView.post(() -> {
                if (getParent() != null) {
                    ((ViewGroup) getParent()).removeView(BasePopupView.this);
                }
                if (popupInfo.decorView != null) {
                    popupInfo.decorView.addView(BasePopupView.this, new LayoutParams(LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT));
                    init();
                }
            });
        }
        return this;
    }

    protected void doAfterShow() {
        removeCallbacks(doAfterShowTask);
        postDelayed(doAfterShowTask, getAnimationDuration());
    }

    private ShowSoftInputTask showSoftInputTask;

    public void focusAndProcessBackPress() {
        if (popupInfo.isRequestFocus) {
            setFocusableInTouchMode(true);
            requestFocus();
            if (!stack.contains(this)) stack.push(this);
        }
        // 此处焦点可能被内容的EditText抢走，也需要给EditText也设置返回按下监听
        setOnKeyListener(new BackPressListener());
        if (!popupInfo.autoFocusEditText) showSoftInput(this);

        //let all EditText can process back pressed.
        ArrayList<EditText> list = new ArrayList<>();
        XPopupUtils.findAllEditText(list, (ViewGroup) getPopupContentView());
        for (int i = 0; i < list.size(); i++) {
            final EditText et = list.get(i);
            et.setOnKeyListener(new BackPressListener());
            if (i == 0 && popupInfo.autoFocusEditText) {
                et.setFocusable(true);
                et.setFocusableInTouchMode(true);
                et.requestFocus();
                showSoftInput(et);
            }
        }
    }

    protected void showSoftInput(View focusView) {
        if (popupInfo.autoOpenSoftInput) {
            if (showSoftInputTask == null) {
                showSoftInputTask = new ShowSoftInputTask(focusView);
            } else {
                removeCallbacks(showSoftInputTask);
            }
            postDelayed(showSoftInputTask, 10);
        }
    }

    protected void dismissOrHideSoftInput() {
        if (KeyboardUtils.sDecorViewInvisibleHeightPre == 0)
            dismiss();
        else
            KeyboardUtils.hideSoftInput(BasePopupView.this);
    }

    class ShowSoftInputTask implements Runnable {
        View focusView;
        boolean isDone = false;

        public ShowSoftInputTask(View focusView) {
            this.focusView = focusView;
        }

        @Override
        public void run() {
            if (focusView != null && !isDone) {
                isDone = true;
                KeyboardUtils.showSoftInput(focusView);
            }
        }
    }

    class BackPressListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                if (popupInfo.isDismissOnBackPressed &&
                        (popupInfo.xPopupCallback == null || !popupInfo.xPopupCallback.onBackPressed()))
                    dismissOrHideSoftInput();
                return true;
            }
            return false;
        }
    }

    /**
     * 根据PopupInfo的popupAnimation字段来生成对应的内置的动画执行器
     */
    protected PopupAnimator genAnimatorByPopupType() {
        if (popupInfo == null || popupInfo.popupAnimation == null) return null;
        switch (popupInfo.popupAnimation) {
            case ScaleAlphaFromCenter:
            case ScaleAlphaFromLeftTop:
            case ScaleAlphaFromRightTop:
            case ScaleAlphaFromLeftBottom:
            case ScaleAlphaFromRightBottom:
                return new ScaleAlphaAnimator(getPopupContentView(), popupInfo.popupAnimation);

            case TranslateAlphaFromLeft:
            case TranslateAlphaFromTop:
            case TranslateAlphaFromRight:
            case TranslateAlphaFromBottom:
                return new TranslateAlphaAnimator(getPopupContentView(), popupInfo.popupAnimation);

            case TranslateFromLeft:
            case TranslateFromTop:
            case TranslateFromRight:
            case TranslateFromBottom:
                return new TranslateAnimator(getPopupContentView(), popupInfo.popupAnimation);

            case ScrollAlphaFromLeft:
            case ScrollAlphaFromLeftTop:
            case ScrollAlphaFromTop:
            case ScrollAlphaFromRightTop:
            case ScrollAlphaFromRight:
            case ScrollAlphaFromRightBottom:
            case ScrollAlphaFromBottom:
            case ScrollAlphaFromLeftBottom:
                return new ScrollScaleAnimator(getPopupContentView(), popupInfo.popupAnimation);

            case NoAnimation:
                return new EmptyAnimator();
        }
        return null;
    }

    protected abstract int getPopupLayoutId();

    /**
     * 如果你自己继承BasePopupView来做，这个不用实现
     *
     * @return
     */
    protected int getImplLayoutId() {
        return -1;
    }

    /**
     * 获取PopupAnimator，用于每种类型的PopupView自定义自己的动画器
     *
     * @return
     */
    protected PopupAnimator getPopupAnimator() {
        return null;
    }

    /**
     * 请使用onCreate，主要给弹窗内部用，不要去重写。
     */
    protected void initPopupContent() {
    }

    /**
     * do init.
     */
    protected void onCreate() {
    }

    /**
     * 执行显示动画：动画由2部分组成，一个是背景渐变动画，一个是Content的动画；
     * 背景动画由父类实现，Content由子类实现
     */
    protected void doShowAnimation() {
        if (popupInfo.hasShadowBg) {
            shadowBgAnimator.isZeroDuration = (popupInfo.popupAnimation == NoAnimation);
            shadowBgAnimator.animateShow();
        }
        if (popupContentAnimator != null)
            popupContentAnimator.animateShow();
    }

    /**
     * 执行消失动画：动画由2部分组成，一个是背景渐变动画，一个是Content的动画；
     * 背景动画由父类实现，Content由子类实现
     */
    protected void doDismissAnimation() {
        if (popupInfo.hasShadowBg) {
            shadowBgAnimator.animateDismiss();
        }
        if (popupContentAnimator != null)
            popupContentAnimator.animateDismiss();
    }

    /**
     * 获取内容View，本质上PopupView显示的内容都在这个View内部。
     * 而且我们对PopupView执行的动画，也是对它执行的动画
     *
     * @return
     */
    public View getPopupContentView() {
        return getChildAt(0);
    }

    public View getPopupImplView() {
        return ((ViewGroup) getPopupContentView()).getChildAt(0);
    }

    public int getAnimationDuration() {
        return popupInfo.popupAnimation == NoAnimation ? 10 : XPopup.getAnimationDuration();
    }

    /**
     * 弹窗的最大宽度，一般用来限制布局宽度为wrap或者match时的最大宽度
     *
     * @return
     */
    protected int getMaxWidth() {
        return 0;
    }

    /**
     * 弹窗的最大高度，一般用来限制布局高度为wrap或者match时的最大宽度
     *
     * @return
     */
    protected int getMaxHeight() {
        return popupInfo.maxHeight;
    }

    /**
     * 弹窗的宽度，用来动态设定当前弹窗的宽度，受getMaxWidth()限制
     *
     * @return
     */
    protected int getPopupWidth() {
        return 0;
    }

    /**
     * 弹窗的高度，用来动态设定当前弹窗的高度，受getMaxHeight()限制
     *
     * @return
     */
    protected int getPopupHeight() {
        return 0;
    }

    protected View getTargetSizeView() {
        return getPopupContentView();
    }


    private Runnable doAfterShowTask = new Runnable() {
        @Override
        public void run() {
            popupStatus = PopupStatus.Show;
            onShow();
            if (popupInfo != null && popupInfo.xPopupCallback != null)
                popupInfo.xPopupCallback.onShow();
            if (XPopupUtils.getDecorViewInvisibleHeight((Activity) getContext()) > 0 && !hasMoveUp) {
                XPopupUtils.moveUpToKeyboard(XPopupUtils.getDecorViewInvisibleHeight((Activity) getContext()), BasePopupView.this);
            }
        }
    };

    /**
     * 消失
     */
    public void dismiss() {
//        StackTraceElement[] stackElements = new Throwable().getStackTrace();
//        if (stackElements != null) {
//            for (StackTraceElement stackElement : stackElements) {
//                System.out.println("" + stackElement);
//            }
//        }
        if (popupStatus == PopupStatus.Dismissing || popupStatus == PopupStatus.Showing)
            return;
        if (popupStatus == PopupStatus.Dismiss) {
            if (popupInfo != null && popupInfo.xPopupCallback != null) {
                popupInfo.xPopupCallback.onDismiss();
            }
            return;
        }
        popupStatus = PopupStatus.Dismissing;
        if (popupInfo != null && popupInfo.xPopupCallback != null) {
            popupInfo.xPopupCallback.beforeDismiss();
        }
        if (popupInfo != null && popupInfo.autoOpenSoftInput) {
            KeyboardUtils.hideSoftInput(this);
        }
        clearFocus();
        doDismissAnimation();
        doAfterDismiss();
    }

    public void delayDismiss(long delay) {
        if (delay < 0) delay = 0;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, delay);
    }

    Runnable dismissWithRunnable;

    public void dismissWith(Runnable runnable) {
        this.dismissWithRunnable = runnable;
        dismiss();
    }

    public void delayDismissWith(long delay, Runnable runnable) {
        this.dismissWithRunnable = runnable;
        delayDismiss(delay);
    }

    public void doGoneWith(Runnable runnable) {
        shadowBgAnimator.cancel();
        popupContentAnimator.cancel();
        setVisibility(GONE);
        getPopupContentView().setAlpha(0f);
        dismissWithRunnable = runnable;
        removeCallbacks(doAfterDismissTask);
        post(doAfterDismissTask);
    }

    protected void doAfterDismiss() {
        if (popupInfo.autoOpenSoftInput) KeyboardUtils.hideSoftInput(this);
        removeCallbacks(doAfterDismissTask);
        postDelayed(doAfterDismissTask, getAnimationDuration());
    }

    private Runnable doAfterDismissTask = new Runnable() {
        @Override
        public void run() {
            popupStatus = PopupStatus.Dismiss;
            NavigationBarObserver.getInstance().removeOnNavigationBarListener(BasePopupView.this);
            if (!stack.isEmpty()) stack.pop();
            if (popupInfo != null && popupInfo.isRequestFocus) {
                if (!stack.isEmpty()) {
                    stack.get(stack.size() - 1).focusAndProcessBackPress();
                } else {
                    if (contentView != null) {
                        contentView.setFocusable(true);
                        contentView.setFocusableInTouchMode(true);
                    }
                }
            }
            if (popupInfo != null && popupInfo.decorView != null) {
                popupInfo.decorView.removeView(BasePopupView.this);
                KeyboardUtils.removeLayoutChangeListener(popupInfo.decorView, BasePopupView.this);
            }
            ViewParent parent = BasePopupView.this.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(BasePopupView.this);
            }
            onDismiss();
            if (popupInfo != null && popupInfo.xPopupCallback != null) {
                popupInfo.xPopupCallback.onDismiss();
            }
            if (dismissWithRunnable != null) {
                dismissWithRunnable.run();
                dismissWithRunnable = null;//no cache, avoid some bad edge effect.
            }
        }
    };

    public boolean isShow() {
        return popupStatus == PopupStatus.Show || popupStatus == PopupStatus.Dismissing;
    }

    public boolean isDismiss() {
        return popupStatus == PopupStatus.Dismiss;
    }

    public boolean isShowing() {
        return popupStatus == PopupStatus.Prepare || popupStatus == PopupStatus.Showing;
    }

    public void toggle() {
        if (popupStatus == PopupStatus.Dismissing || popupStatus == PopupStatus.Showing) {
            return;
        }
        if (popupStatus == PopupStatus.Dismiss) {
            show();
        } else {
            dismiss();
        }
    }

    /**
     * 消失动画执行完毕后执行
     */
    protected void onDismiss() {
    }

    /**
     * 显示动画执行完毕后执行
     */
    protected void onShow() {
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stack.clear();
        removeCallbacks(doAfterShowTask);
        removeCallbacks(doAfterDismissTask);
        KeyboardUtils.removeLayoutChangeListener(popupInfo.decorView, BasePopupView.this);
        if (showSoftInputTask != null) removeCallbacks(showSoftInputTask);
        popupStatus = PopupStatus.Dismiss;
        showSoftInputTask = null;
        hasMoveUp = false;
    }

    private float x, y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 如果自己接触到了点击，并且不在PopupContentView范围内点击，则进行判断是否是点击事件,如果是，则dismiss
        Rect rect = new Rect();
        getPopupContentView().getGlobalVisibleRect(rect);
        if (!XPopupUtils.isInRect(event.getX(), event.getY(), rect)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = event.getX();
                    y = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    float dx = event.getX() - x;
                    float dy = event.getY() - y;
                    float distance = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                    if (distance < touchSlop && popupInfo.isDismissOnTouchOutside) {
                        dismiss();
                    }
                    x = 0;
                    y = 0;
                    break;
            }
        }
        return true;
    }
}

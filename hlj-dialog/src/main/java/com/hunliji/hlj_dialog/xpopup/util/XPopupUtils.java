package com.hunliji.hlj_dialog.xpopup.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.hunliji.hlj_dialog.xpopup.core.AttachPopupView;
import com.hunliji.hlj_dialog.xpopup.core.BasePopupView;
import com.hunliji.hlj_dialog.xpopup.core.BottomPopupView;
import com.hunliji.hlj_dialog.xpopup.core.CenterPopupView;
import com.hunliji.hlj_dialog.xpopup.core.PartShadowPopupView;
import com.hunliji.hlj_dialog.xpopup.core.PositionPopupView;

import java.util.ArrayList;

/**
 * Description:
 * Create by lxj, at 2018/12/7
 */
public class XPopupUtils {
    public static int getWindowWidth(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
    }

    public static int getWindowHeight(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
    }

    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int getStatusBarHeight() {
        Resources resources = Resources.getSystem();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * Return the navigation bar's height.
     *
     * @param context
     * @return the navigation bar's height
     */
    public static int getNavBarHeight(Context context) {
        if (!(context instanceof Activity)) {
            return 0;
        }
        ViewGroup decorView = (ViewGroup) ((Activity) context).getWindow().getDecorView();
        for (int i = 0, count = decorView.getChildCount(); i < count; i++) {
            final View child = decorView.getChildAt(i);
            final int id = child.getId();
            if (id != View.NO_ID) {
                String resourceEntryName = context
                        .getResources()
                        .getResourceEntryName(id);
                if ("navigationBarBackground".equals(resourceEntryName)
                        && child.getVisibility() == View.VISIBLE) {
                    return child.getHeight();
                }
            }
        }
        return 0;
    }

    public static void setWidthHeight(View target, int width, int height) {
        if (width <= 0 && height <= 0) return;
        ViewGroup.LayoutParams params = target.getLayoutParams();
        if (width > 0) params.width = width;
        if (height > 0) params.height = height;
        target.setLayoutParams(params);
    }

    public static void applyPopupSize(ViewGroup content, int maxWidth, int maxHeight) {
        applyPopupSize(content, maxWidth, maxHeight, null);
    }

    public static void applyPopupSize(final ViewGroup content, final int maxWidth, final int maxHeight, final Runnable afterApplySize) {
        content.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = content.getLayoutParams();
                View implView = content.getChildAt(0);
                ViewGroup.LayoutParams implParams = implView.getLayoutParams();
                // 假设默认Content宽是match，高是wrap
                int w = content.getMeasuredWidth();
                // response impl view wrap_content params.
                if (implParams.width == FrameLayout.LayoutParams.WRAP_CONTENT) {
//                    w = Math.min(w, implView.getMeasuredWidth());
                }
                if (maxWidth != 0) {
                    params.width = Math.min(w, maxWidth);
                }
                int h = content.getMeasuredHeight();
                // response impl view match_parent params.
                if (implParams.height == FrameLayout.LayoutParams.MATCH_PARENT) {
                    h = ((ViewGroup) content.getParent()).getMeasuredHeight();
                    params.height = h;
                }
                if (maxHeight != 0) {
                    // 如果content的高为match，则maxHeight限制impl
                    if (params.height == FrameLayout.LayoutParams.MATCH_PARENT ||
                            params.height == (getWindowHeight(content.getContext()) + getStatusBarHeight())) {
                        implParams.height = Math.min(implView.getMeasuredHeight(), maxHeight);
                        implView.setLayoutParams(implParams);
                    } else {
                        params.height = Math.min(h, maxHeight);
                    }
                }
                content.setLayoutParams(params);
                if (afterApplySize != null) {
                    afterApplySize.run();
                }
            }
        });
    }

    public static void setCursorDrawableColor(EditText et, int color) {
        //暂时没有找到有效的方法来动态设置cursor的颜色
    }

    public static BitmapDrawable createBitmapDrawable(Resources resources, int width, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, 20, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(0, 0, bitmap.getWidth(), 4, paint);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawRect(0, 4, bitmap.getWidth(), 20, paint);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, bitmap);
        bitmapDrawable.setGravity(Gravity.BOTTOM);
        return bitmapDrawable;
    }

    public static StateListDrawable createSelector(Drawable defaultDrawable, Drawable focusDrawable) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, focusDrawable);
        stateListDrawable.addState(new int[]{}, defaultDrawable);
        return stateListDrawable;
    }

    public static boolean isInRect(float x, float y, Rect rect) {
        return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom;
    }

    /**
     * Return whether soft input is visible.
     *
     * @param activity The activity.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isSoftInputVisible(final Activity activity) {
        return getDecorViewInvisibleHeight(activity) > 0;
    }

    private static int sDecorViewDelta = 0;

    public static int getDecorViewInvisibleHeight(final Activity activity) {
        final View decorView = activity.getWindow().getDecorView();
        final Rect outRect = new Rect();
        decorView.getWindowVisibleDisplayFrame(outRect);
        int delta = Math.abs(decorView.getBottom() - outRect.bottom);
        if (delta <= getNavBarHeight(activity)) {
            sDecorViewDelta = delta;
            return 0;
        }
        return delta - sDecorViewDelta;
    }

    public static void moveUpToKeyboard(int keyboardHeight, BasePopupView pv) {
        if (!pv.popupInfo.isMoveUpToKeyboard) return;
        if (pv instanceof PositionPopupView) return;
        //判断是否盖住输入框
        ArrayList<EditText> allEts = new ArrayList<>();
        findAllEditText(allEts, pv);
        EditText focusEt = null;
        for (EditText et : allEts) {
            if (et.isFocused()) {
                focusEt = et;
                break;
            }
        }
        int dy = 0;
        int popupHeight = pv.getPopupContentView().getHeight();
        int popupWidth = pv.getPopupContentView().getWidth();
        if (pv.getPopupImplView() != null) {
            popupHeight = Math.min(popupHeight, pv.getPopupImplView().getMeasuredHeight());
            popupWidth = Math.min(popupWidth, pv.getPopupImplView().getMeasuredWidth());
        }
        int windowHeight = getWindowHeight(pv.getContext());
        int focusEtTop = 0;
        int focusBottom = 0;
        if (focusEt != null) {
            int[] locations = new int[2];
            focusEt.getLocationInWindow(locations);
            focusEtTop = locations[1];
            focusBottom = focusEtTop + focusEt.getMeasuredHeight();
        }
        //暂时忽略PartShadow弹窗和AttachPopupView
        if (!(pv instanceof PartShadowPopupView) && pv instanceof AttachPopupView) return;
        //执行上移
        if ((popupWidth == XPopupUtils.getWindowWidth(pv.getContext()) &&
                popupHeight == (XPopupUtils.getWindowHeight(pv.getContext()) + XPopupUtils.getStatusBarHeight()))
        ) {
            // 如果是全屏弹窗，特殊处理，只要输入框没被盖住，就不移动。
            if (focusBottom + keyboardHeight < windowHeight) {
                return;
            }
        }
        if (pv instanceof CenterPopupView) {
            int targetY = keyboardHeight - (windowHeight - popupHeight + getStatusBarHeight()) / 2;

            if (focusEt != null && focusEtTop - targetY < 0) {
                targetY += focusEtTop - targetY - getStatusBarHeight();
            }
            dy = Math.max(0, targetY);
        } else if (pv instanceof BottomPopupView) {
            dy = keyboardHeight;
            if (focusEt != null && focusEtTop - dy < 0) {
                dy += focusEtTop - dy - getStatusBarHeight();
            }
        } else if (isBottomPartShadow(pv)) {
            int overflowHeight = (focusBottom + keyboardHeight) - windowHeight;
            if (focusEt != null && overflowHeight > 0) {
                dy = overflowHeight;
            }
        } else if (isTopPartShadow(pv)) {
            int overflowHeight = (focusBottom + keyboardHeight) - windowHeight;
            if (focusEt != null && overflowHeight > 0) {
                dy = overflowHeight;
            }
            if (dy != 0) {
                pv.getPopupImplView().animate().translationY(-dy)
                        .setDuration(200)
                        .setInterpolator(new OvershootInterpolator(0))
                        .start();
            }
            return;
        }
        //dy=0说明没有触发移动，有些弹窗有translationY，不能影响它们
        if (dy == 0 && pv.getPopupContentView().getTranslationY() != 0) return;
        Log.e("test", "translate");
        pv.getPopupContentView().animate().translationY(-dy)
                .setDuration(200)
                .setInterpolator(new OvershootInterpolator(0))
                .start();
    }

    private static boolean isBottomPartShadow(BasePopupView pv) {
        return pv instanceof PartShadowPopupView && ((PartShadowPopupView) pv).isShowUp;
    }

    private static boolean isTopPartShadow(BasePopupView pv) {
        return pv instanceof PartShadowPopupView && !((PartShadowPopupView) pv).isShowUp;
    }

    public static void moveDown(BasePopupView pv) {
        //暂时忽略PartShadow弹窗和AttachPopupView
        if (pv instanceof PositionPopupView) return;
        if (!(pv instanceof PartShadowPopupView) && pv instanceof AttachPopupView) return;
        if (pv instanceof PartShadowPopupView && !isBottomPartShadow(pv)) {
            pv.getPopupImplView().animate().translationY(0)
                    .setInterpolator(new OvershootInterpolator(0))
                    .setDuration(200).start();
        } else {
            pv.getPopupContentView().animate().translationY(0)
                    .setInterpolator(new OvershootInterpolator(0))
                    .setDuration(200).start();

        }
    }


    /**
     * Return whether the navigation bar visible.
     * <p>Call it in onWindowFocusChanged will get right result.</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isNavBarVisible(Context context) {
        boolean isVisible = false;
        ViewGroup decorView = (ViewGroup) ((Activity) context).getWindow().getDecorView();
        for (int i = 0, count = decorView.getChildCount(); i < count; i++) {
            final View child = decorView.getChildAt(i);
            final int id = child.getId();
            if (id != View.NO_ID) {
                String resourceEntryName = context
                        .getResources()
                        .getResourceEntryName(id);
                if ("navigationBarBackground".equals(resourceEntryName)
                        && child.getVisibility() == View.VISIBLE) {
                    isVisible = true;
                    break;
                }
            }
        }
        if (isVisible) {
            int visibility = decorView.getSystemUiVisibility();
            isVisible = (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
        }
        return isVisible;
    }

    public static boolean isNavigationBarShow(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && context instanceof Activity) {
            Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !menu && !back;
        }
    }

    public static void findAllEditText(ArrayList<EditText> list, ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View v = group.getChildAt(i);
            if (v instanceof EditText && v.getVisibility() == View.VISIBLE) {
                list.add((EditText) v);
            } else if (v instanceof ViewGroup) {
                findAllEditText(list, (ViewGroup) v);
            }
        }
    }

    private static Context mContext;


}

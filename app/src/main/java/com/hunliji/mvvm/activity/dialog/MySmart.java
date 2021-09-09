package com.hunliji.mvvm.activity.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.hunliji.mvvm.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;


/**
 * @author kou_zhong
 * @date 2020/9/15
 */
public class MySmart extends SmartRefreshLayout {

    public MySmart(Context context) {
        super(context);
    }

    public MySmart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final View thisView = this;
        final int paddingLeft = thisView.getPaddingLeft();
        final int paddingTop = thisView.getPaddingTop();
        final int paddingBottom = thisView.getPaddingBottom();

        for (int i = 0, len = super.getChildCount(); i < len; i++) {
            View child = super.getChildAt(i);

            if (child.getVisibility() == GONE || child.getTag(R.string.srl_component_falsify) == child) {
                continue;
            }

            if (mRefreshContent != null && mRefreshContent.getView() == child) {
                boolean isPreviewMode = thisView.isInEditMode() && mEnablePreviewInEditMode && isEnableRefreshOrLoadMore(mEnableRefresh) && mRefreshHeader != null;
                final View contentView = mRefreshContent.getView();
                final ViewGroup.LayoutParams lp = contentView.getLayoutParams();
                final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams) lp : sDefaultMarginLP;
                int left = paddingLeft + mlp.leftMargin;
                int top = paddingTop + mlp.topMargin;
                int right = left + contentView.getMeasuredWidth();
                int bottom = top + contentView.getMeasuredHeight();
                if (isPreviewMode && (isEnableTranslationContent(mEnableHeaderTranslationContent, mRefreshHeader))) {
                    top = top + mHeaderHeight;
                    bottom = bottom + mHeaderHeight;
                }

                contentView.layout(left, top, right, bottom);
            }
            if (mRefreshHeader != null && mRefreshHeader.getView() == child) {
                boolean isPreviewMode = thisView.isInEditMode() && mEnablePreviewInEditMode && isEnableRefreshOrLoadMore(mEnableRefresh);
                final View headerView = mRefreshHeader.getView();
                final ViewGroup.LayoutParams lp = headerView.getLayoutParams();
                final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams) lp : sDefaultMarginLP;
                int left = mlp.leftMargin;
                int top = mlp.topMargin + mHeaderInsetStart;
                int right = left + headerView.getMeasuredWidth();
                int bottom = top + headerView.getMeasuredHeight();
                if (!isPreviewMode) {
                    if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Translate) {
                        top = top - mHeaderHeight;
                        bottom = bottom - mHeaderHeight;
                    }
                }
                headerView.layout(left, top, right, bottom);
            }
            if (mRefreshFooter != null && mRefreshFooter.getView() == child) {
                final boolean isPreviewMode = thisView.isInEditMode() && mEnablePreviewInEditMode && isEnableRefreshOrLoadMore(mEnableLoadMore);
                final View footerView = mRefreshFooter.getView();
                final ViewGroup.LayoutParams lp = footerView.getLayoutParams();
                final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams) lp : sDefaultMarginLP;
                final SpinnerStyle style = mRefreshFooter.getSpinnerStyle();
                int left = mlp.leftMargin;
                int top = mlp.topMargin + thisView.getMeasuredHeight() - mFooterInsetStart;
                if (mFooterNoMoreData && mFooterNoMoreDataEffective && mEnableFooterFollowWhenNoMoreData && mRefreshContent != null
                        && mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Translate
                        && isEnableRefreshOrLoadMore(mEnableLoadMore)) {
                    final View contentView = mRefreshContent.getView();
                    final ViewGroup.LayoutParams clp = contentView.getLayoutParams();
                    final int topMargin = clp instanceof MarginLayoutParams ? ((MarginLayoutParams) clp).topMargin : 0;
                    RecyclerView recyclerView = null;
                    if (contentView instanceof ViewGroup) {
                        int childCount = ((ViewGroup) contentView).getChildCount();
                        if (childCount > 0) {
                            for (int i1 = 0; i1 < childCount; i1++) {
                                View childAt = ((ViewGroup) contentView).getChildAt(i1);
                                if (childAt instanceof RecyclerView) {
                                    recyclerView = (RecyclerView) childAt;
                                    break;
                                }
                            }
                        }
                    }
                    int height;
                    if (recyclerView == null) {
                        height = contentView.getMeasuredHeight();
                    } else {
                        height = recyclerView.getMeasuredHeight();
                    }
                    top = paddingTop + paddingTop + topMargin + height;
                    Log.e("move", "inininin:" + contentView.getMeasuredHeight());
                }
                if (style == SpinnerStyle.MatchLayout) {
                    top = mlp.topMargin - mFooterInsetStart;
                } else if (isPreviewMode
                        || style == SpinnerStyle.FixedFront
                        || style == SpinnerStyle.FixedBehind) {
                    top = top - mFooterHeight;
                } else if (style.scale && mSpinner < 0) {
                    top = top - Math.max(isEnableRefreshOrLoadMore(mEnableLoadMore) ? -mSpinner : 0, 0);
                }

                int right = left + footerView.getMeasuredWidth();
                int bottom = top + footerView.getMeasuredHeight();
                footerView.layout(left, top, right, bottom);
            }
        }
    }
}

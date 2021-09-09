package com.hunliji.mvvm.activity.dialog.test

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.hunliji.mvvm.R
import kotlinx.android.synthetic.main.module_lib_widget_filter_item_view.view.*

/**
 * FilterView
 *
 * @author kou_zhong
 * @date 2020/10/29
 */
class FilterItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr), FilterItemInterface {

    init {
        View.inflate(context, R.layout.module_lib_widget_filter_item_view, this)
    }

    private var default: String = "排序"
    private var isShow = false
    override fun setDefault(default: String) {
        this.default = default
        widget_lib_tv_sort.text = default
    }

    override fun onShow() {
        if (isShow) return
        isShow = true
        widget_lib_tv_sort.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        iv_price.setImageResource(R.drawable.icon_arrow_up_primary_d_18_9)
    }

    override fun onHideUnSelect() {
        isShow = false
        widget_lib_tv_sort.text = default
        widget_lib_tv_sort.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
        iv_price.setImageResource(R.drawable.icon_arrow_down_black3_18_9)
    }

    override fun onHideSelect(text: String) {
        isShow = false
        widget_lib_tv_sort.text = text
        widget_lib_tv_sort.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        iv_price.setImageResource(R.drawable.icon_arrow_down_primary_d_18_9)
    }
}
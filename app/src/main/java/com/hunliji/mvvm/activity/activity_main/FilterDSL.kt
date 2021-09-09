package com.hunliji.mvvm.activity.activity_main

import android.view.ViewGroup
import android.widget.LinearLayout
import com.hunliji.mvvm.activity.dialog.test.FilterItemView

/**
 * FilterDSL
 *
 * @author kou_zhong
 * @date 2020/11/3
 */
fun LinearLayout.property(
    parentWidth: Int,
    defaults: List<String>
): List<FilterItemView> {
    val parent = this
    val itemWidth = parentWidth / defaults.size
    return defaults.map {
        val filterItemView = FilterItemView(context)
        filterItemView.setDefault(it)
        parent.addView(filterItemView, ViewGroup.LayoutParams(itemWidth, -1))
        filterItemView
    }
}

fun FilterItemView.onDismiss(hasSelect: Boolean, selectText: String) {
    val itemView = this
    if (hasSelect) {
        itemView.onHideSelect(selectText)
    } else {
        itemView.onHideUnSelect()
    }
}


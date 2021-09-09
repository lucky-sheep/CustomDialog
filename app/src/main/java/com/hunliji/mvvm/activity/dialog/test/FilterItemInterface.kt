package com.hunliji.mvvm.activity.dialog.test

/**
 * FilterItemInterface
 *
 * @author kou_zhong
 * @date 2020/10/29
 */
interface FilterItemInterface {

    fun setDefault(default: String)

    fun onShow()

    fun onHideUnSelect()

    fun onHideSelect(text: String)
}
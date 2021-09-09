package com.hunliji.mvvm.binding

/**
 * RefreshPresenter
 *
 * @author wm
 * @date 19-9-3
 */
interface RefreshPresenter {
    fun loadData(isNormal: Boolean = false,isRefresh: Boolean)
}

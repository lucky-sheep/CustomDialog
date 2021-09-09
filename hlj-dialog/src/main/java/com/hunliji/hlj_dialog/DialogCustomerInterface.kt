package com.hunliji.hlj_dialog

import android.app.Dialog
import android.util.Log
import android.view.View
import com.hunliji.hlj_dialog.model.DialogRequestBuilder
import kotlinx.coroutines.*

/**
 * DialogCustomerInterface
 *
 * @author wm
 * @date 20-3-16
 */
abstract class DialogCustomerInterface<T> : CoroutineScope by MainScope() {

    abstract fun getLayoutId(): Int

    abstract fun onCreate(dialog: Dialog)

    open fun onShow() {

    }

    abstract fun onDismiss()

    open fun confirmView(): View? = null

    open fun cancelView(): View? = null

    open fun confirmViewId(): Int = 0

    open fun cancelViewId(): Int = 0

    open fun onConfirmResult(): T? = null

    open fun showLoading() {

    }

    open fun hideLoading() {

    }

    fun <T> requestMix(
        block: suspend CoroutineScope.() -> T,
        build: DialogRequestBuilder<T>.() -> Unit = {}
    ): Job = launch {
        val builder = DialogRequestBuilder<T>().also(build)
        showLoading()
        try {
            val data = withContext(Dispatchers.IO) { block() }
            hideLoading()
            builder.getSuccess()?.invoke(data)
        } catch (e: Exception) {
            hideLoading()
            builder.getError()?.invoke(e)
            Log.e("dialog_request", e.message)
        }
    }
}

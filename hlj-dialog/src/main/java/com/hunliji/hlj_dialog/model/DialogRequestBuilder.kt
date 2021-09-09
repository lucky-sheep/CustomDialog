package com.hunliji.hlj_dialog.model

/**
 * RequestBuilder
 *
 * @author wm
 * @date 20-2-25
 */
class DialogRequestBuilder<T> {
    private var success: ((T) -> Unit)? = null
    private var error: ((Throwable) -> Unit)? = null

    fun success(method: (T) -> Unit = {}) {
        success = method
    }

    fun getSuccess(): ((T) -> Unit)? {
        return success
    }

    fun error(method: (Throwable) -> Unit = {}) {
        error = method
    }

    fun getError(): ((Throwable) -> Unit)? {
        return error
    }
}

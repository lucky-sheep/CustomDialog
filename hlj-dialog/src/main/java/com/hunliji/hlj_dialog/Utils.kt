package com.hunliji.hlj_dialog

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Point
import android.util.TypedValue
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.hunliji.ext_master.toolbarHeight
import com.hunliji.hlj_dialog.core.app

/**
 * Utils
 *
 * @author wm
 * @date 20-3-16
 */
internal val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), app.resources.displayMetrics
    ).toInt()

internal fun Context.findActivity(): Activity? {
    val context = this
    if (context is Activity) {
        return context
    }
    return if (context is ContextWrapper) {
        context.baseContext.findActivity()
    } else {
        null
    }
}



package com.hunliji.hlj_dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hunliji.hlj_dialog.model.*
import com.hunliji.hlj_dialog.xpopup.XPopup
import com.hunliji.hlj_dialog.xpopup.core.BasePopupView
import com.hunliji.hlj_dialog.xpopup.core.BottomPopupView
import com.hunliji.hlj_dialog.xpopup.core.PartShadowPopupView
import com.hunliji.hlj_dialog.xpopup.interfaces.SimpleCallback
import com.hunliji.hlj_dialog.xpopup.widget.MaxHeightLinearLayout
import com.xueyu.kotlinextlibrary.color
import com.xueyu.kotlinextlibrary.toolbarHeight

/**
 * DialogHelper
 *
 * @author wm
 * @date 20-3-16
 */
fun Fragment.showDialogSample(
    content: CharSequence,
    init: SampleDialogBuilder.() -> Unit
) = requireContext().showDialogSample(content, init)

fun Context.showDialogSample(
    content: CharSequence,
    init: SampleDialogBuilder.() -> Unit
): Dialog {
    val builder = SampleDialogBuilder().also(init)
    val dialog = Dialog(this, R.style.Dialog_lib_theme)
    dialog.setContentView(R.layout.dialgo_lib_sample)
    setContent(dialog, content, builder)
    builder.getExtra()?.invoke(dialog)
    setTitle(dialog, builder)
    setBtn(dialog, builder)
    dialog.setCanceledOnTouchOutside(builder.getCanCancelOutSide())
    dialog.setCancelable(builder.getCancelAble())
    dialog.setOnDismissListener {
        builder.getDismiss()?.invoke(dialog)
    }
    val mainView = dialog.findViewById<MaxHeightLinearLayout>(R.id.dialog_lib_main)
    setMain(mainView, builder)
    dialog.window?.let { window ->
        val params = window.attributes
        params.width = builder.getWidth()
        val height = builder.getHeight()
        if (height != 0) {
            params.height = height
        }
        window.attributes = params
        window.setGravity(Gravity.CENTER)
        window.setWindowAnimations(R.style.DialogAnimCenter)
        window.setDimAmount(0.5f)
        if (!builder.getShowBehind()) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
    }
    dialog.showSafe()
    return dialog
}

private fun setContent(dialog: Dialog, content: CharSequence, builder: SampleDialogBuilder) {
    val contentView = dialog.findViewById<TextView>(R.id.dialog_lib_content)
    contentView.text = content
    contentView.setTextColor(builder.getContentColor())
    if (TextUtils.isEmpty(builder.getTitle())) {
        contentView.minHeight = 44.dp
    }
}

private fun setTitle(dialog: Dialog, builder: SampleDialogBuilder) {
    val titleView = dialog.findViewById<TextView>(R.id.dialog_lib_title)
    val title = builder.getTitle()
    if (TextUtils.isEmpty(title)) {
        titleView.visibility = View.GONE
    } else {
        titleView.visibility = View.VISIBLE
        titleView.text = title
        titleView.setTextColor(builder.getTitleColor())
    }
}

private fun setMain(mainView: MaxHeightLinearLayout, builder: SampleDialogBuilder) {
    mainView.background = GradientDrawable().also {
        it.cornerRadius = builder.getCorner().toFloat()
        it.setColor(Color.parseColor("#ffffff"))
    }
    mainView.setMaxHeight(builder.getMaxHeight())
}

private fun setBtn(dialog: Dialog, builder: SampleDialogBuilder) {
    val isSingle = builder.getIsSingle()
    val autoDismiss = builder.getAutoDismiss()
    val confirm = dialog.findViewById<TextView>(R.id.dialog_lib_confirm)
    confirm.setOnClickListener {
        builder.getConfirm()?.invoke(dialog)
        if (autoDismiss) {
            dialog.dismiss()
        }
    }
    confirm.text = builder.getConfirmText()
    confirm.setTextColor(builder.getConfirmColor())
    val dividerView = dialog.findViewById<View>(R.id.dialog_lib_divider)
    val cancel = dialog.findViewById<TextView>(R.id.dialog_lib_cancel)
    if (isSingle) {
        confirm.background = createBg(
            floatArrayOf(
                0f, 0f, 0f, 0f,
                builder.getCorner().toFloat(), builder.getCorner().toFloat(),
                builder.getCorner().toFloat(), builder.getCorner().toFloat()
            )
        )
        dividerView.visibility = View.GONE
        cancel.visibility = View.GONE
    } else {
        confirm.background = createBg(
            floatArrayOf(
                0f, 0f, 0f, 0f,
                builder.getCorner().toFloat(), builder.getCorner().toFloat(), 0f, 0f
            )
        )
        dividerView.visibility = View.VISIBLE
        cancel.visibility = View.VISIBLE
        cancel.text = builder.getCancelText()
        cancel.setTextColor(builder.getCancelColor())
        cancel.setOnClickListener {
            builder.getCancel()?.invoke(dialog)
            if (autoDismiss) {
                dialog.dismiss()
            }
        }
        cancel.background = createBg(
            floatArrayOf(
                0f, 0f, 0f, 0f, 0f, 0f,
                builder.getCorner().toFloat(), builder.getCorner().toFloat()
            )
        )
    }
}

private fun createBg(cornerRadii: FloatArray): Drawable {
    val press = GradientDrawable().also {
        it.cornerRadii = cornerRadii
        it.setColor(color(R.color.pressButtonBackground))
    }
    val normal = GradientDrawable().also {
        it.cornerRadii = cornerRadii
        it.setColor(color(R.color.normalButtonBackground))
    }
    return StateListDrawable().also {
        val pressed = android.R.attr.state_pressed
        it.addState(intArrayOf(-pressed), normal)
        it.addState(intArrayOf(pressed), press)
        it.addState(intArrayOf(), normal)
    }
}

fun Context.showDialogSampleCustomer(
    layoutId: Int,
    init: SampleCustomerDialogBuilder.() -> Unit
) {
    val builder = SampleCustomerDialogBuilder().also(init)
    val autoDismiss = builder.getAutoDismiss()
    val dialog = Dialog(this, R.style.Dialog_lib_theme)
    dialog.setContentView(layoutId)
    builder.getConvert()?.invoke(dialog)
    val confirmView = if (builder.getConfirmId() == 0) null else
        try {
            dialog.findViewById<View>(builder.getConfirmId())
        } catch (e: Exception) {
            null
        }
    confirmView?.setOnClickListener {
        builder.getConfirm()?.invoke(dialog)
        if (autoDismiss) {
            dialog.dismiss()
        }
    }
    val cancelView = if (builder.getCancelId() == 0) null else
        try {
            dialog.findViewById<View>(builder.getCancelId())
        } catch (e: Exception) {
            null
        }
    cancelView?.setOnClickListener {
        builder.getCancel()?.invoke(dialog)
        if (autoDismiss) {
            dialog.dismiss()
        }
    }
    dialog.setCanceledOnTouchOutside(builder.getCanCancelOutSide())
    dialog.setCancelable(builder.getCancelAble())
    dialog.setOnDismissListener {
        this.findActivity()?.window?.decorView?.clearFocus()
        builder.getDismiss()?.invoke(dialog)
    }
    val dialogType = builder.getDialogType()
    dialog.window?.let { window ->
        val params = window.attributes
        params.width = builder.getWidth()
        val height = builder.getHeight()
        if (height != 0) {
            params.height = height
        }
        window.attributes = params
        if (dialogType == DialogType.CENTER) {
            window.setGravity(Gravity.CENTER)
            when (builder.getDialogAnimType()) {
                DialogAnimType.BOTTOM -> window.setWindowAnimations(R.style.DialogAnimBottom)
                DialogAnimType.CENTER -> window.setWindowAnimations(R.style.DialogAnimCenter)
                else -> window.setWindowAnimations(R.style.DialogAnimCenter)
            }
        } else {
            window.setGravity(Gravity.BOTTOM)
            window.setWindowAnimations(R.style.DialogAnimBottom)
        }
        if (!builder.getShowBehind()) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
    }
    dialog.showSafe()
}

fun <T> Context.showDialogCustomer(
    dialogInfo: DialogCustomerInterface<T>,
    init: CustomerDialogBuilder<T>.() -> Unit
): Dialog {
    val builder = CustomerDialogBuilder<T>().also(init)
    val autoDismiss = builder.getAutoDismiss()
    val dialog = Dialog(this, R.style.Dialog_lib_theme)
    dialog.setContentView(dialogInfo.getLayoutId())
    dialogInfo.onCreate(dialog)
    val confirmView = dialogInfo.confirmView() ?: if (dialogInfo.confirmViewId() == 0) null else
        try {
            dialog.findViewById<View>(dialogInfo.confirmViewId())
        } catch (e: Exception) {
            null
        }
    confirmView?.setOnClickListener {
        builder.getConfirm()?.invoke(dialog, dialogInfo.onConfirmResult())
        if (autoDismiss) {
            dialog.dismiss()
        }
    }
    val cancelView = dialogInfo.cancelView() ?: if (dialogInfo.cancelViewId() == 0) null else
        try {
            dialog.findViewById<View>(dialogInfo.cancelViewId())
        } catch (e: Exception) {
            null
        }
    cancelView?.setOnClickListener {
        builder.getCancel()?.invoke(dialog)
        if (autoDismiss) {
            dialog.dismiss()
        }
    }
    dialog.setCanceledOnTouchOutside(builder.getCanCancelOutSide())
    dialog.setCancelable(builder.getCancelAble())
    dialog.setOnShowListener {
        dialogInfo.onShow()
    }
    dialog.setOnDismissListener {
        dialogInfo.onDismiss()
        builder.getDismiss()?.invoke(dialog, dialogInfo.onConfirmResult())
    }
    val dialogType = builder.getDialogType()
    dialog.window?.let { window ->
        val params = window.attributes
        params.width = builder.getWidth()
        val height = builder.getHeight()
        if (height != 0) {
            params.height = height
        }
        window.attributes = params
        if (dialogType == DialogType.CENTER) {
            window.setGravity(Gravity.CENTER)
            when (builder.getDialogAnimType()) {
                DialogAnimType.BOTTOM -> window.setWindowAnimations(R.style.DialogAnimBottom)
                DialogAnimType.CENTER -> window.setWindowAnimations(R.style.DialogAnimCenter)
                else -> window.setWindowAnimations(R.style.DialogAnimCenter)
            }
        } else {
            window.setGravity(Gravity.BOTTOM)
            window.setWindowAnimations(R.style.DialogAnimBottom)
        }
        if (!builder.getShowBehind()) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
    }
    dialog.showSafe()
    return dialog
}

fun Context.showDialogBottom(
    popUp: BottomPopupView,
    init: (XPopBuilder.() -> Unit)? = null
) {
    val builder = if (init != null) XPopBuilder().also(init) else null
    val showBg = builder == null || builder.getShowBehind()
    XPopup.Builder(this)
        .hasShadowBg(showBg)
        .dismissOnTouchOutside(builder?.getDismissOnTouchOutSide() ?: true)
        .moveUpToKeyboard(false)
        .setPopupCallback(object : SimpleCallback() {
            override fun beforeShow() {
                builder?.getBeforeShow()?.invoke()
            }

            override fun beforeDismiss() {
                builder?.getBeforeDismiss()?.invoke()
            }

            override fun onShow() {
                builder?.getShow()?.invoke()
            }

            override fun onDismiss() {
                builder?.getDismiss()?.invoke()
            }
        })
        .asCustom(popUp)
        .show()
}

fun Context.createDialogPartShadow(
    popUp: PartShadowPopupView,
    v: View,
    init: (XPopBuilder.() -> Unit)? = null
): BasePopupView {
    val builder = if (init != null) XPopBuilder().also(init) else null
    val showBg = builder == null || builder.getShowBehind()
    return XPopup.Builder(this)
        .atView(v)
        .autoFocusEditText(false)
        .autoOpenSoftInput(false)
        .hasShadowBg(showBg)
        .setPopupCallback(object : SimpleCallback() {
            override fun beforeShow() {
                builder?.getBeforeShow()?.invoke()
            }

            override fun beforeDismiss() {
                builder?.getBeforeDismiss()?.invoke()
            }

            override fun onShow() {
                builder?.getShow()?.invoke()
            }

            override fun onDismiss() {
                builder?.getDismiss()?.invoke()
            }
        })
        .asCustom(popUp)
}

private var time = 0L

fun BasePopupView.showToggle(vararg views: BasePopupView) {
    val currentTimeMillis = System.currentTimeMillis()
    if (currentTimeMillis - time < 200) {
        return
    }
    time = currentTimeMillis
    if (views.isEmpty()) {
        show()
    } else {
        val showingPop = views.filter { pop ->
            pop.isShowing
        }
        if (showingPop.isNotEmpty()) {
            return
        }
        val show = views.filter { pop ->
            pop.isShow
        }
        val showExceptSelf = views.filter { pop ->
            pop.isShow && pop != this
        }
        if (show.isEmpty()) {
            show()
        } else if (showExceptSelf.isNotEmpty()) {
            showExceptSelf[0].doGoneWith {
                show()
            }
        }
    }
}

fun Dialog.showSafe() {
    if (!this.isShowing)
        try {
            this.show()
        } catch (e: Exception) {
        }
}

fun Dialog.dismissSafe() {
    if (this.isShowing)
        try {
            this.dismiss()
        } catch (e: Exception) {
        }
}

object DialogHelper {
    @JvmStatic
    fun sheetPaddingTop(): Int {
        return toolbarHeight + 36.dp
    }

    @JvmStatic
    fun dialogPaddingTop(): Int {
        return 81.dp
    }
}

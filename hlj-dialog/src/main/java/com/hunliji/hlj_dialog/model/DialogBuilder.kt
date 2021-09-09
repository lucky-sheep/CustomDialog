package com.hunliji.hlj_dialog.model

import android.app.Dialog
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.hunliji.hlj_dialog.R
import com.hunliji.hlj_dialog.core.app
import com.hunliji.hlj_dialog.dp

/**
 * SampleDialogBuilder
 *
 * @author wm
 * @date 20-3-16
 */
class SampleDialogBuilder {
    private var title: String = ""
    private var titleColorRes: Int = 0
    private var onTitleColor: String = "#000000"
    private var contentColorRes: Int = 0
    private var onContentColor: String = "#333333"
    private var secondLineText: String = ""
    private var secondLineColorRes: Int = 0
    private var onSecondLineColor: String = "#333333"
    private var onConfirmText: String = "确定"
    private var onConfirmColor: Int = ContextCompat.getColor(app, R.color.colorPrimary)
    private var onConfirm: ((Dialog) -> Unit)? = null
    private var onCancelText: String = "取消"
    private var onCancelColor: Int = Color.parseColor("#999999")
    private var onCancel: ((Dialog) -> Unit)? = null
    private var setCorner = 12
    private var setIsSingle = false
    private var setWidth: Int = 300.dp
    private var setHeight: Int = 0
    private var maxHeight: Int = 500.dp
    private var onShowBehind: Boolean = true
    private var onDismiss: ((Dialog) -> Unit)? = null
    private var cancelOutSide: Boolean = true
    private var canCancel: Boolean = true
    private var setAutoDismiss: Boolean = true
    private var extra: ((Dialog) -> Unit)? = null

    fun extra(method: ((Dialog) -> Unit)? = null) {
        this.extra = method
    }

    fun getExtra(): ((Dialog) -> Unit)? {
        return extra
    }

    fun autoDismiss(autoDismiss: Boolean) {
        this.setAutoDismiss = autoDismiss
    }

    fun getAutoDismiss(): Boolean {
        return setAutoDismiss
    }

    fun confirm(method: ((Dialog) -> Unit)? = null) {
        this.onConfirm = method
    }

    fun getConfirm(): ((Dialog) -> Unit)? {
        return onConfirm
    }

    fun dismiss(method: ((Dialog) -> Unit)? = null) {
        this.onDismiss = method
    }

    fun getDismiss(): ((Dialog) -> Unit)? {
        return onDismiss
    }

    fun cancel(method: ((Dialog) -> Unit)? = null) {
        this.onCancel = method
    }

    fun getCancel(): ((Dialog) -> Unit)? {
        return onCancel
    }

    fun confirmColor(confirmColor: Int) {
        this.onConfirmColor = confirmColor
    }

    fun titleColor(titleColorRes: Int) {
        this.titleColorRes = titleColorRes
    }

    fun titleColor(titleColor: String) {
        this.onTitleColor = titleColor
    }

    fun getTitleColor(): Int {
        return if (titleColorRes != 0) {
            titleColorRes
        } else {
            return try {
                Color.parseColor(onTitleColor)
            } catch (e: Exception) {
                Color.parseColor("#000000")
            }
        }
    }

    fun secondLineColor(secondLineColorRes: Int) {
        this.secondLineColorRes = secondLineColorRes
    }

    fun secondLineColor(onSecondLineColor: String) {
        this.onSecondLineColor = onSecondLineColor
    }

    fun getSecondLineColor(): Int {
        return if (secondLineColorRes != 0) {
            secondLineColorRes
        } else {
            return try {
                Color.parseColor(onSecondLineColor)
            } catch (e: Exception) {
                Color.parseColor("#333333")
            }
        }
    }

    fun contentColor(contentColorRes: Int) {
        this.contentColorRes = contentColorRes
    }

    fun contentColor(contentColor: String) {
        this.onContentColor = contentColor
    }

    fun getContentColor(): Int {
        return if (contentColorRes != 0) {
            contentColorRes
        } else {
            return try {
                Color.parseColor(onContentColor)
            } catch (e: Exception) {
                Color.parseColor("#333333")
            }
        }
    }

    fun getConfirmColor(): Int = onConfirmColor

    fun cancelColor(cancelColor: Int) {
        this.onCancelColor = cancelColor
    }

    fun getCancelColor(): Int = onCancelColor

    fun confirmText(confirmText: String) {
        this.onConfirmText = confirmText
    }

    fun getConfirmText(): String {
        return onConfirmText
    }

    fun cancelText(cancelText: String) {
        this.onCancelText = cancelText
    }

    fun getCancelText(): String {
        return onCancelText
    }

    fun canCancelOutSide(cancelOutSide: Boolean) {
        this.cancelOutSide = cancelOutSide
    }

    fun getCanCancelOutSide(): Boolean {
        return cancelOutSide
    }

    fun cancelAble(cancel: Boolean) {
        this.canCancel = cancel
    }

    fun getCancelAble(): Boolean {
        return canCancel
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun getTitle(): String {
        return title
    }

    fun setSecondLine(secondLine: String) {
        this.secondLineText = secondLine
    }

    fun getSecondLine(): String {
        return secondLineText
    }

    fun showBehind(showBehind: Boolean) {
        this.onShowBehind = showBehind
    }

    fun getShowBehind(): Boolean {
        return onShowBehind
    }

    fun corner(corner: Int) {
        this.setCorner = corner
    }

    fun getCorner(): Int {
        return setCorner.dp
    }

    fun isSingle(isSingle: Boolean) {
        this.setIsSingle = isSingle
    }

    fun getIsSingle(): Boolean {
        return setIsSingle
    }

    fun width(width: Int) {
        this.setWidth = width
    }

    fun getWidth(): Int {
        return setWidth
    }

    fun height(height: Int) {
        this.setHeight = height
    }

    fun getHeight(): Int {
        return setHeight
    }

    fun getMaxHeight(): Int {
        return maxHeight
    }
}

class CustomerDialogBuilder<T> {
    private var onShowBehind: Boolean = true
    private var cancelOutSide: Boolean = true
    private var onDismiss: ((Dialog, T?) -> Unit)? = null
    private var canCancel: Boolean = true
    private var setWidth: Int = 300.dp
    private var setHeight: Int = 0
    private var onConfirm: ((Dialog, T?) -> Unit)? = null
    private var onCancel: ((Dialog) -> Unit)? = null
    private var setAutoDismiss: Boolean = true
    private var setDialogType: DialogType = DialogType.CENTER
    private var setDialogAnimType: Int = DialogAnimType.CENTER

    fun dialogAnimType(dialogAnimType: Int) {
        this.setDialogAnimType = dialogAnimType
    }

    fun getDialogAnimType(): Int {
        return setDialogAnimType
    }

    fun dialogType(dialogType: DialogType) {
        this.setDialogType = dialogType
    }

    fun getDialogType(): DialogType {
        return setDialogType
    }

    fun autoDismiss(autoDismiss: Boolean) {
        this.setAutoDismiss = autoDismiss
    }

    fun getAutoDismiss(): Boolean {
        return setAutoDismiss
    }

    fun showBehind(showBehind: Boolean) {
        this.onShowBehind = showBehind
    }

    fun cancel(method: ((Dialog) -> Unit)? = null) {
        this.onCancel = method
    }

    fun confirm(method: ((Dialog, T?) -> Unit)? = null) {
        this.onConfirm = method
    }

    fun getConfirm(): ((Dialog, T?) -> Unit)? {
        return onConfirm
    }

    fun getCancel(): ((Dialog) -> Unit)? {
        return onCancel
    }

    fun getShowBehind(): Boolean {
        return onShowBehind
    }

    fun canCancelOutSide(cancelOutSide: Boolean) {
        this.cancelOutSide = cancelOutSide
    }

    fun getCanCancelOutSide(): Boolean {
        return cancelOutSide
    }

    fun cancelAble(cancel: Boolean) {
        this.canCancel = cancel
    }

    fun getCancelAble(): Boolean {
        return canCancel
    }

    fun width(width: Int) {
        this.setWidth = width
    }

    fun getWidth(): Int {
        return setWidth
    }

    fun dismiss(method: ((Dialog, T?) -> Unit)? = null) {
        this.onDismiss = method
    }

    fun getDismiss(): ((Dialog, T?) -> Unit)? {
        return onDismiss
    }

    fun height(height: Int) {
        this.setHeight = height
    }

    fun getHeight(): Int {
        return setHeight
    }
}

class SampleCustomerDialogBuilder {
    private var onShowBehind: Boolean = true
    private var cancelOutSide: Boolean = true
    private var onDismiss: ((Dialog) -> Unit)? = null
    private var canCancel: Boolean = true
    private var setWidth: Int = 300.dp
    private var setHeight: Int = 0
    private var setConfirmId: Int = 0
    private var setCancelId: Int = 0
    private var onConvert: ((Dialog) -> Unit)? = null
    private var onConfirm: ((Dialog) -> Unit)? = null
    private var onCancel: ((Dialog) -> Unit)? = null
    private var setAutoDismiss: Boolean = true
    private var setDialogType: DialogType = DialogType.CENTER
    private var setDialogAnimType: Int = DialogAnimType.CENTER

    fun dialogAnimType(dialogAnimType: Int) {
        this.setDialogAnimType = dialogAnimType
    }

    fun getDialogAnimType(): Int {
        return setDialogAnimType
    }

    fun dialogType(dialogType: DialogType) {
        this.setDialogType = dialogType
    }

    fun getDialogType(): DialogType {
        return setDialogType
    }

    fun autoDismiss(autoDismiss: Boolean) {
        this.setAutoDismiss = autoDismiss
    }

    fun getAutoDismiss(): Boolean {
        return setAutoDismiss
    }

    fun confirmId(id: Int) {
        this.setConfirmId = id
    }

    fun getConfirmId(): Int {
        return setConfirmId
    }

    fun cancelId(id: Int) {
        this.setCancelId = id
    }

    fun getCancelId(): Int {
        return setCancelId
    }

    fun showBehind(showBehind: Boolean) {
        this.onShowBehind = showBehind
    }

    fun getShowBehind(): Boolean {
        return onShowBehind
    }

    fun convert(method: ((Dialog) -> Unit)? = null) {
        this.onConvert = method
    }

    fun getConvert(): ((Dialog) -> Unit)? {
        return onConvert
    }

    fun cancel(method: ((Dialog) -> Unit)? = null) {
        this.onCancel = method
    }

    fun getCancel(): ((Dialog) -> Unit)? {
        return onCancel
    }

    fun confirm(method: ((Dialog) -> Unit)? = null) {
        this.onConfirm = method
    }

    fun getConfirm(): ((Dialog) -> Unit)? {
        return onConfirm
    }

    fun canCancelOutSide(cancelOutSide: Boolean) {
        this.cancelOutSide = cancelOutSide
    }

    fun getCanCancelOutSide(): Boolean {
        return cancelOutSide
    }

    fun cancelAble(cancel: Boolean) {
        this.canCancel = cancel
    }

    fun getCancelAble(): Boolean {
        return canCancel
    }

    fun width(width: Int) {
        this.setWidth = width
    }

    fun getWidth(): Int {
        return setWidth
    }

    fun dismiss(method: ((Dialog) -> Unit)? = null) {
        this.onDismiss = method
    }

    fun getDismiss(): ((Dialog) -> Unit)? {
        return onDismiss
    }

    fun height(height: Int) {
        this.setHeight = height
    }

    fun getHeight(): Int {
        return setHeight
    }
}

class XPopBuilder {
    private var onShow: (() -> Unit)? = null
    private var onBeforeShow: (() -> Unit)? = null
    private var onBeforeDismiss: (() -> Unit)? = null
    private var onDismiss: (() -> Unit)? = null
    private var onShowBehind: Boolean = true
    private var dismissOnTouchOutSide: Boolean = true

    fun show(method: (() -> Unit)? = null) {
        this.onShow = method
    }

    fun beforeShow(method: (() -> Unit)? = null) {
        this.onBeforeShow = method
    }

    fun beforeDismiss(method: (() -> Unit)? = null) {
        this.onBeforeDismiss = method
    }

    fun getShow(): (() -> Unit)? {
        return onShow
    }

    fun getBeforeShow(): (() -> Unit)? {
        return onBeforeShow
    }

    fun getBeforeDismiss(): (() -> Unit)? {
        return onBeforeDismiss
    }

    fun dismiss(method: (() -> Unit)? = null) {
        this.onDismiss = method
    }

    fun getDismiss(): (() -> Unit)? {
        return onDismiss
    }

    fun showBehind(showBehind: Boolean) {
        this.onShowBehind = showBehind
    }

    fun getShowBehind(): Boolean {
        return onShowBehind
    }

    fun setDismissOnTouchOutSide(dismissOnTouchOutSide: Boolean) {
        this.dismissOnTouchOutSide = dismissOnTouchOutSide
    }

    fun getDismissOnTouchOutSide(): Boolean {
        return dismissOnTouchOutSide
    }
}



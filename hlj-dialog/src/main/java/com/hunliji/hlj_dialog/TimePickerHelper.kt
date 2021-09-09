package com.hunliji.hlj_dialog

import android.content.Context
import android.view.View
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.TimePickerView
import com.hunliji.ext_master.isNavBarOnBottom
import com.hunliji.ext_master.navigationBarHeight
import com.hunliji.ext_master.statusBarHeight
import com.hunliji.hlj_dialog.xpopup.util.XPopupUtils
import java.util.*

/**
 * showTimePicker
 *
 * @param params
 * @param onConfirm
 */
fun Context.showTimePicker(
    params: (TimePickerBuilder.() -> TimePickerBuilder)? = null,
    onConfirm: ((date: Date) -> Unit)? = null
): TimePickerView {
    var pickerView: TimePickerView? = null
    val builder = TimePickerBuilder(this) { date, _ ->
        onConfirm?.invoke(date)
    }
    builder.setLayoutRes(R.layout.dialot_time_picker_view) {
        it.findViewById<View>(R.id.tv_cancel).setOnClickListener {
            pickerView?.dismiss()
        }
        it.findViewById<View>(R.id.tv_finish).setOnClickListener {
            pickerView?.returnData()
            pickerView?.dismiss()
        }
        it.findViewById<View>(R.id.frame).setPadding(
            0, 0, 0,
            if (XPopupUtils.isNavBarVisible(this)) navigationBarHeight else 0
        )
    }
    params?.let {
        builder.it()
    }
    pickerView = builder.build()
    pickerView.show()
    return pickerView
}
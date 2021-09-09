package com.hunliji.mvvm.activity

import android.content.Intent
import android.view.View
import com.hunliji.ext_master.deviceWidth
import com.hunliji.hlj_dialog.showTimePicker
import com.hunliji.mvvm.BaseActivity
import com.hunliji.mvvm.R
import com.hunliji.mvvm.activity.activity_main.property
import com.hunliji.mvvm.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding, MainVm>() {
    override fun getLayoutId() = R.layout.activity_main

    override fun getData(intent: Intent?) {
        setValue("id" to 1)
    }

    override fun initView() {
        ll.property(
            parentWidth = deviceWidth,
            defaults = mutableListOf("第一", "第二")
        )

    }

    fun isPalindrome(x: Int): Boolean {
        if (x < 0) return false
        var rem: Int
        var y = 0
        var quo = x
        while (quo != 0) {
            rem = quo % 10
            y = y * 10 + rem
            quo /= 10
        }
        return y == x
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.text -> {
                showTimePicker({
                    setRangDate(Calendar.getInstance().also {
                        it.set(2000, 11,10)
                    }, Calendar.getInstance().also {
                        it.set(2049, 11,10)
                    })
                    setDate(Calendar.getInstance().also {
                        it.set(2040, 11,10)
                    })
                    setType(booleanArrayOf(true, true, true, true, true, false))
                    setLabel("年", "月", "日", "时", "分", "秒")
                    setLineSpacingMultiplier(3f)
                    setItemVisibleCount(5)
                    setContentTextSize(15)
                }, onConfirm = {

                })
            }
            R.id.text1 -> {
            }
        }
    }

    override fun onRequestReload() {
        showLoading()
    }

    override fun getRegisterLoading(): Any {
        return fragment_container
    }
}

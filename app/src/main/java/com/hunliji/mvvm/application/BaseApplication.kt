package com.hunliji.mvvm.application

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import com.hunliji.integration_mw.AppDelegate
import com.tencent.mmkv.MMKV


/**
 * @author kou_zhong
 * @date 2020-02-13.
 * description：
 */
class BaseApplication : Application() {
    private var appDelegate: AppDelegate? = null
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
        if (appDelegate == null) {
            appDelegate = AppDelegate(base)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val rootDir = MMKV.initialize(this)
        appDelegate?.onCreate(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        appDelegate?.onTerminate(this)
    }
}

package com.hunliji.mvvm

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hunliji.hlj_loading.loading.DefaultEmptyCallback
import com.hunliji.hlj_loading.loading.DefaultErrorCallback
import com.hunliji.hlj_loading.loading.DefaultLoadingHideCallback
import com.hunliji.hlj_loading.loading.DefaultLoadingShowCallback
import com.hunliji.mvvm.binding.RefreshPresenter
import com.hunliji.mvvm.core.*
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import java.lang.reflect.ParameterizedType
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * BaseActivity
 *
 * @author wm
 * @date 19-8-9
 */
abstract class BaseActivity<B : ViewDataBinding, VM : BaseVm> : FragmentActivity(),
    IView, ILoading, IViewEvent, RefreshPresenter, ITimer {

    var loadService: LoadService<*>? = null
    private var timer: ScheduledExecutorService? = null
    private val paramsMap = mutableMapOf<String, @JvmSuppressWildcards Any>()

    /**
     * dataBinding
     */
    protected lateinit var binding: B
    open lateinit var viewModel: VM
    var baseVm: BaseVm? = null

    @Suppress("UNCHECKED_CAST")
    private fun createVM() {
        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            val tp = type.actualTypeArguments[1]
            val tClass = tp as? Class<VM> ?: BaseVm::class.java
            viewModel = ViewModelProvider(
                this.viewModelStore,
                VmFactory(application, getMap())
            ).get(tClass) as VM
        }
    }

    val linearLayoutManager by lazy {
        LinearLayoutManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayoutId())
        binding.lifecycleOwner = this
        if (isSupportLoading()) {
            registerLoading()
        }
        binding.setVariable(BR.v, this)
        getData(intent)
        createVM()
        binding.setVariable(BR.vm, viewModel)
        baseVm = viewModel
        onPrepare()
        initView()
        fitScreen()
        if (isSupportLoading()) {
            baseVm?.stateModel?.observe(this, Observer {
                val value = baseVm?.stateModel?.value
                when (true) {
                    value == BaseVm.NORMAL -> hideLoading()
                    value == BaseVm.PROGRESS -> showLoading()
                    value == BaseVm.EMPTY -> showEmpty()
                    value == BaseVm.ERROR -> showError()
                }
            })
        }
    }

    /**
     * 获取传递数据 intent
     *
     * @param intent intent
     */
    open fun getData(intent: Intent?) {

    }

    open fun onPrepare() {

    }

    fun setValue(pair: Pair<String, @JvmSuppressWildcards Any>) {
        paramsMap[pair.first] = pair.second
    }

    fun getMap(): MutableMap<String, @JvmSuppressWildcards Any> {
        return paramsMap
    }

    /**
     * 动态适配
     */
    open fun fitScreen() {

    }

    override fun onDestroy() {
        if (isSupportTimer()) {
            cancelTimer()
        }
        super.onDestroy()
    }

    private fun registerLoading() {
        val build = LoadSir.Builder()
            .addCallback(DefaultLoadingHideCallback())
            .addCallback(DefaultLoadingShowCallback())
            .addCallback(DefaultEmptyCallback())
            .addCallback(DefaultErrorCallback())
            .setDefaultCallback(DefaultLoadingHideCallback::class.java)
            .build()
        loadService = build.register(getRegisterLoading()) {
            if (isSupportReload()) {
                onRequestReload()
            }
        }
    }

    open fun getRegisterLoading(): Any {
        return findViewById(android.R.id.content)
    }

    override fun showLoading() {
        if (isSupportLoading()) {
            loadService?.showCallback(DefaultLoadingHideCallback::class.java)
        }
    }

    override fun hideLoading() {
        if (isSupportLoading()) {
            loadService?.showSuccess()
        }
    }

    override fun showEmpty() {
        if (isSupportLoading()) {
            loadService?.showCallback(DefaultEmptyCallback::class.java)
        }
    }

    override fun showError() {
        if (isSupportLoading()) {
            loadService?.showCallback(DefaultErrorCallback::class.java)
        }
    }

    override fun onClick(v: View?) {

    }

    override fun loadData(isNormal: Boolean, isRefresh: Boolean) {

    }

    open fun startTimer(any: Any? = null) {
        cancelTimer()
        timer = Executors.newScheduledThreadPool(4)
        timer?.scheduleAtFixedRate({
            runOnUiThread {
                onTimer(any)
            }
        }, timerDelay(), timerInterval(), TimeUnit.SECONDS)
    }

    open fun cancelTimer() {
        timer?.let {
            it.shutdown()
            timer = null
        }
    }

    override fun onTimer(any: Any?) {

    }
}

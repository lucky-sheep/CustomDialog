package com.hunliji.mvvm.net.exception

import com.hunliji.mvvm.BaseVm
import com.hunliji.mvvm.activity.activity2.RefreshType
import com.hunliji.mvvm.net.api.ArticlesBean
import com.hunliji.mvvm.net.api.BaseResult
import com.hunliji.mvvm.net.api.HomeListBean
import com.hunliji.mvvm.net.model.BaseListResponse
import com.hunliji.mvvm.net.model.BaseResponse

/**
 * NetConvert
 *
 * @author wm
 * @date 20-2-25
 */
fun HomeListBean.convertList(isRefresh: Boolean = true, vm: BaseVm): List<ArticlesBean> {
    val response = this
    val list = response.datas
    return list
}

fun <T> BaseResult<T>.convert(
    vm: BaseVm
): T {
    val response = this
    vm.run {
        if (response.isSuccess()) {
            if (response.data == null) {
                throw Errors.EmptyException
            } else {
                return response.data
            }
        } else {
            throw Errors.ErrorException(response.errorCode, response.errorMsg)
        }
    }
}

fun <T> BaseResult<T>.convertList(
    isRefresh: Boolean = true,
    vm: BaseVm
): T {
    val response = this
    vm.run {
        val convert = response.convert(this)
        if (convert is HomeListBean) {
            convert.convertList(isRefresh, this)
        }
        return convert
    }
}


fun <T> BaseListResponse<T>.convertList(
    isRefresh: Boolean = true,
    vm: BaseVm
): List<T> {
    val response = this
    val list = response.data
    return list
}

fun <T> BaseResponse<T>.convert(
    vm: BaseVm
): T {
    val response = this
    vm.run {
        if (response.isSuccess()) {
            if (response.data == null) {
                throw Errors.EmptyException
            } else {

                return response.data
            }
        } else {
            throw Errors.ErrorException(response.status.retCode, response.status.msg)
        }
    }
}

fun <T> BaseResponse<T>.convertList(
    isRefresh: Boolean = true,
    vm: BaseVm
): T {
    val response = this
    vm.run {
        val convert = response.convert(this)
        if (convert is BaseListResponse<*>) {
            convert.convertList(isRefresh, this)
        }
        return convert
    }
}



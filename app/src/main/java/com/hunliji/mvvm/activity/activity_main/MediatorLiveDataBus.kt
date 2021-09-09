package com.hunliji.mvvm.activity.activity_main

/**
 * @author kou_zhong
 * @date 2020/11/3
 */
class MediatorLiveDataBus(val type: Int, val value: Any){
    override fun toString(): String {
        return "type:${type},value:${value}"
    }
}
package com.hunliji.mvvm.activity.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author kou_zhong
 * @date 2020/9/17
 */
@Parcelize
data class UserTalentInformation(
    val id: Long,
    val name: String?
) : Parcelable
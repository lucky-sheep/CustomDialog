package com.hunliji.mvvm.model

import android.graphics.Rect

import com.hunliji.hlj_image_preview.preview.model.ImagePreViewInfo
import com.hunliji.hlj_image_preview.preview.model.ImagePreviewDefaultType
import kotlinx.android.parcel.Parcelize

/**
 * ImageInfo
 *
 * @author wm
 * @date 20-3-4
 */
@Parcelize
data class ImageInfo(
    val img: String = "",
    val mBounds: Rect? = null,
    val rectType: ImagePreviewDefaultType? = null
) : ImagePreViewInfo {
    override val title: String
        get() = ""
    override val previewBounds: Rect?
        get() = mBounds
    override val defaultRectType: ImagePreviewDefaultType?
        get() = rectType
    override val previewUrl: String
        get() = img
}

package com.hunliji.hlj_dialog.xpopup.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class MaxHeightLinearLayout : LinearLayout {

    private var maxHeight = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec1 = heightMeasureSpec
        if (maxHeight > 0) {
            heightMeasureSpec1 = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec1)
    }

    fun setMaxHeight(maxHeight: Int) {
        this.maxHeight = maxHeight
    }

}
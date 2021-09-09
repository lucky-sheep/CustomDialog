package com.hunliji.mvvm.activity.activity2;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.hunliji.mvvm.activity.activity2.RefreshType.LOAD_MORE_FINISH;
import static com.hunliji.mvvm.activity.activity2.RefreshType.LOAD_NOT_MORE;
import static com.hunliji.mvvm.activity.activity2.RefreshType.REFRESH_FINISH;
import static com.hunliji.mvvm.activity.activity2.RefreshType.REFRESH_NOT_MORE;

/**
 * RefreshType
 *
 * @author wm
 * @date 19-9-3
 */
@IntDef({REFRESH_NOT_MORE, REFRESH_FINISH, LOAD_MORE_FINISH, LOAD_NOT_MORE})
@Retention(RetentionPolicy.SOURCE)
public @interface RefreshType {
    //下拉刷新结束
    int REFRESH_FINISH = -1;
    //下拉刷新结束并且没有更多
    int REFRESH_NOT_MORE = -3;
    //加载更多结束
    int LOAD_MORE_FINISH = -4;
    //没有更多数据
    int LOAD_NOT_MORE = -6;
}

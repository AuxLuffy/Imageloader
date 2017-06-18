package com.atguigu.imageloader.listener;

import org.xutils.common.Callback;

/**
 * Created by shkstart on 2016/6/1.
 */
public class MyCacheCallback<T> implements Callback.CacheCallback<T> {

    @Override
    public boolean onCache(T result) {
        return false;
    }

    @Override
    public void onSuccess(T result) {

    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {

    }

    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onFinished() {

    }
}

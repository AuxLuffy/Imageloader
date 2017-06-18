package com.atguigu.imageloader.ui;

import android.app.Application;

import com.atguigu.imageloader.util.AppUtils;

import org.xutils.BuildConfig;
import org.xutils.x;

/**
 * Created by shkstart on 2016/5/31.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化xutils框架
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);

        //初始化要使用的context
        AppUtils.setContext(this);
    }
}

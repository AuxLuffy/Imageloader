package com.atguigu.imageloader.util;

import android.os.Environment;

/**
 * Created by shkstart on 2016/5/31.
 */
public class Contants {

    public static final int s_web = 0;//联网状态
    public static final int s_local = 1;//本地状态
    public static int state = s_web;//标识当前的页面的状态


    public static final String SDCARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/imageloader";

}


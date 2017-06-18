package com.atguigu.imageloader.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.atguigu.imageloader.R;

import org.xutils.image.ImageOptions;

/**
 * Created by shkstart on 2016/5/31.
 */
public class AppUtils {

    public static Context context;
    public static void setContext(Context context){
        AppUtils.context = context;
    }


    public static ImageOptions smallImageOptions;
    public static ImageOptions bigImageOptions;
    static {
        smallImageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.FIT_CENTER) //等比例放大/缩小到充满长/宽居中显示
                .setLoadingDrawableId(R.drawable.default_image)
                .setFailureDrawableId(R.drawable.default_image)
                .setConfig(Bitmap.Config.RGB_565)//设置为16位图
                .build();

        bigImageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_INSIDE)//等比例缩小到充满长/宽居中显示, 或原样显示
                .setLoadingDrawableId(R.drawable.default_image)
                .setFailureDrawableId(R.drawable.default_image)
                .setConfig(Bitmap.Config.ARGB_8888)//设置为32位图
                .build();
    }

    /**
     * 获取指定路径下文件的名称
     * @param fileName
     * @return
     */
    public static String cutFilePath(String fileName){ //http://www.atguigu.com/courses/abc.png

        int i = fileName.lastIndexOf("/");
        return fileName.substring(i + 1);

    }

}

package com.atguigu.imageloader.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.imageloader.R;
import com.atguigu.imageloader.bean.ImageBean;
import com.atguigu.imageloader.fragment.ImgDetailFragment;
import com.atguigu.imageloader.listener.MyCacheCallback;
import com.atguigu.imageloader.util.AppUtils;
import com.atguigu.imageloader.util.Contants;

import org.xutils.common.util.FileUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DragPicturesActivity extends FragmentActivity {

    private TextView tv_dragpics_url;
    private TextView tv_dragpics_pageno;
    private ViewPager vp_dragpics;
    private ImageView iv_dragpics_download;
    private ImageView iv_dragpics_share;

    private int position;
    private ArrayList<ImageBean> imageBeans;
    private PictureSlidePagerAdapter adapter;

    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        //当滑动停止时调用的回调方法
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            DragPicturesActivity.this.position = position;
            tv_dragpics_url.setText(imageBeans.get(position).getUrl());
            tv_dragpics_pageno.setText((position + 1) + "/" + imageBeans.size());
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_pictures);


        //隐藏ActionBar
        getActionBar().hide();

        init();

        //创建Adapter，并加载显示
        adapter = new PictureSlidePagerAdapter(getSupportFragmentManager());
        vp_dragpics.setAdapter(adapter);

        vp_dragpics.setCurrentItem(position);//显示当前项的图片
        vp_dragpics.addOnPageChangeListener(listener);

    }

    private void init() {
        tv_dragpics_url = (TextView) findViewById(R.id.tv_dragpics_url);
        tv_dragpics_pageno = (TextView) findViewById(R.id.tv_dragpics_pageno);
        vp_dragpics = (ViewPager) findViewById(R.id.vp_dragpics);
        iv_dragpics_download = (ImageView) findViewById(R.id.iv_dragpics_download);
        iv_dragpics_share = (ImageView) findViewById(R.id.iv_dragpics_share);

        //设置ImageView的显示
        if(Contants.state == Contants.s_web){//联网状态
            iv_dragpics_download.setImageResource(R.drawable.icon_s_download_press);
            iv_dragpics_share.setVisibility(View.GONE);
        }else if(Contants.state == Contants.s_local){//本地状态
            iv_dragpics_download.setImageResource(R.drawable.garbage_media_cache);
            iv_dragpics_share.setVisibility(View.VISIBLE);
        }

        //获取前一个界面发送过来的数据
        Intent intent = getIntent();
        position = intent.getIntExtra("position",0);
        imageBeans = (ArrayList<ImageBean>) intent.getSerializableExtra("data");

        //更新界面
        tv_dragpics_url.setText(imageBeans.get(position).getUrl());
        tv_dragpics_pageno.setText((position + 1) + "/" + imageBeans.size());



    }

    /**
     * 内部类
     *
     * FragmentStatePagerAdapter:适合于要显示的Fragment比较多的情况
     * FragmentPagerAdapter:适合于要显示的Fragment比较少的情况
     *
     */

    class PictureSlidePagerAdapter extends FragmentStatePagerAdapter{

        public PictureSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ImgDetailFragment.getInstance(imageBeans.get(position).getUrl());
        }

        @Override
        public int getCount() {
            return imageBeans.size();
        }
    }

    /**
     * 点击下载/设置为壁纸的ImageView的回调方法
     * @param v
     */
    public void downloadImage(View v) {
        //此时得到的filePath可能是一个联网状态下得到的图片的url地址，也可能是本地的图片的路径
        String filePath = imageBeans.get(position).getUrl();

        if(Contants.state == Contants.s_web){//联网状态：下载

            final String localFilePath = Contants.SDCARD_DIR + "/" + System.currentTimeMillis() + AppUtils.cutFilePath(filePath);

            x.http().get(new RequestParams(filePath),new MyCacheCallback<File>(){
                @Override
                public boolean onCache(File result) {

                    FileUtil.copy(result.getAbsolutePath(),localFilePath);
                    Toast.makeText(DragPicturesActivity.this, "下载图片成功", Toast.LENGTH_SHORT).show();
                    
                    return true;
                }

                @Override
                public void onSuccess(File result) {
                    FileUtil.copy(result.getAbsolutePath(),localFilePath);
                    Toast.makeText(DragPicturesActivity.this, "下载图片成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Toast.makeText(DragPicturesActivity.this, "下载图片失败", Toast.LENGTH_SHORT).show();
                    
                }
            });
            
            
        }else if(Contants.state == Contants.s_local){//本地状态：设置为壁纸
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            try {
                setWallpaper(bitmap);
                Toast.makeText(DragPicturesActivity.this, "设置壁纸成功", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(DragPicturesActivity.this, "设置壁纸失败", Toast.LENGTH_SHORT).show();
            }

        }


    }

    /**
     * 分享图片
     * @param v
     */
    public void shareImage(View v) {

        File file = new File(imageBeans.get(position).getUrl());
        if(file.exists()){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file),"image/*");
            startActivity(intent);

        }
    }
}

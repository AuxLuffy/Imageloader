package com.atguigu.imageloader.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.atguigu.imageloader.R;
import com.atguigu.imageloader.adapter.MainAdapter;
import com.atguigu.imageloader.bean.WebLink;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    private GridView gv_main;
    private MainAdapter adapter;
    private List<WebLink> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExecutorService pool = Executors.newCachedThreadPool();
        pool.execute(new Runnable() {
            @Override
            public void run() {
                checkPermissions();
            }
        });

        gv_main = (GridView) findViewById(R.id.gv_main);
        //初始化集合数据
        initData();
        adapter = new MainAdapter(data, this);
        //显示列表
        gv_main.setAdapter(adapter);
        //设置GridView中item的点击事件的监听
        gv_main.setOnItemClickListener(this);

    }

    private void checkPermissions() {
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionsList.size() != 0) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        3);
            }
        }
    }

    /**
     * 初始化集合数据
     */
    private void initData() {
        data = new ArrayList<WebLink>();
        data.add(new WebLink("图片天堂", R.drawable.i1, "www.ivsky.com/"));
        data.add(new WebLink("硅谷教育", R.drawable.i2, "www.atguigu.com/"));
        data.add(new WebLink("新闻图库", R.drawable.i3, "www.cnsphoto.com/"));

        data.add(new WebLink("MOKO美空", R.drawable.i4, "www.moko.cc/"));
        data.add(new WebLink("114啦", R.drawable.i5, "www.114la.com/mm/index.htm/"));
        data.add(new WebLink("动漫之家", R.drawable.i6, "www.donghua.dmzj.com/"));

        data.add(new WebLink("7k7k", R.drawable.i7, "www.7k7k.com/"));
        data.add(new WebLink("嘻嘻哈哈", R.drawable.i8, "www.xxhh.com/"));
        data.add(new WebLink("有意思吧", R.drawable.i9, "www.u148.net/"));
    }

    /**
     * 当点击GridView中具体某个Item时的回调方法
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(this, WebPicturesActivity.class);
        String url = data.get(position).getUrl();
        intent.putExtra("url", url);
        startActivity(intent);

    }

    //2s内两次按回退键, 退出应用, 并清理内存缓存
    //方式一：使用handler实现
    //方式二：
    private long firstTime = 0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (System.currentTimeMillis() - firstTime > 2000) {
                firstTime = System.currentTimeMillis();
                Toast.makeText(this, "再点击一次退出应用", Toast.LENGTH_SHORT).show();
                return true;
            } else {
//                x.image().clearCacheFiles();//清理缓存文件

                x.image().clearMemCache();//清理缓存的内存
            }

        }


        return super.onKeyUp(keyCode, event);
    }
}

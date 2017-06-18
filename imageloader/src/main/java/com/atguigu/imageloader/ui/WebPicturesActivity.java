package com.atguigu.imageloader.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.imageloader.R;
import com.atguigu.imageloader.adapter.ImageAdapter;
import com.atguigu.imageloader.bean.HistoryUrl;
import com.atguigu.imageloader.bean.ImageBean;
import com.atguigu.imageloader.dao.HistoryUrlDao;
import com.atguigu.imageloader.listener.MyCacheCallback;
import com.atguigu.imageloader.util.AppUtils;
import com.atguigu.imageloader.util.Contants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xutils.common.Callback;
import org.xutils.common.util.FileUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class WebPicturesActivity extends Activity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private TextView tv_pictures_msg;//显示文本信息
    private CheckBox cb_pictures_selected; //勾选图片
    private ImageView iv_pictures_download; //下载或删除图片
    private Button btn_pictures_stopDownload; //停止抓取图片
    private ProgressBar pb_pictures_loading; //深度抓取进度
    private GridView gv_pictures; //抓取图片列表

    private ImageAdapter adapter;
    private SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String url) {

            String newUrl = checkedUrlPre(url);
            //联网
            getHttpImages(newUrl);

            searchView.clearFocus();//清除焦点，隐藏软键盘

            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    /**
     * 布局中下载/删除ImageView的回调方法
     *
     * @param v
     */
    public void downloadImages(View v) {
        if (Contants.state == Contants.s_web) {//联网状态：批量下载
            //显示水平进度的ProgressDialog
            showProgressDialog(true, "当前进度");
            dialog.setTitle("下载进度");
            dialog.setMax(selectCount);

            //下载的操作
            for (int i = 0; i < imageBeans.size(); i++) {
                ImageBean imageBean = imageBeans.get(i);
                if (imageBean.isChecked()) {
                    downloadImage(imageBean.getUrl());
                }
            }
            adapter.changeAllItemStatus(false);


        } else if (Contants.state == Contants.s_local) {//本地状态：批量删除

            for (int i = 0; i < imageBeans.size(); i++) {
                ImageBean imageBean = imageBeans.get(i);
                if (imageBean.isChecked()) {

                    File file = new File(imageBean.getUrl());//本地图片对应的路径
                    if (file.exists()) {
                        file.delete();//存储中删除图片
                        imageBeans.remove(i);//内存中删除图片对象
                        i--;
                    }
                }
            }

            Toast.makeText(WebPicturesActivity.this, "删除成功", Toast.LENGTH_SHORT).show();

        }
        //下载完成以后：
        isEdit = false;
        selectCount = 0;
        tv_pictures_msg.setText("请在搜索框输入网址");
        cb_pictures_selected.setChecked(false);
        cb_pictures_selected.setVisibility(View.GONE);
        iv_pictures_download.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();

    }

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        return super.onKeyUp(keyCode, event);
//    }

    /**
     * 设置返回键的操作
     */
    @Override
    public void onBackPressed() {
        if(isEdit) {//编辑状态
            isEdit = false;
            selectCount = 0;
            tv_pictures_msg.setText("请在搜索框输入网址");
            cb_pictures_selected.setChecked(false);
            cb_pictures_selected.setVisibility(View.GONE);
            iv_pictures_download.setVisibility(View.GONE);
            adapter.changeAllItemStatus(false);
            adapter.notifyDataSetChanged();

        }else{//查看状态

            super.onBackPressed();//实现退出当前的Activity
        }

    }

    /**
     * 下载指定url对应的图片，下载到本地指定文件目录下
     *
     * @param url
     */
    private void downloadImage(String url) {
        //提供一个本地的文件目录，用于保存下载的图片
        File filesDir = new File(Contants.SDCARD_DIR);
        if (!filesDir.exists()) {
            filesDir.mkdirs();
        }
        //得到要存储的名称
        final String filePath = Contants.SDCARD_DIR + "/" + System.currentTimeMillis() + AppUtils.cutFilePath(url);

        //联网操作
        RequestParams requestParams = new RequestParams(url);
        requestParams.setConnectTimeout(5000);
        x.http().get(requestParams, new MyCacheCallback<File>() {
            /**
             *当联网成功时，会首次调用onCache()方法，会实现内存中缓存下载的数据。
             *
             * 如果onCache()方法返回true:则下次显示同样的url对应的图片时，就从内存中直接调用，不再联网
             * 如果onCache()方法返回false:则下次显示同样的url对应的图片时，会调用onSuccess()，重新下载
             *
             * @param result
             * @return
             */
            @Override
            public boolean onCache(File result) {
                //实现了网络中指定url图片的下载，下载到指定的filePath对应的文件中
                FileUtil.copy(result.getAbsolutePath(), filePath);

                updateProgress();

                return true;
            }

            @Override
            public void onSuccess(File result) {
                //实现了网络中指定url图片的下载，下载到指定的filePath对应的文件中
                FileUtil.copy(result.getAbsolutePath(), filePath);
                updateProgress();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(WebPicturesActivity.this, "下载图片失败", Toast.LENGTH_SHORT).show();

                updateProgress();

            }
        });


    }

    /**
     * 下载图片过程中，修改ProgressDialog的进度
     */
    private void updateProgress() {
        dialog.incrementProgressBy(1);
        if (dialog.getProgress() == dialog.getMax()) {
            dialog.dismiss();
            Toast.makeText(WebPicturesActivity.this, "所有图片下载完成", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置屏幕常亮
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        setContentView(R.layout.activity_web_pictures);

        init();//初始化的方法

        //给GridView设置item的长按事件的监听
        gv_pictures.setOnItemLongClickListener(this);

        //给GridView设置item的点击事件的监听
        gv_pictures.setOnItemClickListener(this);

        //给CheckBox设置状态改变的监听
        cb_pictures_selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//全选
                    selectCount = imageBeans.size();
                } else {//全不选
                    selectCount = 0;
                }

                tv_pictures_msg.setText(selectCount + "/" + imageBeans.size());
                adapter.changeAllItemStatus(isChecked);
                adapter.notifyDataSetChanged();
            }
        });

        //显示“返回的”小图标
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private HistoryUrlDao historyUrlDao = new HistoryUrlDao();//用于与数据库交互的dao类
    private List<HistoryUrl> historyUrls = historyUrlDao.getAll();//用于在内存中保存历史记录的集合


    private SearchView searchView;//搜索视图

    /**
     * 加载提供的menu布局文件
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);

        //搜索功能的实现
        MenuItem item = menu.findItem(R.id.item_menu_search);
        searchView = (SearchView) item.getActionView();

        //设置属性
        searchView.setQueryHint("请输入网址");//显示hint提示
        searchView.setSubmitButtonEnabled(true);//使得“提交”的button有效
        searchView.setOnQueryTextListener(listener);//提供对输入内容的监听

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_menu_history://查看历史记录

                showHistoryUrl();

                break;

            case R.id.item_menu_download://查看本地图片

                showLocalImages();

                break;
            case android.R.id.home:
                finish();//结束当前activity的显示
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * 查看本地图片
     */
    private void showLocalImages() {
        Contants.state = Contants.s_local;//本地状态


        isEdit = false;
        iv_pictures_download.setImageResource(R.drawable.op_del_press);
        iv_pictures_download.setVisibility(View.GONE);

        cb_pictures_selected.setChecked(false);
        cb_pictures_selected.setVisibility(View.GONE);
        tv_pictures_msg.setText("请在搜索框输入网址");


        //查找本地指定的路径下的所有的图片
        File fileDir = new File(Contants.SDCARD_DIR);
        Log.e("TAG", fileDir.toString() + "1111");
        File[] files = fileDir.listFiles();//得到指定路径下的所有的图片构成的file对象

        List<ImageBean> list = new ArrayList<ImageBean>();
        if (files != null) {
            //得到本地指定路径下所有的图片的url封装成的imagebean对象构成的集合
            for (int i = 0; i < files.length; i++) {
                String localUrl = files[i].getAbsolutePath();//storage/sdcard/imageloader/abc.png
                ImageBean imageBean = new ImageBean(localUrl);
                list.add(imageBean);
            }

        }
        imageBeans = list;
        adapter.setList(imageBeans);
        adapter.notifyDataSetChanged();

        Toast.makeText(WebPicturesActivity.this, "查看本地图片", Toast.LENGTH_SHORT).show();


    }

    /**
     * 查看历史记录的方法
     */
    private void showHistoryUrl() {
        //得到历史记录构成的数组
        final String[] urls = new String[historyUrls.size()];
        for (int i = 0; i < urls.length; i++) {
            urls[i] = historyUrls.get(i).getUrl();
        }
        new AlertDialog.Builder(this)
                .setTitle("历史记录")
                .setItems(urls, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //联网操作
                        getHttpImages(urls[which]);
                    }
                })
                .show();
    }

    private String url;//当前Activity所要加载的网站的url

    private void init() {
        //初始化视图对象
        tv_pictures_msg = (TextView) findViewById(R.id.tv_pictures_msg);
        cb_pictures_selected = (CheckBox) findViewById(R.id.cb_pictures_selected);
        iv_pictures_download = (ImageView) findViewById(R.id.iv_pictures_download);
        btn_pictures_stopDownload = (Button) findViewById(R.id.btn_pictures_stopDownload);
        pb_pictures_loading = (ProgressBar) findViewById(R.id.pb_pictures_loading);
        gv_pictures = (GridView) findViewById(R.id.gv_pictures);

        adapter = new ImageAdapter(this);
        //显示列表
        gv_pictures.setAdapter(adapter);


        //从前一个界面中获取要联网的url
        Intent intent = getIntent();
        url = intent.getStringExtra("url");//url: http://www.sina.com/news/123.jpg
        //联网前的检查
        url = checkedUrlPre(url);

        //联网的操作
        getHttpImages(url);
    }

    private List<ImageBean> imageBeans = new ArrayList<ImageBean>();//用来保存联网下载的具体图片url
    private HashSet<ImageBean> imageUrlSet = new HashSet<>();//用来去除重复url代表的图片

    public static boolean isEdit = false;//是否处理编辑状态

    /**
     * 联网操作
     *
     * @param url：http://www.atguigu.com
     */
    private void getHttpImages(final String url) {

        //将url添加到历史记录中
        HistoryUrl historyUrl = new HistoryUrl(-1, url);
        if (!historyUrls.contains(historyUrl)) {//重写HistoryUrl的equals()方法
            historyUrlDao.insert(historyUrl);//添加记录到存储中
            ////添加记录到内存中
            //方式一：
//          historyUrls = historyUrlDao.getAll();
            //方式二：
            historyUrls.add(historyUrl);
        }


        //1.标识当前的状态
        Contants.state = Contants.s_web;

        //2.显示加载图片的视图
        showProgressDialog(false, "正在抓取" + url + "网站的图片");

        //联网加载数据

        RequestParams entity = new RequestParams(url);
        //参数1：指明联网的url地址
        //参数2：对应响应的不同情况
        x.http().get(entity, new Callback.CommonCallback<String>() {
            //当正确的得到了响应数据：html
            @Override
            public void onSuccess(String html) {

                //清空之前的集合数据
                imageBeans.clear();
                imageUrlSet.clear();

                showImagesFromHtml(url, html);

                dialog.dismiss();//设置为消失

                //实现深度抓取
                showDeepSearchDialog(url, html);

            }

            //获取响应数据失败
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(WebPicturesActivity.this, "抓取图片失败", Toast.LENGTH_SHORT).show();

                dialog.dismiss();//设置为消失
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });


    }

    /**
     * 显示深度抓取的AlertDialog
     */
    private void showDeepSearchDialog(final String url, final String html) {
        new AlertDialog.Builder(this)
                .setTitle("请确认")
                .setMessage(url + "首页数据已经抓取完毕，是否进行深度抓取？(请确认有wifi环境)")
                .setPositiveButton("深度抓取", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //深度抓取
                        deepSearch(url, html);
                    }
                })
                .setNegativeButton("下回吧", null)
                .show();

    }

    /**
     * 深度抓取的方法
     */
    private void deepSearch(String url, String html) {
        pb_pictures_loading.setVisibility(View.VISIBLE);
        btn_pictures_stopDownload.setVisibility(View.VISIBLE);

        Document doc = Jsoup.parse(html);// 解析HTML页面
        // 获取页面中的所有连接
        Elements links = doc.select("a[href]");//得到所有的首页对应的html代码中的二级链接的地址
        List<String> useLinks = getUseableLinks(links);// 过滤

        //设置ProgressBar的最大值
        pb_pictures_loading.setMax(useLinks.size());

        for (int i = 0; i < useLinks.size(); i++) {
            final String useLink = useLinks.get(i);//比如：http://www.atguigu.com/course


            x.http().get(new RequestParams(useLink), new MyCacheCallback<String>() {
                @Override
                public void onSuccess(String html) {

                    if (stopDeepSearch) {
                        return;
                    }

                    showImagesFromHtml(useLink, html);
                    tv_pictures_msg.setText("抓取到" + imageBeans.size() + "张图片");

                    updateProgress("抓取完毕，总共抓取到" + imageBeans.size() + "张图片");

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                    if (stopDeepSearch) {
                        return;
                    }
                    Log.e("TAG", "下载图片失败");

                    updateProgress("部分抓取失败，目前总共抓取到" + imageBeans.size() + "张图片");

                }
            });


        }
    }

    /**
     * 操作深度抓取进度的设置
     */
    private void updateProgress(String text) {
        pb_pictures_loading.incrementProgressBy(1);

        if (pb_pictures_loading.getProgress() == pb_pictures_loading.getMax()) {
            pb_pictures_loading.setVisibility(View.GONE);
            btn_pictures_stopDownload.setVisibility(View.GONE);
            tv_pictures_msg.setText(text);
        }
    }

    private boolean stopDeepSearch = false;//是否停止抓取

    /**
     * 点击“停止抓取”button的回调方法
     *
     * @param v
     */
    public void stopSearch(View v) {
        stopDeepSearch = true;
        pb_pictures_loading.setVisibility(View.GONE);
        btn_pictures_stopDownload.setVisibility(View.GONE);
        tv_pictures_msg.setText("停止抓取，总共抓取到" + imageBeans.size() + "张图片");
    }

    /**
     * 过滤出有效链接
     *
     * @param links
     * @return
     */
    private List<String> getUseableLinks(Elements links) {
        //用于过滤重复url的集合
        HashSet<String> set = new HashSet<String>();
        //用于保存有效url的集合
        List<String> lstLinks = new ArrayList<String>();

        //遍历所有links,过滤,保存有效链接
        for (Element link : links) {
            String href = link.attr("href");// abs:href, "http://"
            //Log.i("spl","过滤前,链接:"+href);
            // 设置过滤条件
            if (href.equals("")) {
                continue;// 跳过
            }
            if (href.equals(url)) {
                continue;// 跳过
            }
            if (href.startsWith("javascript")) {
                continue;// 跳过
            }

            if (href.startsWith("/")) {
                href = url + href;
            }
            if (!set.contains(href)) {//使用HashSet实现过滤
                set.add(href);// 将有效链接保存至哈希表中
                lstLinks.add(href);
            }

            Log.i("spl", "有效链接:" + href);
        }
        return lstLinks;
    }


    /**
     * 解析url对应的html代码
     *
     * @param url
     * @param html
     */
    private void showImagesFromHtml(String url, String html) {

        List<ImageBean> list = parseHtml(url, html);//将解析得到的集合数据添加到现有的集合中
        imageBeans.addAll(list);//
        adapter.setList(imageBeans);
        adapter.notifyDataSetChanged();//更新界面数据

        selectCount = 0;//将选中的个数设置为0
        isEdit = false;


    }

    /**
     * 解析html代码得到集合数据：jsoup框架
     *
     * @param url
     * @param html
     * @return
     */
    private List<ImageBean> parseHtml(String url, String html) {

        List<ImageBean> list = new ArrayList<>();

        Document doc = Jsoup.parse(html);
        List<Element> imgs = doc.getElementsByTag("img");

        for (Element img : imgs) {
            String src = img.attr("src");
            if (src.toLowerCase().endsWith("jpg") || src.toLowerCase().endsWith("png")) {
                src = checkSrc(url, src);
                ImageBean imageBean = new ImageBean(src);
                //过滤重复的图片
                if (!imageUrlSet.contains(imageBean) && src.indexOf("/../") == -1) {
                    imageUrlSet.add(imageBean);
                    list.add(imageBean);
                }
            }
        }
        return list;

    }

    /**
     * 得到图片地址的绝对路径表示
     *
     * @param url
     * @param src
     * @return
     */
    private String checkSrc(String url, String src) {
        if (src.startsWith("http")) {//得到的src是绝对路径
            url = src;
        } else {
            if (src.startsWith("/")) {
                url = url + src;
            } else {
                url = url + "/" + src;
            }
        }
        return url;

    }

    private ProgressDialog dialog;

    /**
     * 提供一个显示信息的ProgressDialog
     *
     * @param horizontal
     * @param msg
     */

    private void showProgressDialog(boolean horizontal, String msg) {
        dialog = new ProgressDialog(this);
        if (horizontal) {
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//水平进度条
        } else {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//圆形进度条
        }

        dialog.setTitle("提示信息");
        dialog.setMessage(msg);
        dialog.show();
        ;
    }

    private String checkedUrlPre(String url) {
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }

        return url;

    }

    private int selectCount = 0;//选中的个数

    /**
     * 长按GridView中具体某个item的回调方法
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        if (!isEdit) {//查看状态
            iv_pictures_download.setVisibility(View.VISIBLE);
            cb_pictures_selected.setVisibility(View.VISIBLE);
            isEdit = true;
        }

        boolean isChecked = adapter.getItemCheckedStatus(position);
        selectCount += isChecked ? -1 : 1;
        //修改textview的显示
        tv_pictures_msg.setText(selectCount + "/" + imageBeans.size());

        //修改Item的选中状态
        adapter.changeItemStatus(position, isChecked);
        adapter.notifyDataSetChanged();


        return true;//消费掉此事件
    }

    /**
     * 点击GridView中具体某个item的回调方法
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isEdit) {//编辑状态
            boolean isChecked = adapter.getItemCheckedStatus(position);
            selectCount += isChecked ? -1 : 1;
            //修改textview的显示
            tv_pictures_msg.setText(selectCount + "/" + imageBeans.size());

            //修改Item的选中状态
            adapter.changeItemStatus(position, isChecked);
            adapter.notifyDataSetChanged();


        } else {//查看状态


            Intent intent = new Intent(this, DragPicturesActivity.class);
            //携带数据
            intent.putExtra("position", position);
            intent.putExtra("data", (ArrayList) imageBeans);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isEdit = false;
    }
}

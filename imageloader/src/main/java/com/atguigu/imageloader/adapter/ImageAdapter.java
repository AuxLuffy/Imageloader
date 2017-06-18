package com.atguigu.imageloader.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.atguigu.imageloader.R;
import com.atguigu.imageloader.bean.ImageBean;
import com.atguigu.imageloader.ui.WebPicturesActivity;
import com.atguigu.imageloader.util.AppUtils;

import org.xutils.x;

import java.util.List;

/**
 *用来装配从前一个界面获取的url网址得到的所有的下载以后图片的显示
 *
 * Created by shkstart on 2016/5/31.
 */
public class ImageAdapter extends BaseAdapter {

    private List<ImageBean> list;
    private Context context;

    public ImageAdapter(Context context){
        this.context = context;
    }

    public List<ImageBean> getList() {
        return list;
    }

    public void setList(List<ImageBean> list) {
        this.list = list;
    }


    public void addList(List list){
        this.list.addAll(list);
    }
    @Override
    public int getCount() {
        return (list == null)? 0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return (list == null)? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = View.inflate(context, R.layout.item_pictures,null);
            holder = new ViewHolder();
            holder.iv_item_pictures_icon = (ImageView) convertView.findViewById(R.id.iv_item_pictures_icon);
            holder.iv_item_pictures_selected = (ImageView) convertView.findViewById(R.id.iv_item_pictures_selected);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //装配数据
        ImageBean imageBean = list.get(position);
        Log.e("TAG",imageBean.toString());//url: http://wwww.atguigu.com/courses/android.jpg
        //使用xutils实现
        //联网加载图片显示/读取本地指定路径下的图片
        x.image().bind(holder.iv_item_pictures_icon,imageBean.getUrl(), AppUtils.smallImageOptions);

        //设置是否选中的显示
        if(WebPicturesActivity.isEdit){//编辑状态
            holder.iv_item_pictures_selected.setVisibility(View.VISIBLE);
            if(imageBean.isChecked()){//选中状态
                holder.iv_item_pictures_selected.setImageResource(R.drawable.blue_selected);
            }else{//未被选中状态
                holder.iv_item_pictures_selected.setImageResource(R.drawable.blue_unselected);
            }
        }else{//查看状态
            holder.iv_item_pictures_selected.setVisibility(View.GONE);
        }

        return convertView;
    }

    /**
     * 获取当前position位置的item的选中状态
     * @param position
     */
    public boolean getItemCheckedStatus(int position) {
        return list.get(position).isChecked();
    }

    /**
     * 修改item的选中状态
     * @param isChecked
     */
    public void changeItemStatus(int position,boolean isChecked) {
        list.get(position).setIsChecked(!isChecked);
    }
/**
 * 改变所有的item的选中状态
 */
    public void changeAllItemStatus(boolean isChecked) {
        for(int i = 0;i < list.size();i++){
            list.get(i).setIsChecked(isChecked);
        }
    }

    static class ViewHolder{
        ImageView iv_item_pictures_icon;
        ImageView iv_item_pictures_selected;
    }
}

package com.atguigu.imageloader.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.imageloader.R;
import com.atguigu.imageloader.bean.WebLink;

import java.util.List;

/**
 * Created by shkstart on 2016/5/31.
 */
public class MainAdapter extends BaseAdapter{

    private List<WebLink> list;
    private Context context;

    public MainAdapter(List<WebLink> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHoder;
        if(convertView == null){
            viewHoder = new ViewHolder();

            convertView = View.inflate(context, R.layout.item_main,null);

            viewHoder.iv_item_icon = (ImageView) convertView.findViewById(R.id.iv_item_icon);
            viewHoder.tv_item_name = (TextView) convertView.findViewById(R.id.tv_item_name);

            convertView.setTag(viewHoder);
        }else{
            viewHoder = (ViewHolder) convertView.getTag();
        }

        //装配数据
        WebLink webLink = list.get(position);
        viewHoder.iv_item_icon.setImageResource(webLink.getIcon());
        viewHoder.tv_item_name.setText(webLink.getName());

        return convertView;
    }

    static class ViewHolder{
        ImageView iv_item_icon;
        TextView tv_item_name;
    }
}

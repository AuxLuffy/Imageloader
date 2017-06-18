package com.atguigu.imageloader.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.atguigu.imageloader.R;
import com.atguigu.imageloader.util.AppUtils;

import org.xutils.x;

/**
 * Created by shkstart on 2016/6/3.
 */
public class ImgDetailFragment extends Fragment {

    private ImageView imageView;
    private String imagePath;//两种情况：①联网状态：url  ②本地状态：本地文件的路径


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null){
            imagePath = bundle.getString("imagepath");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        imageView = (ImageView) View.inflate(getActivity(), R.layout.fg_imagedetail,null);

        x.image().bind(imageView,imagePath, AppUtils.bigImageOptions);

        return imageView;
    }

    /**
     * 返回一个当前类的对象
     * @param url
     * @return
     */
    public static Fragment getInstance(String url) {
        ImgDetailFragment fragment = new ImgDetailFragment();

//        fragment.imagePath = url;

        Bundle bundle = new Bundle();
        bundle.putString("imagepath",url);
        fragment.setArguments(bundle);

        return fragment;
    }
}

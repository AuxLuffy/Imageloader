package com.atguigu.imageloader.bean;

import java.io.Serializable;

/**
 * Created by shkstart on 2016/5/31.
 */
public class ImageBean implements Serializable{//ImageBean而可序列化，保证在Intent中传输
    private String url;//http://sina.com/news/abc.jpg
    private boolean isChecked;//是否被选中

    public ImageBean(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public String toString() {
        return "ImageBean{" +
                "url='" + url + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }

    //重写equals()和hashCode()方法的目的是能够在集合中判断两个ImageBean是否相同
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageBean imageBean = (ImageBean) o;

        return !(url != null ? !url.equals(imageBean.url) : imageBean.url != null);

    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}

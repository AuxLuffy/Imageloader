package com.atguigu.imageloader.bean;

/**
 * Created by shkstart on 2016/5/31.
 */
public class WebLink {

    private String name;//网站名称
    private int icon;//图标
    private String url;//网址

    public WebLink() {
    }

    public WebLink(String name, int icon, String url) {
        this.name = name;
        this.icon = icon;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "WebLink{" +
                "name='" + name + '\'' +
                ", icon=" + icon +
                ", url='" + url + '\'' +
                '}';
    }
}

package com.atguigu.imageloader.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by shkstart on 2016/6/1.
 *
 * ORM思想:  数据库中的表与java类对应。
 *           数据表中的一个字段（或列）与java类的一个属性对应
 *           数据表中的一个行数据 与 java类的一个对象对应
 *
 *  orm:object relation mapping
 */
@Table(name="history_url")
public class HistoryUrl {
    @Column(name="_id",isId = true)
    private int id;
    @Column(name="url",isId = false)
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HistoryUrl() {

    }

    public HistoryUrl(int id,String url) {
        this.url = url;
        this.id = id;
    }

    @Override
    public String toString() {
        return "HistoryUrl{" +
                "id=" + id +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryUrl that = (HistoryUrl) o;

        return !(url != null ? !url.equals(that.url) : that.url != null);

    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}

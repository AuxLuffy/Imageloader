package com.atguigu.imageloader.dao;

import com.atguigu.imageloader.bean.HistoryUrl;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shkstart on 2016/6/1.
 */
public class HistoryUrlDao {

    private DbManager.DaoConfig daoConfig;

    public HistoryUrlDao(){
        daoConfig = new DbManager.DaoConfig()
                    .setDbName("image_loader.db")
                    .setDbVersion(1);
    }

    //查询
    public List<HistoryUrl> getAll(){

        DbManager dbManager = x.getDb(daoConfig);
        //try-catch快捷键：alt + shift + z
        List<HistoryUrl> list = null;
        try {
            list = dbManager.findAll(HistoryUrl.class);
        } catch (DbException e) {
            e.printStackTrace();
        } finally {
            try {
                dbManager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(list == null){
                list = new ArrayList();
            }


        }


        return list;

    }
    //添加
    public void insert(HistoryUrl historyUrl){
        DbManager dbManager = x.getDb(daoConfig);

        try {
            dbManager.saveBindingId(historyUrl);//在保存的同时，还可以更新historyUrl对象的id，使其与数据表_id一致
        } catch (DbException e) {
            e.printStackTrace();
        } finally {
            try {
                dbManager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}

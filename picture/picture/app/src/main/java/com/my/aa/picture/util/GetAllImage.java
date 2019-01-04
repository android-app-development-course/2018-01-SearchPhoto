package com.my.aa.picture.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;

import com.my.aa.picture.bean.ImgBean;

import java.util.ArrayList;
import java.util.List;

public class GetAllImage {
    public static List<ImgBean> getallImage(ContentResolver contentResolver){
        List<ImgBean>list=new ArrayList<>();
        Cursor cursor=contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,null,null,null);
        while (cursor.moveToNext()){
            String name=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            byte[]data=cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            String desc=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION));
            String path=new String(data,0,data.length-1);
            ImgBean imgBean=new ImgBean(name,path,desc);
            list.add(imgBean);
        }
        return list;
    }
}

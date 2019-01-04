package com.my.aa.picture.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.loopj.android.http.RequestParams;
import com.my.aa.picture.MainActivity;
import com.my.aa.picture.PictureToWordResults;
import com.my.aa.picture.ShowResults;
import com.my.aa.picture.bean.ImgBean;

import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Handler;

import android.app.ProgressDialog;

public class RequestThread extends Thread {
    private ProgressDialog progressDialog;
    private String filePath;
    private Context context;
    private int type;
    private String key;

    public RequestThread(String path, Context context, int type, String key, ProgressDialog progressDialog) {
        this.filePath = path;
        this.context = context;
        this.type = type;
        this.key = key;
        this.progressDialog = progressDialog;
    }

    @Override
    public void run() {
        String otherHost = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
        String filePath = this.filePath;

        try {
            /**
             * 线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
             */
            if (type == 1) {
                //文转图
                String accessToken = AuthService.getAuth();
                List<ImgBean> allImg = GetAllImage.getallImage(context.getContentResolver());
                Log.i("sizepicture", String.valueOf(allImg.size()));
                List<String> paths = new ArrayList<>();
                List<String> results = new ArrayList<>();
                for (int k = 0; k < allImg.size(); k++) {
                    byte[] imgData = FileUtil.readFileByBytes(allImg.get(k).getPath());
                    String imgStr = Base64Util.encode(imgData);
                    String params = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(imgStr, "UTF-8");
                    String result = HttpUtil.post(otherHost, accessToken, params);
                    paths.add(allImg.get(k).getPath());
                    results.add(result);
                }
                //匹配
                ArrayList<String> re = new ArrayList<>();
                for (int i = 0; i < results.size(); i++) {
                    if (results.get(i).contains(key))
                        re.add(paths.get(i));
                }
                Intent intent = new Intent(context, ShowResults.class);
                intent.putExtra("path", re);
                Log.i("re.size", String.valueOf(re.size()));
                progressDialog.cancel();
                context.startActivity(intent);
                re.clear();
                paths.clear();
                results.clear();
            }
            if (type == 2) {
                //调到图转文
                byte[] imgData = FileUtil.readFileByBytes(filePath);
                String imgStr = Base64Util.encode(imgData);
                String params = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(imgStr, "UTF-8");
                String accessToken = AuthService.getAuth();
//            Log.i("token2",accessToken);
                String result = HttpUtil.post(otherHost, accessToken, params);
                System.out.println("result" + "\n" + result);
                Intent intent = new Intent(context, PictureToWordResults.class);
                intent.putExtra("picturePath", result);
                progressDialog.cancel();
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

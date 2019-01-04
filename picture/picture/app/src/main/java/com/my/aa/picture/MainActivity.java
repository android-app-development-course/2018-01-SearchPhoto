package com.my.aa.picture;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.my.aa.picture.util.AuthService;
import com.my.aa.picture.util.OCRUtil;
import com.my.aa.picture.util.RequestThread;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitView();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        if (checkSelfPermission(Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
        }
//        Log.d("网络状态", String.valueOf(isNetworkConnected(this)));
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s=AuthService.getAuth();
                Log.d("token啊",s);
            }
        }).start();
*/
    }

    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 3;
    private ImageButton pictureToWord;//图片转文字按钮
    private ImageButton wordToPicture;//文字转图片按钮
    private Dialog dialog;
    private View inflate;
    private TextView choosePhoto;
    private TextView takePhoto;
    private TextView cancel;
    private static final int FROM_CAMERA = 1;
    private static final int FROM_PIC = 2;
    private ImageView img;
    private String mCurrentPhotoPath;
    private ProgressDialog mProgressDialog;
    private void InitView() {
        pictureToWord = (ImageButton) findViewById(R.id.PictureToWord);
        wordToPicture = (ImageButton) findViewById(R.id.WorDToPicture);
        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setMessage("处理中...");
        setOnClickListener();
    }

    private void setOnClickListener() {
        //图转文
        pictureToWord.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseDialog(v);   //启动对话框
            }
        });
        //文转图
        wordToPicture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WordToPicture.class);//启动该页面
                startActivity(intent);

            }
        });
    }

    private void showChooseDialog(View v) {
        dialog = new Dialog(this, R.style.DialogTheme);
        inflate = LayoutInflater.from(this).inflate(R.layout.choose_dialog, null);//与choose_dialog绑定
        choosePhoto = (TextView) inflate.findViewById(R.id.abroad_choosephoto);
        takePhoto = (TextView) inflate.findViewById(R.id.abroad_takephoto);
        cancel = (TextView) inflate.findViewById(R.id.abroad_choose_cancel);
        //本地选择
        choosePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "请到应用设置授予文件访问权限", Toast.LENGTH_LONG).show();
                    flag = false;
                }
                if (flag) {
                    choosePhoto();
                    dialog.dismiss();
                }
            }
        });
        //启动相机拍照
        takePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "请到应用设置授予相机权限", Toast.LENGTH_LONG).show();
                    flag = false;
                } else if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "请到应用设置授予文件访问以及写入权限", Toast.LENGTH_LONG).show();
                    flag = false;
                }
                if (flag) {
                    takePhoto();
                    dialog.dismiss();
                }
            }
        });
        //取消功能
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(inflate);
        Window window = dialog.getWindow();
        if (dialog != null && window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams attr = window.getAttributes();
            if (attr != null) {
                attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                attr.width = ViewGroup.LayoutParams.MATCH_PARENT;
                attr.gravity = Gravity.BOTTOM;//设置dialog 在布局中的位置
                window.setAttributes(attr);
            }
        }
        dialog.show();
    }

    private void takePhoto() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri mOrignUri;
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            File file = createImageFile();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mOrignUri = FileProvider.getUriForFile(getApplication(), getApplication().getPackageName() + ".FileProvider", file);
            } else {
                mOrignUri = Uri.fromFile(file);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mOrignUri);
            startActivityForResult(intent, FROM_CAMERA);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    public void choosePhoto() {
        Intent openAlbumIntent = new Intent(Intent.ACTION_PICK);
        openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(openAlbumIntent, FROM_PIC);
    }

    //创建路径
    public File createImageFile() throws IOException {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d("my", "no SDCARD!!!!");
        } else {
            String pre = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_SSS")
                    .format(new Date());
            //创建图片保存的位置
            File path = new File(Environment.getExternalStorageDirectory(),
                    "myImage");
            //如果路径不存在
            if (!path.exists()) {
                //创建路径，需要权限
                path.mkdirs();
            }
            //组合全部的路径
            mCurrentPhotoPath = path.toString() + File.separator + pre + ".jpg";
            Log.d("my", mCurrentPhotoPath);
            File file = new File(mCurrentPhotoPath);
            return file;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FROM_CAMERA && resultCode == RESULT_OK) {
            mProgressDialog.show();
            RequestThread requestThread=new RequestThread(mCurrentPhotoPath,this,2,null,mProgressDialog);
            requestThread.start();
            /*
            Intent intent = new Intent(this, PictureToWordResults.class);
            intent.putExtra("picturePath", mCurrentPhotoPath);
            startActivity(intent);
            */
        }
        //从图库选择返回的结果
        if (requestCode == FROM_PIC && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            Log.d("my", uri.toString());
            //要返回的列名
            Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null,null);
            if (cursor.moveToFirst()) {
                String ImgPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                img.setImageBitmap(BitmapFactory.decodeFile(ImgPath));
                cursor.close();
                mProgressDialog.show();
               RequestThread requestThread=new RequestThread(ImgPath,this,2,null,mProgressDialog);
               requestThread.start();
               /* Intent intent = new Intent(this, PictureToWordResults.class);
                intent.putExtra("picturePath", ImgPath);
                startActivity(intent);
                */
            }


        }

    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }
}




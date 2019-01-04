package com.my.aa.picture;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class ChooseDialog extends Activity implements OnClickListener {
    private static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private TextView cancel;    //取消
    private TextView takePhoto;   //拍照
    private TextView choosePhoto; //本地选择
    private Dialog dialog;
    private Bitmap bitmap;
    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chosePhotoDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.abroad_choosephoto:
                pickAlbum();
                break;
            case R.id.abroad_takephoto:
                takePhotos();
                break;
            case R.id.abroad_choose_cancel:
                dialog.dismiss();
                finish();
                break;
            default:
                dialog.dismiss();
                finish();
        }

    }


    public void chosePhotoDialog() {
        dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        View inflate = LayoutInflater.from(this).inflate(R.layout.choose_dialog, null);
        choosePhoto = (TextView) inflate.findViewById(R.id.abroad_choosephoto);
        takePhoto = (TextView) inflate.findViewById(R.id.abroad_takephoto);
        cancel = (TextView) inflate.findViewById(R.id.abroad_choose_cancel);
        choosePhoto.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        cancel.setOnClickListener(this);
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

    //相册选取
    private void pickAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "images/*");
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    //拍照
    protected void takePhotos() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    /*
    图片选择后，最终都会把数据返回到onActivityResult（）方法里面，所以我们需要在activity里面重写此方法
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_GALLERY:
                if (data != null) {

                }
                break;
            case PHOTO_REQUEST_CAREMA:
                if (resultCode == RESULT_CANCELED) {
                    return;
                }
                if (data != null) {
                    Bitmap photo = data.getParcelableExtra("data");
                    //将Bitmap转化为uri
                    Uri uri = saveBitmap(photo, "temp");
                    //启动图像裁剪
                    cropPhoto(uri);
                }
                break;
            case PHOTO_REQUEST_CUT:
                //LogUtil.d("abroadUseActivity2", "裁剪");
                // 从剪切图片返回的数据
                if (data == null) {
                    return;
                }
                bitmap = data.getParcelableExtra("data");
                if (bitmap == null) {//
                    return;
                }
                // TODO 此处我们便获得了bitmap对象，做其他操作
                bitmap.recycle();
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
    剪裁方法
     */
    private void cropPhoto(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /**
     * 把bitmap保存到本地
     *
     * @param bm      bitmap
     * @param dirPath 路径
     * @return 文件的uri
     */
    private Uri saveBitmap(Bitmap bm, String dirPath) {
        //新建文件夹用于存放裁剪后的图片
        File tmpDir = new File(Environment.getExternalStorageDirectory() + "/" + dirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }
        //新建文件存储裁剪后的图片
        File img = new File(tmpDir.getAbsolutePath() + "/feedback.png");
        try {
            //打开文件输出流
            FileOutputStream fos = new FileOutputStream(img);
            //将bitmap压缩后写入输出流(参数依次为图片格式、图片质量和输出流)
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            //返回File类型的Uri
            return Uri.fromFile(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

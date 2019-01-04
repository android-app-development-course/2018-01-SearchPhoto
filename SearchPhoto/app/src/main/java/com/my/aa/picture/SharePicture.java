package com.my.aa.picture;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.my.aa.picture.bean.ImgBean;

//import org.asynchttpclient.AsyncHttpClient;

public class SharePicture extends Activity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_picture);
        init();
    }
    private Button more;
    private TextView textView;
    private ImageView pictureDetail;
    private void init() {
        Intent intent = getIntent();
        final String path = intent.getStringExtra("path");
        more = (Button) findViewById(R.id.more);
        more.setText("分享");
        textView = (TextView) findViewById(R.id.title);
        textView.setText("图片详情");


        pictureDetail = (ImageView) findViewById(R.id.pictureDetail);
        pictureDetail.setImageBitmap(BitmapFactory.decodeFile(path));
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            //分享
            public void onClick(View v) {
                Intent imageIntent = new Intent(Intent.ACTION_SEND);
                imageIntent.setType("image/jpg");
                imageIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                startActivity(Intent.createChooser(imageIntent, "分享"));
            }
        });
    }
}

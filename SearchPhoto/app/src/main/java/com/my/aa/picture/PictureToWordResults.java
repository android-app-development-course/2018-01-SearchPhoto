package com.my.aa.picture;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.my.aa.picture.util.RequestThread;

import java.sql.BatchUpdateException;

public class PictureToWordResults extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_to_word);
        init();
    }

    private Button back;
    private TextView title;
    private Button more;
    private String picturePath;
    private Button copy;
    private TextView resultWords;
    private void init() {
        back = (Button) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        more = (Button) findViewById(R.id.more);
        copy=(Button)findViewById(R.id.copy);
        resultWords=(TextView)findViewById(R.id.resultWord);
        more.setText("");
        more.setEnabled(false);
        title.setText("识别结果");
        Intent intent = getIntent();
        picturePath = (String)intent.getStringExtra("picturePath");
        resultWords.setText(picturePath);
        if (!picturePath.isEmpty())
            Log.d("picturePath: ", picturePath);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result=resultWords.getText().toString();
                if(!result.isEmpty()) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText(null, result);
                    cm.setPrimaryClip(clipData);
                    Toast.makeText(PictureToWordResults.this,"复制成功",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

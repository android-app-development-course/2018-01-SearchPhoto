package com.my.aa.picture;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.my.aa.picture.util.RequestThread;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class WordToPicture extends Activity {

    private ListView listView;      //列表
    private EditText searchText;
    private Button serchButton;
    private TextView textView;    //标题栏
    private ImageView iv_clear;   //若隐若现删除键
    private List<String> data = new ArrayList<>();  //ListView数据源
    private String str;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_to_picture);
        Intent intent = getIntent();
        String a = intent.getStringExtra("hha");
        init();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("处理中...");
    }

    private void init() {
        searchText = (EditText) findViewById(R.id.searchText);
        serchButton = (Button) findViewById(R.id.buttonSearch);
        textView = (TextView) findViewById(R.id.title);
        iv_clear = (ImageView) findViewById(R.id.iv_clear);
        textView.setText("搜索图片");
        iv_clear.setOnClickListener(new OnClickListener() {
            /*
            清空搜索栏
             */
            @Override
            public void onClick(View v) {
                searchText.setText("");
            }
        });
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    iv_clear.setVisibility(GONE);
                } else {
                    iv_clear.setVisibility(VISIBLE);
                }
                str = s.toString();
            }
        });
        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                } else {

                }
            }
        });
        serchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                str = searchText.getText().toString();
                if (str.isEmpty()) {
                    Toast.makeText(WordToPicture.this, "请输入搜索的关键字", Toast.LENGTH_SHORT).show();
                } else {
                    mProgressDialog.show();
                    RequestThread requestThread = new RequestThread(null, WordToPicture.this, 1, str,mProgressDialog);
                    requestThread.start();
                    /*
                    Intent intent = new Intent(WordToPicture.this, ShowResults.class);
                    intent.putExtra("key", str);
                    startActivity(intent);
                    */
                }
            }
        });
    }
}


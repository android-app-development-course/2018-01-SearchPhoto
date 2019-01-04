package com.my.aa.picture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.my.aa.picture.bean.ImgBean;
import com.my.aa.picture.util.GetAllImage;
import com.my.aa.picture.util.RequestThread;

import java.util.ArrayList;
import java.util.List;

public class ShowResults extends Activity {
    private Button back;
    private TextView title;
    private Button more;
    private RecyclerView recyclerView;
    private ArrayList<String> allImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_show);
        Intent intent = getIntent();
        allImg= intent.getStringArrayListExtra("path");
        Log.i("msgmsgmsg",allImg.toString());
        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        more = (Button) findViewById(R.id.more);
        title.setText("匹配结果");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //测试用了，应该用来分享按钮

            }
        });

        List<ImgBean>resultImg=new ArrayList<>();
        for(int i=0;i<allImg.size();i++){
            ImgBean imgBean=new ImgBean(null,allImg.get(i),null);
            resultImg.add(imgBean);
        }
        //allImg = new ArrayList<>();//图库中所有图片路径
        //访问全部图片
        //allImg = GetAllImage.getallImage(getContentResolver());
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        ResultsAdapter resultsAdapter = new ResultsAdapter(this, resultImg);
        resultsAdapter.setOnItemClickListener(new ResultsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String data) {
                //Toast.makeText(ShowResults.this,"您点击了"+position,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ShowResults.this, SharePicture.class);
                intent.putExtra("path", data);
                startActivity(intent);
            }
        });
        resultsAdapter.setOnItemLongClickListener(new ResultsAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position, String data) {
                //Toast.makeText(ShowResults.this,"您长按了"+data,Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(resultsAdapter);
    }

}

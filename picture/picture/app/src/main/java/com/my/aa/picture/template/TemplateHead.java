package com.my.aa.picture.template;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.my.aa.picture.R;

public class TemplateHead extends LinearLayout {
    public TemplateHead(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.head_template, null);
        //把布局添加到当前控件中
        ViewGroup.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(view, params);
        init();
    }

    private Button back;
    private Button more;
    private TextView title;

    public void init() {
        back = (Button) findViewById(R.id.back);
        more = (Button) findViewById(R.id.more);
        title = (TextView) findViewById(R.id.title);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回一级页面
                Activity activity = (Activity) getContext();
                activity.finish();
            }
        });
        more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}

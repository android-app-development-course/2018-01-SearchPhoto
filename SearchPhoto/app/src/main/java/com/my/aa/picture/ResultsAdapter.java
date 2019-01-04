package com.my.aa.picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.my.aa.picture.bean.ImgBean;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.MyViewHolder> {
    private List<ImgBean>listItem;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private Context context;
    public ResultsAdapter(Context context,List<ImgBean>listItem){
        this.listItem=listItem;
        this.context=context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        MyViewHolder holder=new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item,parent,false));
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        ImgBean imgBean=listItem.get(i);
        String url=imgBean.getPath();
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .dontAnimate()
//                .bitmapTransform(new ColorFilterTransformation(context, 0x7900CCCC))
                .override(400,400)
                ;
        Glide.with(context)
                .asBitmap()
                .apply(options)
                .load(url)
                .into(myViewHolder.imgView);
//        Bitmap bitmap=BitmapFactory.decodeFile(imgBean.getPath());
//        myViewHolder.imgView.setImageBitmap(bitmap);
        int adapterPositon =myViewHolder.getAdapterPosition();
        if(onItemClickListener!=null){
            myViewHolder.itemView.setOnClickListener(new MyOnClickListener(i,listItem.get(adapterPositon).getPath()));
        }
        if(onItemLongClickListener!=null){
            myViewHolder.itemView.setOnLongClickListener(new MyOnLongClickListener(i,listItem.get(adapterPositon).getPath()));
        }
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imgView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView=(ImageView)itemView.findViewById(R.id.result);
        }
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
    /*
    长按事件
     */
    private class MyOnLongClickListener implements View.OnLongClickListener {
        private int position;
        private String data;

        public MyOnLongClickListener(int position, String data) {
            this.position = position;
            this.data = data;
        }

        @Override
        public boolean onLongClick(View v) {
            onItemLongClickListener.onItemLongClick(v, position, data);
            return true;
        }
    }
    /*
    点击事件
     */
    private class MyOnClickListener implements View.OnClickListener {
        private int position;
        private String data;

        public MyOnClickListener(int position, String data) {
            this.position = position;
            this.data = data;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v, position, data);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position, String data);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position, String data);
    }
}

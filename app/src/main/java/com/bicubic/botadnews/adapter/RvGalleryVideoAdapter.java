package com.bicubic.botadnews.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bicubic.botadnews.R;
import com.bicubic.botadnews.activity.VideoViewActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.FileDescriptorBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;

import java.io.File;
import java.util.List;

/**
 * Created by admin on 20-Oct-16.
 */

public class RvGalleryVideoAdapter extends RecyclerView.Adapter<RvGalleryVideoAdapter.NewsHolder> {

    List<File> fileList;
    private static Context context;

    public RvGalleryVideoAdapter(List<File> fileList, Context context) {
        this.fileList = fileList;
        RvGalleryVideoAdapter.context = context;
    }

    @Override
    public NewsHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_video_row, viewGroup, false);
        NewsHolder newsHolder = new NewsHolder(v);
        return newsHolder;
    }

    @Override
    public void onBindViewHolder(final NewsHolder holder, int position) {

        String sdcard_path = Environment.getExternalStorageDirectory().getPath();
        String fileName = fileList.get(position).getName();
        Bitmap bmThumbnail;

//MICRO_KIND, size: 96 x 96 thumbnail
       /* bmThumbnail = ThumbnailUtils.createVideoThumbnail(sdcard_path + "/Botad News/Videos/" + fileName, MediaStore.Images.Thumbnails.MICRO_KIND);
        holder.img_main.setImageBitmap(bmThumbnail);*/


        BitmapPool bitmapPool = Glide.get(context).getBitmapPool();
        int microSecond = 6000000;// 6th second as an example
        VideoBitmapDecoder videoBitmapDecoder = new VideoBitmapDecoder(microSecond);
        FileDescriptorBitmapDecoder fileDescriptorBitmapDecoder = new FileDescriptorBitmapDecoder(videoBitmapDecoder, bitmapPool, DecodeFormat.PREFER_ARGB_8888);
        Glide.with(context)
                .load(sdcard_path + "/Botad News/Videos/" + fileName)
                .asBitmap()
                .override(250,250)// Example
                .videoDecoder(fileDescriptorBitmapDecoder)
                .into(holder.img_main);


    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public class NewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img_main;
        TextView tv_news;
        Button btn_play;
        private DownloadManager downloadManager;

        public NewsHolder(View itemView) {
            super(itemView);

            btn_play = (Button) itemView.findViewById(R.id.btn_play);
//            tv_news = (TextView) itemView.findViewById(R.id.tv_news);
            img_main = (ImageView) itemView.findViewById(R.id.img_main);

            btn_play.setOnClickListener(this);
            img_main.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            /*if (v.getId() == btn_play.getId()) {

                String fileName = fileList.get(getAdapterPosition()).getName();

                Toast.makeText(context, fileName, Toast.LENGTH_SHORT).show();

                String sdcard_path = Environment.getExternalStorageDirectory().getPath();
//                File file = new File(sdcard_path + "/Botad News/Videos" + Name + ".mp4");

                Bundle gameData = new Bundle();
                gameData.putString("file", sdcard_path + "/Botad News/Videos/" + fileName);
                Intent intent = new Intent(context, VideoViewActivity.class);
                intent.putExtras(gameData);
                context.startActivity(intent);

            } else*/
            if (v.getId() == img_main.getId()) {

                String fileName = fileList.get(getAdapterPosition()).getName();

                Toast.makeText(context, fileName, Toast.LENGTH_SHORT).show();

                String sdcard_path = Environment.getExternalStorageDirectory().getPath();
//                File file = new File(sdcard_path + "/Botad News/Videos" + Name + ".mp4");

                Bundle gameData = new Bundle();
                gameData.putString("file", sdcard_path + "/Botad News/Videos/" + fileName);
                Intent intent = new Intent(context, VideoViewActivity.class);
                intent.putExtras(gameData);
                context.startActivity(intent);

            }
        }


    }


}

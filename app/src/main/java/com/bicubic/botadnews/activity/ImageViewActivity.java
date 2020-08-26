package com.bicubic.botadnews.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

import com.bicubic.botadnews.R;
import com.bicubic.botadnews.utils.TouchImageView;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ImageViewActivity extends Activity{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_view);
        TouchImageView view = (TouchImageView) findViewById(R.id.imageView);
        Bundle gameData = getIntent().getExtras();

        if (gameData != null) {
            String imagefile = gameData.getString("file", "null");

            if (isLocalFile(imagefile)) {

                File file = new File(gameData.getString("file", "null"));
                Picasso.with(ImageViewActivity.this)
//                    .load("http://192.168.1.201/botad/uploads/item/round.png")
                        .load(file)
//                        .fit()
//                        .placeholder(R.drawable.progress_animation)
                        .into(view);

            } else {
                Picasso.with(ImageViewActivity.this)
//                    .load("http://192.168.1.201/botad/uploads/item/round.png")
                        .load(imagefile)
//                        .fit()
//                        .placeholder(R.drawable.progress_animation)
                        .into(view);
            }

        }


        view.setOnTouchImageViewListener(new TouchImageView.OnTouchImageViewListener() {

            @Override
            public void onMove() {

            }
        });

    }



    public static boolean isLocalFile(String path) {
        return new File(path).exists();
    }


}
package com.bicubic.botadnews.activity;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.MediaController;

import com.bicubic.botadnews.R;

public class VideoViewActivity extends AppCompatActivity {

    // Declare variables
    ProgressDialog pDialog;
    VideoView videoview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_view);
        videoview = (VideoView) findViewById(R.id.videoView);
        // Create a progressbar
        pDialog = new ProgressDialog(VideoViewActivity.this);
        // Set progressbar title
//        pDialog.setTitle("Android Video Streaming Tutorial");
        // Set progressbar message
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        // Show progressbar
        pDialog.show();

        Bundle gameData = getIntent().getExtras();

        if (gameData != null) {

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    VideoViewActivity.this);
            mediacontroller.setAnchorView(videoview);

            String file = gameData.getString("file",null);
            // Get the URL from String VideoURL
//            Uri video = Uri.parse("http://192.168.1.201/botad/uploads/news/1478502322_1651245825.mp4");
//            Uri video = Uri.parse("http://192.168.1.201/botad/uploads/news/1478502619_1731315177.mp4");
            Uri video = Uri.parse(file);
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
            pDialog.dismiss();
        }

    }
        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and video the video
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                pDialog.dismiss();
                videoview.start();
            }
        });

        MediaPlayer.OnErrorListener vidVwErrorListener = new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {       //if there was an error in trying to play the intro video

                Toast.makeText(VideoViewActivity.this, "File format not supported", Toast.LENGTH_SHORT).show();

                pDialog.dismiss();

                return true;
            }
        };

//        pDialog.dismiss();
    }


}

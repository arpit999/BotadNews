package com.bicubic.botadnews.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bicubic.botadnews.R;
import com.bicubic.botadnews.activity.ImageViewActivity;
import com.bicubic.botadnews.activity.YoutubeViewActivity;
import com.bicubic.botadnews.model.News;
import com.bicubic.botadnews.utils.ConnectivityReceiver;
import com.bicubic.botadnews.utils.DownloadTask;
import com.bicubic.botadnews.utils.VideoRequestHandler;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;

/**
 * Created by admin on 20-Oct-16.
 */

public class RvHomeAdapter extends RecyclerView.Adapter<RvHomeAdapter.NewsHolder> {

    List<News> newsArrayList;
    private static Context context;
    private static long downloadReference;
    VideoRequestHandler videoRequestHandler;
    Picasso picassoInstance;

    public RvHomeAdapter(List<News> newsArrayList, Context context) {
        this.newsArrayList = newsArrayList;
        RvHomeAdapter.context = context;
//        mmr = new FFmpegMediaMetadataRetriever();
    }


    @Override
    public NewsHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_row, viewGroup, false);
        NewsHolder newsHolder = new NewsHolder(v);
        return newsHolder;
    }

    @Override
    public void onBindViewHolder(final NewsHolder holder, final int position) {


        if (newsArrayList.get(position).getType().equals("image")) {

            holder.btn_play.setBackgroundResource(R.drawable.image);

            try {
                Picasso.with(context)
                        .load(newsArrayList.get(position).getThumb_image())
                        .fit()
                        .into(holder.img_main);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (newsArrayList.get(position).getType().equals("video")) {

            try {

                try {
                    Picasso.with(context)
                            .load("http://img.youtube.com/vi/"+newsArrayList.get(position).getLink()+"/0.jpg")
                            .fit()
                            .into(holder.img_main);
                } catch (Exception e) {
                    e.printStackTrace();
                }


               /* mmr.setDataSource(newsArrayList.get(position).getLink());
                mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
                mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
                Bitmap b = mmr.getFrameAtTime(20000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST); // frame at 2 seconds
//                byte [] artwork = mmr.getEmbeddedPicture();

                mmr.release();

                holder.img_main.setImageBitmap(b);*/


            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            holder.btn_play.setBackgroundResource(R.drawable.video);
//            holder.img_main.setImageBitmap(null);


        }
        Typeface font = Typeface.createFromAsset(context.getResources().getAssets(), "saumil_guj.ttf");
        holder.tv_news.setTypeface(font);
        holder.tv_news.setText(newsArrayList.get(position).getTitle());


    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static Bitmap retriveVideoFrameFromVideo(String videoPath)
            throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)"
                            + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }

        Log.e("", "retriveVideoFrameFromVideo: bitmap = " + bitmap);

        return bitmap;
    }


    @Override
    public int getItemCount() {
        return newsArrayList.size();
    }

    public class NewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img_download, img_share, img_main;
        TextView tv_news;
        Button btn_play;
        private DownloadManager downloadManager;

        public NewsHolder(View itemView) {
            super(itemView);

            btn_play = (Button) itemView.findViewById(R.id.btn_play);
            img_download = (ImageView) itemView.findViewById(R.id.img_download);
            img_share = (ImageView) itemView.findViewById(R.id.img_share);
            tv_news = (TextView) itemView.findViewById(R.id.tv_news);
            img_main = (ImageView) itemView.findViewById(R.id.img_main);

            img_main.setImageDrawable(null);
            btn_play.setOnClickListener(this);
            img_download.setOnClickListener(this);
            img_share.setOnClickListener(this);
            img_main.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == img_download.getId()) {

                String link = newsArrayList.get(getAdapterPosition()).getLink();
                final String FileType = link.substring(link.lastIndexOf(".") + 1);
                System.out.println(link.substring(link.lastIndexOf(".") + 1));


                if (newsArrayList.get(getAdapterPosition()).getType().equals("image")) {

                    File extStore = Environment.getExternalStorageDirectory();
                    File myFile = new File(extStore.getAbsolutePath() + "/Botad News/Images/" + newsArrayList.get(getAdapterPosition()).getTitle() + "." + FileType);

                    if (!myFile.exists()) {
                        if (ConnectivityReceiver.isConnected()) {
                            DownloadTask downloadTask = new DownloadTask(context);
                            downloadTask.execute(newsArrayList.get(getAdapterPosition()).getLink(), "" + newsArrayList.get(getAdapterPosition()).getTitle() + "." + FileType, newsArrayList.get(getAdapterPosition()).getType());
                        } else {
                            Toast.makeText(context, context.getString(R.string.please_connect_to_internet), Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        new AlertDialog.Builder(context)
                                .setTitle(R.string.app_name)
                                .setMessage(R.string.file_already_exists)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                })
                                .setIcon(R.mipmap.ic_launcher)
                                .show();

//                        Toast.makeText(context, "File already exists", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    File extStore = Environment.getExternalStorageDirectory();
                    File myFile = new File(extStore.getAbsolutePath() + "/Botad News/Videos/" + newsArrayList.get(getAdapterPosition()).getTitle() + "." + FileType);

                    if (!myFile.exists()) {
                        if (ConnectivityReceiver.isConnected()) {

                            String youtubeLink = "https://www.youtube.com/watch?v="+newsArrayList.get(getAdapterPosition()).getLink();

                            YouTubeUriExtractor ytEx = new YouTubeUriExtractor(context) {
                                @Override
                                public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                                    if (ytFiles != null) {
                                        int itag = 22;
                                        String downloadUrl = ytFiles.get(itag).getUrl();
//                                        Toast.makeText(context, "url : "+downloadUrl, Toast.LENGTH_SHORT).show();

                                        DownloadTask downloadTask = new DownloadTask(context);
                                        downloadTask.execute(downloadUrl, "" + newsArrayList.get(getAdapterPosition()).getTitle() + "." + FileType, newsArrayList.get(getAdapterPosition()).getType());


                                    }
                                }
                            };

                            ytEx.execute(youtubeLink);



//                            new YouTubePageStreamUriGetter().execute("https://www.youtube.com/watch?v=4GuqB1BQVr4");


//                            DownloadTask downloadTask = new DownloadTask(context);
//                            downloadTask.execute(newsArrayList.get(getAdapterPosition()).getLink(), "" + newsArrayList.get(getAdapterPosition()).getTitle() + "." + FileType, newsArrayList.get(getAdapterPosition()).getType());

                        } else {
                            Toast.makeText(context, context.getString(R.string.please_connect_to_internet), Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        new AlertDialog.Builder(context)
                                .setTitle(R.string.app_name)
                                .setMessage("File already exists !")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                })
                                .setIcon(R.mipmap.ic_launcher)
                                .show();

//                        Toast.makeText(context, "File already exists", Toast.LENGTH_SHORT).show();
                    }

                }


            } else if (v.getId() == img_share.getId()) {


                if (newsArrayList.get(getAdapterPosition()).getType().equals("video")) {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
//                    intent.setType("text/plain");
                    intent.setType("video/*");
                    intent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=" + newsArrayList.get(getAdapterPosition()).getLink());
                    context.startActivity(Intent.createChooser(intent, "Share"));

                } else {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
//                    intent.setType("text/plain");
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_TEXT, newsArrayList.get(getAdapterPosition()).getLink());
                    context.startActivity(Intent.createChooser(intent, "Share"));

                }


            } else if (v.getId() == btn_play.getId()) {

                if (newsArrayList.get(getAdapterPosition()).getType().contains("image")) {
//                    Toast.makeText(v.getContext(), "button image", Toast.LENGTH_SHORT).show();
                    Bundle gameData = new Bundle();
                    gameData.putString("file", newsArrayList.get(getAdapterPosition()).getLink());

                    Intent intent = new Intent(context, ImageViewActivity.class);
                    intent.putExtras(gameData);
                    context.startActivity(intent);

                } else {
                    if (ConnectivityReceiver.isConnected()) {
                        Bundle gameData = new Bundle();
                        gameData.putString("file", newsArrayList.get(getAdapterPosition()).getLink());
//                    Toast.makeText(v.getContext(), "button video", Toast.LENGTH_SHORT).show();
//                        gameData.putString("file", "fhWaJi1Hsfo");
//                        gameData.putString("file", "ywS4lTQDeCU");

//                        Intent intent = new Intent(context, VideoViewActivity.class);
                        Intent intent = new Intent(context, YoutubeViewActivity.class);
                        intent.putExtras(gameData);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, context.getString(R.string.please_connect_to_internet), Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
//                Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }
        }


    }








   /* class Meta {
        public String num;
        public String type;
        public String ext;

        Meta(String num, String ext, String type) {
            this.num = num;
            this.ext = ext;
            this.type = type;
        }
    }

    class Video {
        public String ext = "";
        public String type = "";
        public String url = "";

        Video(String ext, String type, String url) {
            this.ext = ext;
            this.type = type;
            this.url = url;
        }
    }

    public ArrayList<Video> getStreamingUrisFromYouTubePage(String ytUrl)
            throws IOException {
        if (ytUrl == null) {
            return null;
        }

        // Remove any query params in query string after the watch?v=<vid> in
        // e.g.
        // http://www.youtube.com/watch?v=0RUPACpf8Vs&feature=youtube_gdata_player
        int andIdx = ytUrl.indexOf('&');
        if (andIdx >= 0) {
            ytUrl = ytUrl.substring(0, andIdx);
        }

        // Get the HTML response
        String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:8.0.1)";
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                userAgent);
        HttpGet request = new HttpGet(ytUrl);
        HttpResponse response = client.execute(request);
        String html = "";
        InputStream in = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder str = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            str.append(line.replace("\\u0026", "&"));
        }
        in.close();
        html = str.toString();

        // Parse the HTML response and extract the streaming URIs
        if (html.contains("verify-age-thumb")) {
//            Log.e("YouTube is asking for age verification. We can't handle that sorry.");
            Log.d(TAG, "getStreamingUrisFromYouTubePage() called with: ytUrl = [" + ytUrl + "]");
            return null;
        }

        if (html.contains("das_captcha")) {
//            CLog.w("Captcha found, please try with different IP address.");
            Log.d(TAG, "getStreamingUrisFromYouTubePage() called with: ytUrl = [" + ytUrl + "]");
            return null;
        }

        Pattern p = Pattern.compile("stream_map\": \"(.*?)?\"");
        // Pattern p = Pattern.compile("/stream_map=(.[^&]*?)\"/");
        Matcher m = p.matcher(html);
        List<String> matches = new ArrayList<String>();
        while (m.find()) {
            matches.add(m.group());
        }

        if (matches.size() != 1) {
//            CLog.w("Found zero or too many stream maps.");
            Log.d(TAG, "getStreamingUrisFromYouTubePage() called with: ytUrl = [" + ytUrl + "]");
            return null;
        }

        String urls[] = matches.get(0).split(",");
        HashMap<String, String> foundArray = new HashMap<String, String>();
        for (String ppUrl : urls) {
            String url = URLDecoder.decode(ppUrl, "UTF-8");

            Pattern p1 = Pattern.compile("itag=([0-9]+?)[&]");
            Matcher m1 = p1.matcher(url);
            String itag = null;
            if (m1.find()) {
                itag = m1.group(1);
            }

            Pattern p2 = Pattern.compile("sig=(.*?)[&]");
            Matcher m2 = p2.matcher(url);
            String sig = null;
            if (m2.find()) {
                sig = m2.group(1);
            }

            Pattern p3 = Pattern.compile("url=(.*?)[&]");
            Matcher m3 = p3.matcher(ppUrl);
            String um = null;
            if (m3.find()) {
                um = m3.group(1);
            }

            if (itag != null && sig != null && um != null) {
                foundArray.put(itag, URLDecoder.decode(um, "UTF-8") + "&"
                        + "signature=" + sig);
            }
        }

        if (foundArray.size() == 0) {
//            CLog.w("Couldn't find any URLs and corresponding signatures");
            Log.d(TAG, "getStreamingUrisFromYouTubePage() called with: ytUrl = [" + ytUrl + "]");
            return null;
        }

        HashMap<String, Meta> typeMap = new HashMap<String, Meta>();
        typeMap.put("13", new Meta("13", "3GP", "Low Quality - 176x144"));
        typeMap.put("17", new Meta("17", "3GP", "Medium Quality - 176x144"));
        typeMap.put("36", new Meta("36", "3GP", "High Quality - 320x240"));
        typeMap.put("5", new Meta("5", "FLV", "Low Quality - 400x226"));
        typeMap.put("6", new Meta("6", "FLV", "Medium Quality - 640x360"));
        typeMap.put("34", new Meta("34", "FLV", "Medium Quality - 640x360"));
        typeMap.put("35", new Meta("35", "FLV", "High Quality - 854x480"));
        typeMap.put("43", new Meta("43", "WEBM", "Low Quality - 640x360"));
        typeMap.put("44", new Meta("44", "WEBM", "Medium Quality - 854x480"));
        typeMap.put("45", new Meta("45", "WEBM", "High Quality - 1280x720"));
        typeMap.put("18", new Meta("18", "MP4", "Medium Quality - 480x360"));
        typeMap.put("22", new Meta("22", "MP4", "High Quality - 1280x720"));
        typeMap.put("37", new Meta("37", "MP4", "High Quality - 1920x1080"));
        typeMap.put("33", new Meta("38", "MP4", "High Quality - 4096x230"));

        ArrayList<Video> videos = new ArrayList<RvHomeAdapter.Video>();

        for (String format : typeMap.keySet()) {
            Meta meta = typeMap.get(format);

            if (foundArray.containsKey(format)) {
                Video newVideo = new Video(meta.ext, meta.type,
                        foundArray.get(format));
                videos.add(newVideo);
//                CLog.d("YouTube Video streaming details: ext:" + newVideo.ext
//                        + ", type:" + newVideo.type + ", url:" + newVideo.url);
                Log.d(TAG, "getStreamingUrisFromYouTubePage() called with: ytUrl = [" + ytUrl + "]");
            }
        }

        return videos;
    }



    private class YouTubePageStreamUriGetter extends
            AsyncTask<String, String, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "",
                    "Connecting to YouTube...", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            try {
                ArrayList<Video> videos = getStreamingUrisFromYouTubePage(url);
                if (videos != null && !videos.isEmpty()) {
                    String retVidUrl = null;
                    for (Video video : videos) {
                        if (video.ext.toLowerCase().contains("mp4")
                                && video.type.toLowerCase().contains("medium")) {
                            retVidUrl = video.url;
                            break;
                        }
                    }
                    if (retVidUrl == null) {
                        for (Video video : videos) {
                            if (video.ext.toLowerCase().contains("3gp")
                                    && video.type.toLowerCase().contains(
                                    "medium")) {
                                retVidUrl = video.url;
                                break;

                            }
                        }
                    }
                    if (retVidUrl == null) {

                        for (Video video : videos) {
                            if (video.ext.toLowerCase().contains("mp4")
                                    && video.type.toLowerCase().contains("low")) {
                                retVidUrl = video.url;
                                break;

                            }
                        }
                    }
                    if (retVidUrl == null) {
                        for (Video video : videos) {
                            if (video.ext.toLowerCase().contains("3gp")
                                    && video.type.toLowerCase().contains("low")) {
                                retVidUrl = video.url;
                                break;
                            }
                        }
                    }

                    return retVidUrl;
                }
            } catch (Exception e) {
//                Log.e("Couldn't get YouTube streaming URL", e);
                Log.d(TAG, "doInBackground() called with: params = [" + params + "]");
            }
//            CLog.w("Couldn't get stream URI for " + url);
            Log.d(TAG, "doInBackground() called with: params = [" + params + "]");
            return null;
        }

        @Override
        protected void onPostExecute(String streamingUrl) {
            super.onPostExecute(streamingUrl);
            progressDialog.dismiss();
            if (streamingUrl != null) {
                         *//* Do what ever you want with streamUrl *//*

                Log.d(TAG, "onPostExecute() called with: streamingUrl = [" + streamingUrl + "]");
                DownloadTask downloadTask = new DownloadTask(context);
//                downloadTask.execute(newsArrayList.get(getAdapterPosition()).getLink(), "" + newsArrayList.get(getAdapterPosition()).getTitle() + "." + FileType, newsArrayList.get(getAdapterPosition()).getType());
            }
        }
    }*/



}

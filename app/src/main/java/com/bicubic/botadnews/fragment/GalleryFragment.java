package com.bicubic.botadnews.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bicubic.botadnews.R;
import com.bicubic.botadnews.adapter.RvGalleryImageAdapter;
import com.bicubic.botadnews.adapter.RvGalleryVideoAdapter;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.facebook.login.widget.ProfilePictureView.TAG;


public class GalleryFragment extends Fragment implements View.OnClickListener {

    View rootView;
    static RecyclerView rv_video;
    RecyclerView rv_image;
    static List<File> videofiles;
    static List<File> imagefiles;
    Button bt_video, bt_image;
    private static Context context;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_gallery, container, false);

        rv_video = (RecyclerView) rootView.findViewById(R.id.rv_video);
        rv_image = (RecyclerView) rootView.findViewById(R.id.rv_image);
        bt_video = (Button) rootView.findViewById(R.id.bt_video);
        bt_image = (Button) rootView.findViewById(R.id.bt_image);
        bt_video.setBackgroundResource(R.color.colorAccent);

        bt_video.setOnClickListener(this);
        bt_image.setOnClickListener(this);

        context = getActivity();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_video.setLayoutManager(linearLayoutManager);
        rv_video.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
        rv_image.setLayoutManager(linearLayoutManager1);
        rv_image.setHasFixedSize(true);

        videofiles = new ArrayList<>();
        imagefiles = new ArrayList<>();
        new LoadOfflineData().execute();

        /*rootView.post(new Runnable() {
            @Override
            public void run() {
                new LoadOfflineData().execute();
            }
        });*/




        // Inflate the layout for this fragment
        return rootView;
    }

    public static class LoadOfflineData extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar = new ProgressDialog(context);
            progressBar.setMessage("Loading...");
            progressBar.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String video_path = Environment.getExternalStorageDirectory().getPath() + "/Botad News/Videos";
            Log.d("Files", "Path: " + video_path);
            File checkFile = new File(video_path);

//            videofiles = new ArrayList<>();
            File[] files = new File[0];
            if (checkFile.isDirectory()) {
                File directory = new File(video_path);
                files = directory.listFiles();

                videofiles.addAll(Arrays.asList(files));
                Collections.reverse(videofiles);

            } else {
//                Toast.makeText(getActivity(), "Folder not Found", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "doInBackground: folder not found");
            }


            String image_path = Environment.getExternalStorageDirectory().getPath() + "/Botad News/Images";
            Log.d("Files", "Path: " + image_path);
            File checkFileImages = new File(image_path);

//            imagefiles = new ArrayList<>();
            File[] filesImage = new File[0];
            if (checkFileImages.isDirectory()) {
                File directory = new File(image_path);
                filesImage = directory.listFiles();

                imagefiles.addAll(Arrays.asList(filesImage));

            } else {
//                Toast.makeText(getActivity(), "Folder not Found", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "doInBackground: folder not found");
            }

            Log.d("Files", "Size: " + files.length);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.dismiss();

            if (videofiles.size() > 0) {
                RvGalleryVideoAdapter adapter = new RvGalleryVideoAdapter(videofiles, context);
                rv_video.setAdapter(adapter);
            } else {
                Toast.makeText(context, R.string.data_not_found, Toast.LENGTH_SHORT).show();
            }

        }

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.bt_video:

                bt_video.setBackgroundResource(R.color.colorAccent);
                bt_image.setBackgroundResource(R.color.colorPrimary);
                rv_image.setVisibility(View.GONE);
                rv_video.setVisibility(View.VISIBLE);

                if (videofiles.size() > 0) {
                    RvGalleryVideoAdapter adapter = new RvGalleryVideoAdapter(videofiles, getActivity());
                    rv_video.setAdapter(adapter);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.data_not_found), Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.bt_image:

                bt_image.setBackgroundResource(R.color.colorAccent);
                bt_video.setBackgroundResource(R.color.colorPrimary);
                rv_image.setVisibility(View.VISIBLE);
                rv_video.setVisibility(View.GONE);

                if (imagefiles.size() > 0) {

                    Collections.reverse(imagefiles);
                    RvGalleryImageAdapter adapter_image = new RvGalleryImageAdapter(imagefiles, getActivity());
                    rv_image.setAdapter(adapter_image);

                } else {
                    Toast.makeText(getActivity(), getString(R.string.data_not_found), Toast.LENGTH_SHORT).show();

                }


                break;
            default:


                break;
        }

    }



    public void threadList(){

        Thread background = new Thread(new Runnable() {


            // After call for background.start this run method call
            public void run() {
                try {

                    String video_path = Environment.getExternalStorageDirectory().getPath() + "/Botad News/Videos";
                    Log.d("Files", "Path: " + video_path);
                    File checkFile = new File(video_path);

//            videofiles = new ArrayList<>();
                    File[] files = new File[0];
                    if (checkFile.isDirectory()) {
                        File directory = new File(video_path);
                        files = directory.listFiles();

                        videofiles.addAll(Arrays.asList(files));

                    } else {
//                Toast.makeText(getActivity(), "Folder not Found", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "doInBackground: folder not found");
                    }

                    String image_path = Environment.getExternalStorageDirectory().getPath() + "/Botad News/Images";
                    Log.d("Files", "Path: " + image_path);
                    File checkFileImages = new File(image_path);

//            imagefiles = new ArrayList<>();
                    File[] filesImage = new File[0];
                    if (checkFileImages.isDirectory()) {
                        File directory = new File(image_path);
                        filesImage = directory.listFiles();

                        imagefiles.addAll(Arrays.asList(filesImage));

                    } else {
//                Toast.makeText(getActivity(), "Folder not Found", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "doInBackground: folder not found");
                    }

                    Log.d("Files", "Size: " + files.length);

                    threadMsg(videofiles, imagefiles);

                } catch (Throwable t) {
                    // just end the background thread
                    Log.i("Animation", "Thread  exception " + t);
                }
            }

            private void threadMsg(List<File> videofiles, List<File> imagefiles) {
                Message msgObj = handler.obtainMessage();
                Bundle b = new Bundle();
                b.putSerializable("videofiles", (Serializable) videofiles);
                b.putSerializable("imagefiles", (Serializable) imagefiles);
                msgObj.setData(b);
                handler.sendMessage(msgObj);
            }

            // Define the Handler that receives messages from the
            // thread and update the
            // progress
            private final Handler handler = new Handler() {
                public void handleMessage(Message msg) {

                    List<File> videofiles = (ArrayList<File>) msg.getData().getSerializable("videofiles");
                    List<File> imagefiles = (ArrayList<File>) msg.getData().getSerializable("imagefiles");

                    if (videofiles.size() > 0) {
                        RvGalleryVideoAdapter adapter = new RvGalleryVideoAdapter(videofiles, context);
                        rv_video.setAdapter(adapter);
                    } else {
                        Toast.makeText(context, getString(R.string.data_not_found), Toast.LENGTH_SHORT).show();
                    }

                }
            };

        });
        // Start Thread
        background.start();  //After call start method thread called run Method

    }





}

package com.bicubic.botadnews.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bicubic.botadnews.R;
import com.bicubic.botadnews.adapter.RvHomeAdapter;
import com.bicubic.botadnews.model.News;
import com.bicubic.botadnews.utils.ConnectivityReceiver;
import com.bicubic.botadnews.utils.DatePickerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment{

    View rootView;
    ArrayList<News> newsArrayList;
    RecyclerView rv_news;
    EditText et_date;
    TextView list_empty;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        rv_news = (RecyclerView) rootView.findViewById(R.id.rv_news);
        et_date = (EditText) rootView.findViewById(R.id.et_date);
        list_empty = (TextView) rootView.findViewById(R.id.list_empty);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_news.setLayoutManager(linearLayoutManager);
        rv_news.setHasFixedSize(true);
/*
        try {
         Bitmap bitmap = retriveVideoFrameFromVideo("http://192.168.1.201/botad/uploads/news/1477491887_1084813438.mp4");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }*/



        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate1 = df1.format(c.getTime());
        et_date.setText(formattedDate1);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());

        if (ConnectivityReceiver.isConnected()) {
            GetNewsData getNewsData = new GetNewsData();
            getNewsData.execute(formattedDate);
        } else {
            Toast.makeText(getActivity(), R.string.please_connect_to_internet, Toast.LENGTH_SHORT).show();
        }
//        getNewsData.execute("2016-10-21");

        // Inflate the layout for this fragment
        return rootView;
    }


    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDatePicker();
            }
        });
    }


    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");
    }

    android.app.DatePickerDialog.OnDateSetListener ondate = new android.app.DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            if (ConnectivityReceiver.isConnected()) {
                GetNewsData getNewsData = new GetNewsData();
                getNewsData.execute(String.valueOf(year) + "-" + String.valueOf(monthOfYear+1)
                        + "-" + String.valueOf(dayOfMonth));
            } else {
                Toast.makeText(getActivity(),R.string.please_connect_to_internet, Toast.LENGTH_SHORT).show();
            }
            et_date.setText(String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear+1)
                    + "-" + String.valueOf(year));
        }
    };



    public static Bitmap retriveVideoFrameFromVideo(String videoPath)
            throws Throwable
    {

        Log.d(TAG, "retriveVideoFrameFromVideo: video "+videoPath);
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)"
                            + e.getMessage());

        }
        finally
        {
            if (mediaMetadataRetriever != null)
            {
                mediaMetadataRetriever.release();
            }
        }

        Log.e("", "retriveVideoFrameFromVideo: bitmap = "+bitmap );
        return bitmap;
    }


    public class GetNewsData extends AsyncTask<String, Void, Void> {

        String responseString;
        ProgressDialog progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(getActivity());
            progressBar.setMessage("Loading...");
            progressBar.show();
            list_empty.setVisibility(View.GONE);



        }

        @Override
        protected Void doInBackground(String... str) {

            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("news_date",str[0])
                    .build();

            Request request = new Request.Builder()
                    .url("http://www.bicubicstudios.com/botadnews/Botad/index.php/api/news_list")
                    .post(requestBody)
                    .build();

            try {

                Response response = okHttpClient.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code" + response);
                {

                    responseString = response.body().string();
                    System.out.println(responseString);
                    response.body().close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.dismiss();

            if (responseString!=null){

                newsArrayList = new ArrayList<News>();
                try {
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("message").contains("not success")){

                        RvHomeAdapter adapter = new RvHomeAdapter(newsArrayList, getActivity());
                        rv_news.setAdapter(adapter);
                        if (newsArrayList.isEmpty()) {
                            list_empty.setVisibility(View.VISIBLE);
                        }


                    }else{

                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            newsArrayList.add(new News(jsonObject1.getString("title"),jsonObject1.getString("url"),jsonObject1.getString("type"),jsonObject1.getString("thumb_image")));

                        }

                        if (newsArrayList.size()>0){
                            RvHomeAdapter adapter = new RvHomeAdapter(newsArrayList, getActivity());
                            rv_news.setAdapter(adapter);
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
}

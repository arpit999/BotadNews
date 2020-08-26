package com.bicubic.botadnews.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bicubic.botadnews.R;
import com.bicubic.botadnews.utils.ConnectivityReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SettingFragment extends Fragment {

    View rootView;
    SwitchCompat switchCompat;
    boolean noti_status = true;
    ProgressBar progressBar;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        switchCompat = (SwitchCompat) rootView.findViewById(R.id.compatSwitch);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        SharedPreferences settings = getActivity().getSharedPreferences("preference", 0);
        noti_status = settings.getBoolean("noti_status", true);

        if (noti_status) {
            switchCompat.setChecked(true);
        } else {
            switchCompat.setChecked(false);
        }


        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                SharedPreferences settings = getActivity().getSharedPreferences("preference", 0);
                String userid = settings.getString("userid", "");
                noti_status = settings.getBoolean("noti_status", true);

                if (noti_status) {
                    switchCompat.setChecked(true);
                } else {
                    switchCompat.setChecked(false);
                }

                if (ConnectivityReceiver.isConnected()) {
                    if (b) {
                        String notification = "1";
                        switchCompat.setChecked(true);
                        new NotifyUpdate().execute(notification, userid);
                    } else {
                        String notification = "0";
                        switchCompat.setChecked(false);
                        new NotifyUpdate().execute(notification, userid);
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.please_connect_to_internet), Toast.LENGTH_SHORT).show();
                }


            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }


    public class NotifyUpdate extends AsyncTask<String, Void, Void> {

//        ProgressDialog progressDialog;
        String responseString;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /*progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please Wait....");
            progressDialog.setCancelable(true);
            progressDialog.show();*/

            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... str) {

            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("noti_status", str[0])
                    .add("id", str[1])
                    .build();

            Request request = new Request.Builder()
                    .url("http://www.bicubicstudios.com/botadnews/Botad/index.php/api/edit_notifications")
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
//            progressDialog.dismiss();
            progressBar.setVisibility(View.GONE);
            if (responseString != null) {
                try {

                    JSONObject jsonObject = new JSONObject(responseString);
                    String message = jsonObject.getString("message");
                    String status = String.valueOf(jsonObject.getInt("status"));
                    if (status.equals("1")) {

                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");

                        String noti_status = jsonObject1.getString("noti_status");
                        SharedPreferences settings = getActivity().getSharedPreferences("preference", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = settings.edit();
                        if (noti_status.equals("0")) {
                            editor.putBoolean("noti_status", false);
                            switchCompat.setChecked(false);
                        } else {
                            editor.putBoolean("noti_status", true);
                            switchCompat.setChecked(true);
                        }
                        editor.apply();

                    } else {

                        SharedPreferences settings = getActivity().getSharedPreferences("preference", 0);
                        noti_status = settings.getBoolean("noti_status", true);
                        switchCompat.setChecked(noti_status);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(),getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
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

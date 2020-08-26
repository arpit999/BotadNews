package com.bicubic.botadnews.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bicubic.botadnews.R;
import com.bicubic.botadnews.utils.ConnectivityReceiver;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    // fb login
    String Name = "", City = "", facebookid = "", device_token = "";
    EditText et_name, et_city;
    Button bt_facebook;

    // fb login
    CallbackManager callbackManager;
    LoginButton fb_login;
    String Email, ProfilePic = "", DOB, F_ID, LastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences("preference", 0);
//Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

        if (hasLoggedIn) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        et_city = (EditText) findViewById(R.id.et_city);
        et_name = (EditText) findViewById(R.id.et_name);
        bt_facebook = (Button) findViewById(R.id.bt_facebook);
        fb_login = (LoginButton) findViewById(R.id.fb_login);

        callbackManager = CallbackManager.Factory.create();
        fb_login.setReadPermissions(Arrays.asList("public_profile"));


        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        device_token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "");

//        FaceBookLogin();

        bt_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
                device_token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "");

                if (ConnectivityReceiver.isConnected()) {
                    FaceBookLogin();
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
                } else {
                    Toast.makeText(LoginActivity.this, "Please connect to internet", Toast.LENGTH_SHORT).show();
                }*/

            }
        });


    }


    private void FaceBookLogin() {

        fb_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            public static final String TAG = "facebooklogin";

            @Override
            public void onSuccess(final LoginResult loginResult) {


                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                Log.v("Profile ---------   ", response.toString());

                                try {

                                    if (object != null) {

                                        facebookid = object.getString("id");
                                        if (object.has("name"))
                                            Name = object.getString("name");
                                        Log.d(TAG, "onCompleted: Name - " + object.getString("name"));

                                        Log.d(TAG, "onCompleted() called with: object = [" + object + "], response = [" + response + "]");

                                        ProfilePic = "https://graph.facebook.com/" + F_ID + "/picture?type=large";

                                        LoginTask loginTask = new LoginTask();
                                        loginTask.execute(Name, City, facebookid, device_token);

                                    } else
                                        Log.d(TAG, "onCompleted: object is null " + object);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();

                System.out.println("Facebook Login Successful!");
                System.out.println("Logged in user Details : ");
                System.out.println("--------------------------");
                System.out.println("User ID  : " + loginResult.getAccessToken().getUserId());
                System.out.println("Authentication Token : " + loginResult.getAccessToken().getToken());

            }

            @Override
            public void onCancel() {
                System.out.println("Facebook Login Cancel!!");
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                //                System.out.println("Facebook Login failed!! because of " + e.getCause().toString());
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
//        MyApplication.getInstance().setConnectivityListener(this);
    }

    //    continue button click
    public void clickCountinue(View view) {

        startActivity(new Intent(LoginActivity.this, MainActivity.class));

        if (TextUtils.isEmpty(et_name.getText())) {
            et_name.setError("Name is required?");
        } else if (!ConnectivityReceiver.isConnected()) {
            Toast.makeText(this, R.string.please_connect_to_internet, Toast.LENGTH_SHORT).show();
        } else {

            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
            device_token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "");

            Name = et_name.getText().toString();
            City = et_city.getText().toString();

            LoginTask loginTask = new LoginTask();
            loginTask.execute(Name, City, facebookid, device_token);

        }


    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.bt_facebook), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    public class LoginTask extends AsyncTask<String, Void, Void> {

        String responseString;
        ProgressDialog progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(LoginActivity.this);
            progressBar.setMessage("Please Wait...");
            progressBar.show();
        }

        @Override
        protected Void doInBackground(String... str) {

            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("name", str[0])
                    .add("city", str[1])
                    .add("facebook_id", str[2])
                    .add("device_token", str[3])
                    .build();

            Request request = new Request.Builder()
                    .url("http://www.bicubicstudios.com/botadnews/Botad/index.php/api/signup")
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

            if (responseString != null) {

                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    String message = jsonObject.getString("message");
                    String status = String.valueOf(jsonObject.getInt("status"));

                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    if (message.equals("success")) {

                        SharedPreferences settings = getSharedPreferences("preference", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("name", jsonObject1.optString("name"));
                        editor.putString("userid", jsonObject1.optString("id"));
                        editor.putBoolean("hasLoggedIn", true);
                        editor.putBoolean("noti_status", true);
                        editor.apply();

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        //User has successfully logged in, save this information
// We need an Editor object to make preference changes.

                    } else {

                        Toast.makeText(LoginActivity.this, R.string.somwthing_went_wrong_try_again, Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }


}


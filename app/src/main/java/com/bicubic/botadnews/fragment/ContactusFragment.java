package com.bicubic.botadnews.fragment;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.bicubic.botadnews.R;


public class ContactusFragment extends Fragment {

    View rootView;
    WebView contact_webview;

    public ContactusFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contactus, container, false);
//        contact_webview = (WebView)rootView.findViewById(R.id.contact_webview);





     /*   contact_webview.loadUrl("http://www.bicubicstudios.com/botadnews/Botad/index.php/api/contact_us");

        // Enable Javascript
        WebSettings webSettings = contact_webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        contact_webview.setWebViewClient(new WebViewClient());*/

        // Inflate the layout for this fragment
        return rootView;
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

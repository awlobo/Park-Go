package com.park_and_go.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.park_and_go.R;
import com.park_and_go.assets.Constants;

import static com.park_and_go.assets.Constants.URL_WEBVIEW;

public class WebTransporteCompartido extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_transporte_compartido2);

        Intent intent = getIntent();

        String url = intent.getStringExtra(URL_WEBVIEW);

        mWebView = (WebView) findViewById(R.id.webviewtransporte);

        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);

        mWebView.loadUrl(url);
    }
}

package com.mohammadkz.porsno_android.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mohammadkz.porsno_android.R;

import es.dmoral.toasty.Toasty;

public class WebViewActivity extends AppCompatActivity {

    WebView webView;
    String url;
    String backUrl = "http://185.190.39.159/Porseshno_backend/";
    boolean loadingFinished = true;
    boolean redirect = false;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("در حال دریافت اطلاعات...");
        progressDialog.setCancelable(false);

        progressDialog.show();
        Bundle extras = getIntent().getExtras();
        url = extras.getString("url");
        initViews();
        controllerViews();
    }

    private void initViews() {
        webView = findViewById(R.id.webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Toasty.error(WebViewActivity.this, "error", Toasty.LENGTH_SHORT).show();

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains(backUrl)) {
                    WebViewActivity.super.finish();
                }
                progressDialog.dismiss();
            }

        });
    }

    private void controllerViews() {


    }
}
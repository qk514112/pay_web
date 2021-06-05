/**
 * File Name:PersonInfoActivity.java
 * Package Name:com.zzkko.bussiness.person.ui
 * author:yangxiongjie
 * Date:2014-1-24下午2:26:25
 * Copyright (c) 2014, zzkko All Rights Reserved.
 */
package com.qk514112.pay_web;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//import com.streamkar.util.SPUtil;

/**
 * 修改时间：927
 * paypal支付的Web界面
 */
public class WebPayViewActivity extends AppCompatActivity {
    public final static String URL = "url";
    public final static String Title = "title";
    public final static String PostValue = "postValue";
    WebView webView;
    ProgressBar progressBar;
    TextView mToolbar;
    private String url;
    private String title;
    private String postValue;

    private List<String> HTTP_SCHEMES = Arrays.asList("http", "https");

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ty_setting_webview);

        webView = findViewById(R.id.webview_wv);
        progressBar = findViewById(R.id.webview_pb);
        mToolbar = findViewById(R.id.payment_toolbar);

        url = getIntent().getStringExtra(URL);
        title = getIntent().getStringExtra(Title);
        postValue = getIntent().getStringExtra(PostValue);
        //Logger.e("~~~!!url:" + url + "       /title:" + title + "/postValue" + postValue);
//        Log.d("~~~!!title",title);
        if (title == null) {
            title = getString(R.string.payment);
        }
        mToolbar.setText(title);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//change direction of back arrow
        Drawable backArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     final JsResult result) {
                AlertDialog.Builder b2 = new AlertDialog.Builder(
                        WebPayViewActivity.this).setMessage(message)
                        .setPositiveButton(getString(R.string.common_ok),
                                new AlertDialog.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        result.confirm();
                                    }
                                });

                b2.setCancelable(false);
                b2.create();
                b2.show();
                return true;
            }

        });
        webView.addJavascriptInterface(new JsObject(), "payment");
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) { // 添加GA
                // 判断是否成功
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (shouldOverrideUrlLoadingInner(view, url)) {
                    //Logger.d("shouldOverrideUrlLoadingInner ...");
                    return true;
                } else if (url.startsWith("whatsapp:")) {
                    // 跳转 whatsapp
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                view.loadUrl(url);
                return true;

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //Logger.d("loading url=" + url);
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }
        });
        if (url != null && url.contains("orders/easypaisa")) {
            postURL(url, postValue);
        } else {
            webView.loadUrl(url);
        }
    }

    ///Ep 支付 post方式 加headed
    protected void postURL(final String url, String postData) {
        //Logger.d("EP postURL url:" + url + " ,postData:" + postData);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Cache-Control", "max-age=0")
                .addHeader("Origin", "null") //Optional
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("User-Agent", webView.getSettings().getUserAgentString())
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .addHeader("Accept-Language", Locale.getDefault().getLanguage())
                .post(RequestBody.create(MediaType.parse("application/json"), postData))
//                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), postData))
                .build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String htmlString = response.body().string();

                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.clearCache(true);
                        webView.loadDataWithBaseURL(url, htmlString, "text/html", "utf-8", null);
                    }
                });
            }
        });
    }

    //Back button on Actionbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    /**
     * Parse the url and open it by system function.
     * case 1: deal "intent://xxxx" url.
     * case 2: deal custom scheme. url
     *
     * @param view: WebView
     * @param url
     * @return
     */
    private boolean shouldOverrideUrlLoadingInner(WebView view, String url) {
        if (!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            if (uri != null) {
                if ("intent".equals(uri.getScheme())) {
                    try {
                        Intent intent = Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME);
                        if (intent != null) {
                            PackageManager pm = getApplication().getPackageManager();
                            ResolveInfo info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                            if (info != null) {
                                //Logger.d("shouldOverrideUrlLoadingInner intent startActivity");
                                getApplication().startActivity(Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME));
                                return true;
                            } else {
                                String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                                if (!TextUtils.isEmpty(fallbackUrl)) {
                                    if (fallbackUrl.startsWith("market://"))
                                        SPPayUtil.startAppMarketWithUrl(getApplication(), fallbackUrl, false);
                                    else
                                        view.loadUrl(fallbackUrl);
                                    return true;
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
                if (!HTTP_SCHEMES.contains(uri.getScheme())) {
                    SPPayUtil.startUrl(getApplication(), url, true);
                    return true;
                }
            }
        }

        return false;
    }

    public class JsObject {
        @JavascriptInterface
        public void paySuccess() {
            setResult(RESULT_OK);
            finish();
        }

        @JavascriptInterface
        public String getJsessionId() {
//            //Logger.d("getJsessionId=" + SPUtil.getJsessionId(getApplicationContext()));
//            showNormalDialog(null);
            return "getJsessionId_error";
        }

        // 支付失败跳至失败界面
        @JavascriptInterface
        public void payFail(String errorMsg) {
            finish();
        }

        @JavascriptInterface
        public void payComplete() {
            //Logger.d("-------------------payComplete");
            finish();
        }

      /*  @JavascriptInterface
        public void payDialog(String msg) {
            //Logger.d("-------------------payDialog:"+msg);
            showNormalDialog(msg);
        }*/
    }
}

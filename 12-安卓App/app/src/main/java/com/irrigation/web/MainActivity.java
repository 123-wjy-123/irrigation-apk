package com.irrigation.web;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

/**
 * 精准灌溉 App：一个全屏 WebView，加载树莓派上的网页。
 *
 * 地址默认 http://10.42.0.1:8080（Pi 自己的热点）。
 * 长按屏幕可以改地址（例如 http://192.168.1.127:8080），改完会记住。
 */
public class MainActivity extends android.app.Activity {

    private static final String PREFS = "irrigation";
    private static final String KEY_URL = "url";

    private WebView web;
    private ProgressBar bar;
    private SharedPreferences prefs;
    private String currentUrl;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        currentUrl = prefs.getString(KEY_URL, BuildConfig.DEFAULT_URL);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#fbfaf7"));

        bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        bar.setMax(100);
        bar.setVisibility(View.GONE);
        root.addView(bar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 6));

        web = new WebView(this);
        web.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.addView(web, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        setContentView(root);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, android.webkit.WebResourceRequest req) {
                return false;   // 站内链接都在 WebView 里打开
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                bar.setVisibility(View.GONE);
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                bar.setVisibility(newProgress >= 100 ? View.GONE : View.VISIBLE);
                bar.setProgress(newProgress);
            }
        });

        // 长按标题区域改地址（网页里长按可能被页面用掉，所以绑在顶部细条上）
        bar.setOnLongClickListener(v -> {
            askUrl();
            return true;
        });
        bar.setLongClickable(true);

        load(currentUrl);
    }

    private void load(String url) {
        if (!url.startsWith("http")) url = "http://" + url;
        currentUrl = url;
        prefs.edit().putString(KEY_URL, url).apply();
        web.loadUrl(url);
    }

    private void askUrl() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        input.setText(currentUrl);
        input.setSelection(input.getText().length());

        new AlertDialog.Builder(this)
                .setTitle("服务器地址")
                .setMessage("手机要能访问到树莓派，例如 http://10.42.0.1:8080 或 http://192.168.1.127:8080")
                .setView(input)
                .setPositiveButton("加载", (d, w) -> load(input.getText().toString().trim()))
                .setNegativeButton("取消", null)
                .setNeutralButton("恢复默认", (d, w) -> load(BuildConfig.DEFAULT_URL))
                .show();
    }

    @Override
    public void onBackPressed() {
        if (web != null && web.canGoBack()) {
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (web != null) {
            web.destroy();
            web = null;
        }
        super.onDestroy();
    }
}

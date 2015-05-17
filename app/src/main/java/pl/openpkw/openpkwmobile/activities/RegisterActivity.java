package pl.openpkw.openpkwmobile.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import pl.openpkw.openpkwmobile.R;

/**
 * Created by Wojciech Radzioch on 12.05.15.
 */
public class RegisterActivity extends OpenPKWActivity {
    private boolean doubleBackToExitPressedOnce = false;
    private WebView registerWebView;
    public static final String ua = "Mozilla/5.0 (Android; Tablet; rv:20.0) Gecko/20.0 Firefox/20.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerWebView = (WebView) findViewById(R.id.register_webview);
        WebSettings settings = registerWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        settings.setUserAgentString(ua);
        registerWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }
        });
        registerWebView.loadUrl(RegisterActivity.this.getString(R.string.register_url));
    }
}

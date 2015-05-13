package pl.openpkw.openpkwmobile.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import pl.openpkw.openpkwmobile.R;

/**
 * Created by Wojciech Radzioch on 12.05.15.
 */
public class RegisterFragment extends Fragment {

    private WebView registerWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        registerWebView = (WebView) v.findViewById(R.id.register_webview);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebSettings settings = registerWebView.getSettings();
        settings.setJavaScriptEnabled(true);
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
        registerWebView.loadUrl(getActivity().getString(R.string.register_url));
        setRetainInstance(true);
    }



}

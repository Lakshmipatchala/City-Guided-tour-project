package de.hft.activity;

import de.hft.R;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Web extends Activity{
	
	WebView mWebView;
	//public static final String URL = "";
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.web);
	    String turl = getIntent().getStringExtra("URL");
	    mWebView = (WebView) findViewById(R.id.webview);
	    mWebView.getSettings().setJavaScriptEnabled(true);
	    mWebView.loadUrl(turl);
	    mWebView.getSettings().setBuiltInZoomControls(true);
	    mWebView.setWebViewClient(new HelloWebViewClient());
	}
	
	
	
	
	private class HelloWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
	    }
	}
	
}


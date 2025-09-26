package com.CityGuide;


import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;


public class CityGuideActivity extends Activity {
    /** Called when the activity is first created. */
		

		  private WebView mWebView;
		  
		  public void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        setContentView(R.layout.main);
		        mWebView = (WebView) findViewById(R.id.webview);  
		              mWebView.getSettings().setJavaScriptEnabled(true);    
		        mWebView.loadUrl("http://www.google.com");

		        mWebView.setWebViewClient(new HelloWebViewClient());
			}
		    private class HelloWebViewClient extends WebViewClient 
		    {    
		    	@Override    
		    	public boolean shouldOverrideUrlLoading(WebView view, String url) {       
		    	view.loadUrl(url);        
		    	return true;   
		    	}
		    	}
		    @Override
		   public boolean onKeyDown(int keyCode, KeyEvent event) {    
		    	if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack())
		    	{   
		    	mWebView.goBack();       
		    	return true;   
		    	}    return super.onKeyDown(keyCode, event);}
		}

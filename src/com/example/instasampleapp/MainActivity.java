package com.example.instasampleapp;


import java.util.ArrayList;
import com.example.utilities.ApplicationData;
import com.example.utilities.RequestManager;
import com.example.utilities.interfaces.OnResponseHandler;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnResponseHandler{
	static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT);
	static final int MARGIN = 4;
	static final int PADDING = 2;
	private ProgressDialog mSpinner;
	private WebView mWebView;
	private LinearLayout mContent;
	private TextView mTitle;

	private static final String TAG = "Instagram-WebView";
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSpinner = new ProgressDialog(this);
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage("Loading...");
		mContent = new LinearLayout(this);
		mContent.setOrientation(LinearLayout.VERTICAL);
		setUpTitle();
		setUpWebView();
		setContentView(mContent, new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();

    }

    /**
     * Setup title
     */
    private void setUpTitle() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mTitle = new TextView(this);
		mTitle.setText("Instagram");
		mTitle.setTextColor(Color.WHITE);
		mTitle.setTypeface(Typeface.DEFAULT_BOLD);
		mTitle.setBackgroundColor(Color.BLACK);
		mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
		mContent.addView(mTitle);
	}

    /**
     * Setup web view
     */
	private void setUpWebView() {
		mWebView = new WebView(this);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setWebViewClient(new OAuthWebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(ApplicationData.getAuthUrl());
		mWebView.setLayoutParams(FILL);
		mContent.addView(mWebView);
	}
	
	/**
	 * Get context
	 * @return
	 */
	public Context getContext() {
	    return (Context)this;
	}

	private class OAuthWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(TAG, "Redirecting URL " + url);

			if (url.startsWith(ApplicationData.CALLBACK_URL)) {
				String urls[] = url.split("=");
				RequestManager.getInstance().getAccessToken(getContext(),urls[1], MainActivity.this);
				
				return true;
			}
			return false;
		}
		
		

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			Log.d(TAG, "Page error: " + description);

			super.onReceivedError(view, errorCode, description, failingUrl);
			Toast.makeText(MainActivity.this, description, Toast.LENGTH_SHORT).show();
			
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d(TAG, "Loading URL: " + url);

			super.onPageStarted(view, url, favicon);
			mSpinner.show();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			String title = mWebView.getTitle();
			if (title != null && title.length() > 0) {
				mTitle.setText(title);
			}
			Log.d(TAG, "onPageFinished URL: " + url);
			mSpinner.dismiss();
		}

	}

	public interface OAuthDialogListener {
		public abstract void onComplete(String accessToken);
		public abstract void onError(String error);
	}

	@Override
	public <T> void onResponse(ArrayList<String> urls) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onSuccess() {
		// TODO Auto-generated method stub
		this.finish();
		Intent i = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(i);
	}


	@Override
	public void onError(String error) {
		// TODO Auto-generated method stub
		Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
		
	}
	
	

    
}

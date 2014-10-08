package com.example.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.example.utilities.interfaces.OnResponseHandler;


public class InstagramApp {
	
	private InstagramSession mSession;
	private ProgressDialog mProgress;
	private String mAccessToken;
	private static int WHAT_FINALIZE = 0;
	private static int WHAT_ERROR = 1;
	private static int WHAT_FETCH_INFO = 2;
	private static int WHAT_FETCH_IMAGE = 3;
	private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
	private static final String API_URL = "https://api.instagram.com/v1";
	private static final String TAG = "InstagramAPI";
	
	ArrayList<String> urls =new ArrayList<String>();
	OnResponseHandler responseHandler;
	

	public InstagramApp(Context context) {
		mSession = new InstagramSession(context);
		mAccessToken = mSession.getAccessToken();
		mProgress = new ProgressDialog(context);
		mProgress.setCancelable(false);
	}
	
	/**
	 * Get Access token
	 * @param code
	 * @param responseHandler
	 */
	public void getAccessToken(final String code,OnResponseHandler responseHandler) {
		this.responseHandler = responseHandler;
		mProgress.setMessage("Getting access token ...");
		mProgress.show();

		new Thread() {
			@Override
			public void run() {
				Log.i(TAG, "Getting access token");
				int what = WHAT_FETCH_INFO;
				try {
					URL url = new URL(TOKEN_URL);
					
					Log.i(TAG, "Opening Token URL " + url.toString());
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("POST");
					urlConnection.setDoInput(true);
					urlConnection.setDoOutput(true);
					OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
					writer.write("client_id="+ApplicationData.CLIENT_ID+
								"&client_secret="+ApplicationData.CLIENT_SECRET+
								"&grant_type=authorization_code" +
								"&redirect_uri="+ApplicationData.CALLBACK_URL+
								"&code=" + code);
				    writer.flush();
					String response = streamToString(urlConnection.getInputStream());
					Log.i(TAG, "response " + response);
					JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
					
					mAccessToken = jsonObj.getString("access_token");
					Log.i(TAG, "Got access token: " + mAccessToken);
					
					String id = jsonObj.getJSONObject("user").getString("id");
					String user = jsonObj.getJSONObject("user").getString("username");
					String name = jsonObj.getJSONObject("user").getString("full_name");					
					
					mSession.storeAccessToken(mAccessToken, id, user, name);
					
				} catch (Exception ex) {
					what = WHAT_ERROR;
					ex.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
			}
		}.start();
	}
	
	/**
	 * Fetch user name
	 */
	private void fetchUserName() {
		mProgress.setMessage("Finalizing ...");
		
		new Thread() {
			@Override
			public void run() {
				Log.i(TAG, "Fetching user info");
				int what = WHAT_FINALIZE;
				try {
					URL url = new URL(API_URL + "/users/" + mSession.getId() + "/?access_token=" + mAccessToken);

					Log.d(TAG, "Opening URL " + url.toString());
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.setDoInput(true);
					urlConnection.connect();
					String response = streamToString(urlConnection.getInputStream());
					System.out.println(response);
					JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
					String name = jsonObj.getJSONObject("data").getString("full_name");
					String bio = jsonObj.getJSONObject("data").getString("bio");
					Log.i(TAG, "Got name: " + name + ", bio [" + bio + "]");
				} catch (Exception ex) {
					what = WHAT_ERROR;
					ex.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
			}
		}.start();	
		
	}


	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == WHAT_ERROR) {
				mProgress.dismiss();
				if(msg.arg1 == 1) {
					if(responseHandler!= null)
						responseHandler.onError("Failed to get access token");
				}
				else if(msg.arg1 == 2) {
					if(responseHandler != null)
						responseHandler.onError("Failed to get user information");
				}
			} 
			else if(msg.what == WHAT_FETCH_INFO) {
				fetchUserName();
			}
			else if(msg.what == WHAT_FETCH_IMAGE)
			{
				mProgress.dismiss();
				responseHandler.onResponse(urls);
			}
			else {
				mProgress.dismiss();
				responseHandler.onSuccess();
			}
		}
	};

	public boolean hasAccessToken() {
		return (mAccessToken == null) ? false : true;
	}

	public String getUserName() {
		return mSession.getUsername();
	}

	public String getId() {
		return mSession.getId();
	}
	
	public String getName() {
		return mSession.getName();
	}
	
	/**
	 * Convert inputstream to String
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private String streamToString(InputStream is) throws IOException {
		String str = "";

		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));

				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}

				reader.close();
			} finally {
				is.close();
			}

			str = sb.toString();
		}

		return str;
	}

	/**
	 * Reset access token
	 */
	public void resetAccessToken() {
		if (mAccessToken != null) {
			mSession.resetAccessToken();
			mAccessToken = null;
		}
	}
	
	/**
	 * Fetch Images
	 * @param responseHandler
	 */
	public void fetchImages(final OnResponseHandler responseHandler)
	{
		mProgress.setMessage("Getting access token ...");
		mProgress.show();
		this.responseHandler = responseHandler;
		new Thread() {
			@Override
			public void run() {
		URL example;
		try {
			example = new URL("https://api.instagram.com/v1/users/self/media/recent?access_token="
			        + mAccessToken);
			Log.i(TAG, "Getting fetch url="+example);

		URLConnection tc = example.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
		        tc.getInputStream()));

		String line;
		while ((line = in.readLine()) != null) {
			JSONObject ob = new JSONObject(line);

			JSONArray object = ob.getJSONArray("data");
			Log.i(TAG, "Getting fetch url object length="+object.length());
			for (int i = 0; i < object.length(); i++) {

				Log.i(TAG, "Getting fetch url="+example);
				JSONObject jo = (JSONObject) object.get(i);
				JSONObject nja = (JSONObject) jo.getJSONObject("images");

				JSONObject purl3 = (JSONObject) nja
						.getJSONObject("thumbnail");

				urls.add(purl3.getString("url"));
				Log.i(TAG, "" + purl3.getString("url"));
			}

			mHandler.sendMessage(mHandler.obtainMessage(WHAT_FETCH_IMAGE, 3, 0));
		}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			}}.start();

}

	
}
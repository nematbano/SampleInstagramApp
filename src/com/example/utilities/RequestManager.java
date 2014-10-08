package com.example.utilities;

import android.content.Context;
import com.example.utilities.interfaces.OnResponseHandler;

public class RequestManager {
	private static RequestManager instance = null;

	public static RequestManager getInstance()
	{
		 if (instance == null) {
	            instance = new RequestManager();
	        }
	        return instance;
	}
	
	public void getImages(Context context,OnResponseHandler responseHandler){
		(new InstagramApp(context)).fetchImages(responseHandler);
	}
	
	public void getAccessToken(Context context,final String code,OnResponseHandler responseHandler)
	{
		(new InstagramApp(context)).getAccessToken(code,responseHandler);
	}
	
	public void resetAccessToken(Context context)
	{
		(new InstagramApp(context)).resetAccessToken();
	}
}

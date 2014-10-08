package com.example.utilities;

public class ApplicationData {
	public static final String CLIENT_ID = "657f4eea23fd432c84fd0df30ba82ab2";
	public static final String CLIENT_SECRET = "2e597487cee34c39ab631e1fa35020a9";
	public static final String CALLBACK_URL = "http://instagram.com/";
	
	private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
	
	
	private static String mAuthUrl;
	
	public static String getAuthUrl()
	{
		mAuthUrl = AUTH_URL + "?client_id=" + CLIENT_ID + "&redirect_uri="
				+ CALLBACK_URL + "&response_type=code&display=touch&scope=likes+comments+relationships";
		return mAuthUrl;
	}
	

}

package com.example.utilities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;


public class InstagramSession {

	private SharedPreferences sharedPref;
	private Editor editor;

	private static final String SHARED = "Instagram_Preferences";
	private static final String API_USERNAME = "username";
	private static final String API_ID = "id";
	private static final String API_NAME = "name";
	private static final String API_ACCESS_TOKEN = "access_token";

	public InstagramSession(Context context) {
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
		editor = sharedPref.edit();
	}

	/**
	 * Store access token
	 */
	public void storeAccessToken(String accessToken, String id, String username, String name) {
		editor.putString(API_ID, id);
		editor.putString(API_NAME, name);
		editor.putString(API_ACCESS_TOKEN, accessToken);
		editor.putString(API_USERNAME, username);
		editor.commit();
	}

	/**
	 * Store access token
	 */
	public void storeAccessToken(String accessToken) {
		editor.putString(API_ACCESS_TOKEN, accessToken);
		editor.commit();
	}

	/**
	 * Reset access token and user name
	 */
	public void resetAccessToken() {
		editor.putString(API_ID, null);
		editor.putString(API_NAME, null);
		editor.putString(API_ACCESS_TOKEN, null);
		editor.putString(API_USERNAME, null);
		editor.commit();
	}

	/**
	 * Get user name
	 */
	public String getUsername() {
		return sharedPref.getString(API_USERNAME, null);
	}
	
	/**
	 * Get id
	 */
	public String getId() {
		return sharedPref.getString(API_ID, null);
	}
	
	/**
	 * @return name
	 */
	public String getName() {
		return sharedPref.getString(API_NAME, null);
	}

	/**
	 * Get access token
	 * @return Access token
	 */
	public String getAccessToken() {
		return sharedPref.getString(API_ACCESS_TOKEN, null);
	}

}
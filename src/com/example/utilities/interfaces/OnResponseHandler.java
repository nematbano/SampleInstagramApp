/*
 * 
 * A Callback interface for Activity to decouple the response and error from the server.
 */
package com.example.utilities.interfaces;

import java.util.ArrayList;

public interface OnResponseHandler {
	public <T> void onResponse( ArrayList<String> urls);
	public abstract void onSuccess();
	public void onError(String error);
}

package com.example.instasampleapp;

import java.util.ArrayList;

import com.example.images.ItemAdapter;
import com.example.utilities.RequestManager;
import com.example.utilities.interfaces.OnResponseHandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

public class HomeActivity extends Activity implements OnResponseHandler{
	private GridView grid;
	private ItemAdapter adapter;
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_home);
	        Button logout = (Button) findViewById(R.id.button_logout);
			logout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					RequestManager.getInstance().resetAccessToken(getApplicationContext());
					Intent i = new Intent(HomeActivity.this, MainActivity.class);
	                startActivity(i);
	 
	                // close this activity
	                finish();
				}
			});
	        grid = (GridView) findViewById(R.id.gridview);
			adapter = new ItemAdapter(HomeActivity.this,null);
			grid.setAdapter(adapter);
	        RequestManager.getInstance().getImages(getContext(), HomeActivity.this);
			
	 }
	 
	 public Context getContext() {
		    return (Context)this;
		}

	@Override
	public <T> void onResponse(ArrayList<String> urls) {
		// TODO Auto-generated method stub
		adapter.setUrls(urls);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onSuccess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(String error) {
		// TODO Auto-generated method stub
		Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
	}
}

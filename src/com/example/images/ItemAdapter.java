package com.example.images;

import java.util.ArrayList;



import android.app.Activity;

import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.TextView;

public class ItemAdapter extends BaseAdapter {
	private ArrayList<String> urls;
	private Activity activity;
	

	public ItemAdapter(Activity a, ArrayList<String> urls) {
		this.urls = urls;
		activity = a;
	}

	public static class ViewHolder{
		public TextView username;
		public TextView message;
		public ImageView image;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView img = null;
		 
        if (convertView == null) {
            img = new ImageView(activity);
     } else {
            img = (ImageView) convertView;
        }
 
       DownloadImageTask.getInstance().loadBitmap(urls.get(position), img);
        return img;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(urls!=null)
		return urls.size();
		else return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		if(urls!=null)
		return urls.get(arg0);
		else return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	
	public void setUrls(ArrayList<String> sequenceList)
	{
		this.urls = sequenceList;
		 this.notifyDataSetChanged();
	}
}
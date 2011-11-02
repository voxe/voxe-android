package com.joinplato.android;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ProgramCategoryAdapter extends BaseAdapter {
	
	private final Context context;

	public ProgramCategoryAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return 20;
	}

	@Override
	public NewsElement getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			ImageView imageView = new ImageView(context);
			imageView.setImageResource(R.drawable.recycle);
			convertView = imageView;
		}
		
		return convertView;
	}

}

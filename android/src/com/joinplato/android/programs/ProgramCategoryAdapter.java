package com.joinplato.android.programs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.joinplato.android.R;
import com.joinplato.android.news.NewsElement;

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
			convertView = View.inflate(context, R.layout.program_category_item, null);
		}

		return convertView;
	}

}

package com.joinplato.android.news;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joinplato.android.R;

public class NewsAdapter extends BaseAdapter {

	private final List<NewsElement> news;
	private final Context context;

	public NewsAdapter(Context context, List<NewsElement> news) {
		this.context = context;
		this.news = news;
	}

	@Override
	public int getCount() {
		return news.size();
	}

	@Override
	public NewsElement getItem(int position) {
		return news.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TextView titleView;
		if (convertView != null) {
			titleView = (TextView) convertView.getTag();
		} else {
			ViewGroup newsItemView = (ViewGroup) View.inflate(context, R.layout.news_item, null);
			convertView = newsItemView;
			titleView = (TextView) newsItemView.findViewById(R.id.newsTitle);
			newsItemView.setTag(titleView);
		}

		CharSequence title = getItem(position).getTitle();
		titleView.setText(title);

		return convertView;
	}

}

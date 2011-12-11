package com.joinplato.android.webview;

import java.util.List;

import android.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joinplato.android.model.Theme;

public class ThemeAdapter extends BaseAdapter {

	private final Context context;
	private List<Theme> themes;

	public ThemeAdapter(Context context, List<Theme> themes) {
		this.context = context;
		this.themes = themes;
	}

	@Override
	public int getCount() {
		return themes.size();
	}

	@Override
	public Theme getItem(int position) {
		return themes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		TextView textView;
		if (convertView == null) {
			textView = (TextView) View.inflate(context, R.layout.simple_list_item_1, null);
		} else {
			textView = (TextView) convertView;
		}
		
		Theme theme = getItem(position);
		
		textView.setText(theme.name);
		
		return textView;
	}

	public void updateThemes(List<Theme> themes) {
		this.themes = themes;
		notifyDataSetChanged();
	}

}

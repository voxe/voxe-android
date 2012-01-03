package org.voxe.android.webview;

import java.util.List;

import org.voxe.android.model.Tag;

import android.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TagAdapter extends BaseAdapter {

	private final Context context;
	private List<Tag> tags;

	public TagAdapter(Context context, List<Tag> tags) {
		this.context = context;
		this.tags = tags;
	}

	@Override
	public int getCount() {
		return tags.size();
	}

	@Override
	public Tag getItem(int position) {
		return tags.get(position);
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

		Tag tag = getItem(position);

		textView.setText((position + 1) + ". " + tag.getHackedTagName());

		return textView;
	}

	public void updateThemes(List<Tag> tags) {
		this.tags = tags;
		notifyDataSetChanged();
	}

}

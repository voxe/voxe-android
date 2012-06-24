package org.voxe.android.adapter;

import java.util.List;

import org.voxe.android.R;
import org.voxe.android.model.Candidate;
import org.voxe.android.model.Tag;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;

@EBean
public class SelectTagAdapter extends BaseAdapter {

	@RootContext
	Context context;

	private List<Tag> tags;

	public void init(List<Tag> tags) {
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

		TextView tagView;
		ImageView tagIconView;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.tag_list_item, null);
			tagView = (TextView) convertView.findViewById(R.id.tag);
			tagIconView = (ImageView) convertView.findViewById(R.id.tagIcon);
			convertView.setTag(R.id.tag, tagView);
			convertView.setTag(R.id.tagIcon, tagIconView);
		} else {
			tagView = (TextView) convertView.getTag(R.id.tag);
			tagIconView = (ImageView) convertView.getTag(R.id.tagIcon);
		}

		Tag tag = getItem(position);

		String tagName = tag.getName();

		tagView.setText(tagName);

		if (tag.icon != null && tag.icon.bitmap != null) {
			tagIconView.setImageBitmap(tag.icon.bitmap);
		} else {
			tagIconView.setImageResource(Candidate.getDefaultCandidateImageId());
		}

		return convertView;
	}

}

package org.voxe.android.tag;

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

public class SelectTagAdapter extends BaseAdapter {
	
	private static class ViewHolder {
		public final TextView tagView;
		public final ImageView tagIconView;
		
		public ViewHolder(TextView tagView, ImageView tagIconView) {
			this.tagView = tagView;
			this.tagIconView = tagIconView;
		}
	}

	private final Context context;
	private List<Tag> tags;

	public SelectTagAdapter(Context context, List<Tag> tags) {
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

		TextView tagView;
		ImageView tagIconView;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.tag_list_item, null);
			tagView = (TextView) convertView.findViewById(R.id.tag);
			tagIconView = (ImageView) convertView.findViewById(R.id.tagIcon);
			convertView.setTag(new ViewHolder(tagView, tagIconView));
		} else {
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			tagView = viewHolder.tagView;
			tagIconView = viewHolder.tagIconView;
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

	public void updateTags(List<Tag> tags) {
		this.tags = tags;
		notifyDataSetChanged();
	}

}

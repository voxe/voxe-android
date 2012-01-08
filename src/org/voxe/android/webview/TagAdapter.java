package org.voxe.android.webview;

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

public class TagAdapter extends BaseAdapter {
	
	private static class ViewHolder {
		public final TextView tagView;
		public final ImageView tagIconView;
		public final TextView tagChildsView;
		
		public ViewHolder(TextView tagView, ImageView tagIconView, TextView tagChildsView) {
			this.tagView = tagView;
			this.tagIconView = tagIconView;
			this.tagChildsView = tagChildsView;
		}
	}

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

		TextView tagView;
		ImageView tagIconView;
		TextView tagChildsView;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.tag_list_item, null);
			tagView = (TextView) convertView.findViewById(R.id.tag);
			tagIconView = (ImageView) convertView.findViewById(R.id.tagIcon);
			tagChildsView = (TextView) convertView.findViewById(R.id.tagChilds);
			convertView.setTag(new ViewHolder(tagView, tagIconView, tagChildsView));
		} else {
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			tagView = viewHolder.tagView;
			tagIconView = viewHolder.tagIconView;
			tagChildsView = viewHolder.tagChildsView;
		}

		Tag tag = getItem(position);

		String tagName = (position + 1) + ". " + tag.getHackedTagName();
		
		tagView.setText(tagName);
		
		if (tag.icon != null && tag.icon.bitmap != null) {
			tagIconView.setImageBitmap(tag.icon.bitmap);
		} else {
			tagIconView.setImageResource(Candidate.getDefaultCandidateImageId());
		}
		
		tagChildsView.setText(tag.getChildTagsJoined());
		
		return convertView;
	}

	public void updateThemes(List<Tag> tags) {
		this.tags = tags;
		notifyDataSetChanged();
	}

}

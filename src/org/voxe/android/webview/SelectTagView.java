package org.voxe.android.webview;

import java.util.ArrayList;
import java.util.List;

import org.voxe.android.R;
import org.voxe.android.model.Tag;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.select_theme)
public class SelectTagView extends FrameLayout {

	public interface OnTagSelectedListener {
		void onTagSelected();
	}

	@ViewById
	ListView list;

	private TagAdapter tagAdapter;

	private OnTagSelectedListener onTagSelectedListener;

	private Tag selectedTag;

	public SelectTagView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@AfterViews
	void initAdapter() {
		tagAdapter = new TagAdapter(getContext(), new ArrayList<Tag>());
		list.setAdapter(tagAdapter);
	}

	public void updateTags(List<Tag> tags) {
		tagAdapter.updateThemes(tags);
	}

	@ItemClick
	void listItemClicked(Tag selectedTag) {
		this.selectedTag = selectedTag;
		if (onTagSelectedListener != null) {
			onTagSelectedListener.onTagSelected();
		}
	}
	
	public Tag getSelectedTag() {
		return selectedTag;
	}

	public void setOnTagSelectedListener(OnTagSelectedListener onTagSelectedListener) {
		this.onTagSelectedListener = onTagSelectedListener;
	}

}

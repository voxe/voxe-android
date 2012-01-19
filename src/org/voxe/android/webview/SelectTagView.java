package org.voxe.android.webview;

import java.util.ArrayList;
import java.util.List;

import org.voxe.android.R;
import org.voxe.android.model.Tag;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.select_theme)
public class SelectTagView extends FrameLayout {

	private static final String SELECTED_TAG_ID_PREF = "selectedTagId";

	@ViewById
	ListView list;

	private TagAdapter tagAdapter;

	private PageController pageController;

	private Tag selectedTag;

	private SharedPreferences sharedPreferences;

	public SelectTagView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sharedPreferences = context.getSharedPreferences("select_tag", Context.MODE_PRIVATE);
	}

	@AfterViews
	void initAdapter() {
		tagAdapter = new TagAdapter(getContext(), new ArrayList<Tag>());
		list.setAdapter(tagAdapter);
	}

	public void updateTags(List<Tag> tags) {
		tagAdapter.updateThemes(tags);

		String selectedTagId = sharedPreferences.getString(SELECTED_TAG_ID_PREF, "");
		if (selectedTagId != "") {
			for (Tag tag : tags) {
				if (tag.id.equals(selectedTagId)) {
					this.selectedTag = tag;
					break;
				}
			}
		}

	}

	@ItemClick
	void listItemClicked(Tag selectedTag) {
		this.selectedTag = selectedTag;
		sharedPreferences.edit().putString(SELECTED_TAG_ID_PREF, selectedTag.id).commit();
		pageController.showComparisonPage();
	}

	public Tag getSelectedTag() {
		return selectedTag;
	}

	public void setPageController(PageController pageController) {
		this.pageController = pageController;
	}

}

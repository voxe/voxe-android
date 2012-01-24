package org.voxe.android.webview;

import java.util.ArrayList;
import java.util.List;

import org.voxe.android.R;
import org.voxe.android.model.Tag;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.select_tag_content)
public class SelectTagView extends FrameLayout {

	private static final String SELECTED_TAG_ID_PREF = "selectedTagId";

	@ViewById
	ListView list;

	@ViewById
	TextView selectedTagName;

	@ViewById
	ImageView selectedTagIcon;

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
		tagAdapter.updateTags(tags);

		String selectedTagId = sharedPreferences.getString(SELECTED_TAG_ID_PREF, "");
		if (selectedTagId != "") {
			for (Tag tag : tags) {
				if (tag.id.equals(selectedTagId)) {
					this.selectedTag = tag;
					break;
				}
			}
		}

		showSelectedTag();

	}

	@ItemClick
	void listItemClicked(Tag selectedTag) {
		updateSelectedTag(selectedTag);
		pageController.showComparisonPage();
	}

	private void showSelectedTag() {
		if (selectedTag != null) {
			selectedTagName.setText(selectedTag.getName());
			selectedTagIcon.setImageBitmap(selectedTag.icon.bitmap);
			pageController.updateSelectedTag(selectedTag);
		}
	}

	public Tag getSelectedTag() {
		return selectedTag;
	}

	public void setPageController(PageController pageController) {
		this.pageController = pageController;
	}

	@Click
	void selectTagButtonClicked() {
		pageController.showComparisonPage();
	}

	public void updateSelectedTag(Tag selectedTag) {
		this.selectedTag = selectedTag;
		sharedPreferences.edit().putString(SELECTED_TAG_ID_PREF, selectedTag.id).commit();
		showSelectedTag();
	}

}

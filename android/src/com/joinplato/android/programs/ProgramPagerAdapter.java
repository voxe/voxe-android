package com.joinplato.android.programs;

import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joinplato.android.R;
import com.joinplato.android.common.Candidate;

public class ProgramPagerAdapter extends PagerAdapter {

	private List<Candidate> candidates;
	private final Context context;

	public ProgramPagerAdapter(Context context, List<Candidate> candidates) {
		this.context = context;
		this.candidates = candidates;
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View) view);
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public int getCount() {
		return candidates.size();
	}
	
	public int getItemPosition(Object object) {
	    return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(View collection, int position) {

		Candidate candidate = candidates.get(position);

		ViewGroup viewGroup = (ViewGroup) View.inflate(context, R.layout.candidate_program_item, null);
		TextView nameView = (TextView) viewGroup.findViewById(R.id.candidateName);
		ImageView imageView = (ImageView) viewGroup.findViewById(R.id.candidateImage);
		imageView.setImageResource(candidate.getImageId());

		CharSequence name = candidate.getName();
		nameView.setText(name);

		((ViewPager) collection).addView(viewGroup);

		return viewGroup;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

	public void updateCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
		notifyDataSetChanged();
	}

}

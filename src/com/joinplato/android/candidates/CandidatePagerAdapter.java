package com.joinplato.android.candidates;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_SUBJECT;
import static android.content.Intent.EXTRA_TEXT;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.joinplato.android.R;
import com.joinplato.android.model.Candidate;

public class CandidatePagerAdapter extends PagerAdapter {

	private final List<Candidate> candidates;
	private final Context context;

	public CandidatePagerAdapter(Context context, List<Candidate> candidates) {
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

	@Override
	public Object instantiateItem(View collection, int position) {

		Candidate candidate = candidates.get(position);

		ViewGroup viewGroup = (ViewGroup) View.inflate(context, R.layout.candidate_detail_item, null);
		TextView nameView = (TextView) viewGroup.findViewById(R.id.candidateName);
		Button wikiButton = (Button) viewGroup.findViewById(R.id.wiki);
		Button shareButton = (Button) viewGroup.findViewById(R.id.share);
		ImageView imageView = (ImageView) viewGroup.findViewById(R.id.candidateImage);
		imageView.setImageResource(candidate.getImageId());

		final CharSequence name = candidate.getName();
		nameView.setText(name);

		wikiButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://fr.m.wikipedia.org/wiki/Eva_Joly"));
				context.startActivity(browserIntent);
			}
		});

		shareButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent sharingIntent = new Intent(ACTION_SEND);
				sharingIntent.setType("text/plain");
				sharingIntent.putExtra(EXTRA_TEXT, "Je soutiens " + name+ " pour les élections http://joinplato.herokuapp.com/elections/4ed1cb0203ad190006000001");
				context.startActivity(Intent.createChooser(sharingIntent, "Partager via"));
			}
		});

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

}

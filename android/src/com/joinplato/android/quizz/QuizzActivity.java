package com.joinplato.android.quizz;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.ViewById;
import com.joinplato.android.R;
import com.joinplato.android.actionbar.ActionBarActivity;
import com.joinplato.android.common.Candidate;
import com.joinplato.android.common.HomeHelper;
import com.joinplato.android.quizz.QuizzAdapter.OnAnswerListener;

@EActivity(R.layout.quizz)
public class QuizzActivity extends ActionBarActivity implements OnAnswerListener {
	
	public static void start(Context context) {
		context.startActivity(new Intent(context, QuizzActivity_.class));
	}	

	@ViewById
	ListView list;

	@ViewById
	ImageView candidateImage;

	@ViewById
	TextView candidateName;

	private final List<Candidate> candidates = Candidate.mockCandidates();

	private final Random random = new Random();

	private QuizzAdapter quizzAdapter;

	@AfterViews
	void mockList() {
		updateCandidateRandomly();
		quizzAdapter = new QuizzAdapter(this, QuizzQuestion.mockQuizzQuestions());
		list.setAdapter(quizzAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// This is not working, it never gets called
				View convertView = list.getChildAt(position - list.getFirstVisiblePosition());
				@SuppressWarnings("unchecked")
				Pair<Spinner, TextView> views = (Pair<Spinner, TextView>) convertView.getTag();
				Spinner quizzChoice = views.first;
				quizzChoice.performClick();
			}
		});
		quizzAdapter.setAnswerListener(this);
	}

	private void updateCandidateRandomly() {
		Candidate candidate = candidates.get(random.nextInt(candidates.size()));
		candidateName.setText(candidate.getName());
		candidateImage.setImageResource(candidate.getImageId());
	}

	@Override
	public void onNewAnswer() {
		updateCandidateRandomly();
	}
	
	@OptionsItem
	public void homeSelected() {
		HomeHelper.backToHome(this);
	}

}

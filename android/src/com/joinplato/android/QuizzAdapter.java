package com.joinplato.android;

import java.util.List;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class QuizzAdapter extends BaseAdapter {

	public interface OnAnswerListener {
		void onNewAnswer();
	}

	public class AnswerSelectedListener implements OnItemSelectedListener {

		QuizzQuestion quizzQuestion;

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (quizzQuestion != null) {
				if (answerListener != null && position != quizzQuestion.getAnswer()) {
					answerListener.onNewAnswer();
				}
				quizzQuestion.setAnswer(position);

			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}

	}

	private final List<QuizzQuestion> questions;
	private final Context context;
	private ArrayAdapter<CharSequence> quizzChoiceAdapter;

	private OnAnswerListener answerListener;

	public QuizzAdapter(Context context, List<QuizzQuestion> questions) {
		this.context = context;
		this.questions = questions;
		quizzChoiceAdapter = ArrayAdapter.createFromResource(context, R.array.quizz_choices, android.R.layout.simple_spinner_item);
		quizzChoiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	@Override
	public int getCount() {
		return questions.size();
	}

	@Override
	public QuizzQuestion getItem(int position) {
		return questions.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final Spinner quizzChoice;
		TextView quizzQuestionView;
		AnswerSelectedListener answerSelectedListener;
		if (convertView != null) {
			@SuppressWarnings("unchecked")
			Pair<Spinner, TextView> views = (Pair<Spinner, TextView>) convertView.getTag();
			quizzChoice = views.first;
			quizzQuestionView = views.second;
			answerSelectedListener = (AnswerSelectedListener) quizzChoice.getOnItemSelectedListener();
		} else {
			ViewGroup viewGroup = (ViewGroup) View.inflate(context, R.layout.quizz_item, null);
			convertView = viewGroup;
			quizzChoice = (Spinner) viewGroup.findViewById(R.id.quizzChoice);
			quizzQuestionView = (TextView) viewGroup.findViewById(R.id.quizzQuestion);
			viewGroup.setTag(Pair.create(quizzChoice, quizzQuestionView));
			answerSelectedListener = new AnswerSelectedListener();
			quizzChoice.setOnItemSelectedListener(answerSelectedListener);

			quizzQuestionView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					quizzChoice.performClick();
				}
			});
		}

		final QuizzQuestion quizzQuestion = getItem(position);
		String question = quizzQuestion.getQuestion();
		quizzQuestionView.setText(question);
		answerSelectedListener.quizzQuestion = quizzQuestion;
		quizzChoice.setAdapter(quizzChoiceAdapter);
		quizzChoice.setSelection(quizzQuestion.getAnswer());

		return convertView;
	}

	public void setAnswerListener(OnAnswerListener answerListener) {
		this.answerListener = answerListener;
	}

}

package com.joinplato.android.quizz;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Random;

public class QuizzQuestion {

	public static List<QuizzQuestion> mockQuizzQuestions() {
		return asList(new QuizzQuestion("Il faut nationaliser les banques au plus vite"), //
				new QuizzQuestion("Il faut défiscaliser les heures supplémentaires"), //
				new QuizzQuestion("Le nucléaire est la seule énergie viable dans les 30 ans à venir"), //
				new QuizzQuestion("L'immigration doit être contrôlée rigoureusement"), //
				new QuizzQuestion("Il faut tendre vers un fédéralisme européen et y intégrer la Turquie"), //
				new QuizzQuestion("Il faut démondialiser le trafic de stupéfiants"), //
				new QuizzQuestion("Il est interdit d'interdire"), //
				new QuizzQuestion("Plus on est de fous, moins il y a de riz"), //
				new QuizzQuestion("Il faut plus de professeurs à l'éducation nationale"), //
				new QuizzQuestion("Il faut rendre les procureurs indépendants de l'état"), //
				new QuizzQuestion("Le président doit pouvoir être destitué par les deux assemblées"), //
				new QuizzQuestion("Il faut réintroduire les chatiments corporels à l'école"), //
				new QuizzQuestion("Les extra-terrestres sont des sous hommes"), //
				new QuizzQuestion("Voter à gauche permet de perdre 10kg en 3 semaines"), //
				new QuizzQuestion("Il faut interdire les outils d'aide au vote citoyen") //
		);
	}

	private final String question;

	private int answer;

	public QuizzQuestion(String question) {
		this.question = question;
		this.answer = new Random().nextInt(3);
	}

	public int getAnswer() {
		return answer;
	}

	public String getQuestion() {
		return question;
	}

	public void setAnswer(int answer) {
		this.answer = answer;
	}

}

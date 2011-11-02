package com.joinplato.android;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Random;

public class QuizzQuestion {

	public static List<QuizzQuestion> mockQuizzQuestions() {
		return asList(new QuizzQuestion("Il faut nationaliser les banques au plus vite"), //
				new QuizzQuestion("Il faut d�fiscaliser les heures suppl�mentaires"), //
				new QuizzQuestion("Le nucl�aire est la seule �nergie viable dans les 30 ans � venir"), //
				new QuizzQuestion("L'immigration doit �tre contr�l�e rigoureusement"), //
				new QuizzQuestion("Il faut tendre vers un f�d�ralisme europ�en et y int�grer la Turquie"), //
				new QuizzQuestion("Il faut d�mondialiser le trafic de stup�fiants"), //
				new QuizzQuestion("Il est interdit d'interdire"), //
				new QuizzQuestion("Plus on est de fous, moins il y a de riz"), //
				new QuizzQuestion("Il faut plus de professeurs � l'�ducation nationale"), //
				new QuizzQuestion("Il faut rendre les procureurs ind�pendants de l'�tat"), //
				new QuizzQuestion("Le pr�sident doit pouvoir �tre destitu� par les deux assembl�es"), //
				new QuizzQuestion("Il faut r�introduire les chatiments corporels � l'�cole"), //
				new QuizzQuestion("Les extra-terrestres sont des sous hommes"), //
				new QuizzQuestion("Voter � gauche permet de perdre 10kg en 3 semaines"), //
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

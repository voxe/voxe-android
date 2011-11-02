package com.joinplato.android;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Candidate implements Serializable, Comparable<Candidate> {

	private static final long serialVersionUID = 1L;
	
	public static List<Candidate> mockCandidates() {
		List<Candidate> candidates = Arrays.asList(new Candidate("Fran�ois Hollande"), new Candidate("Nicolas Sarkozy"), new Candidate("Jean-Luc M�lanchon"), new Candidate("Marine Lepen"), new Candidate("Nathalie Arthaud"), new Candidate("Fran�ois Bayrou"), new Candidate("Christine Boutin"), new Candidate("Jean-Pierre Chev�nement"), new Candidate("Eva Joly"), new Candidate("Nicolas Dupont-Aignan"));
		Collections.sort(candidates);
		return candidates;
	}
	
	private final String name;
	
	public Candidate(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public int compareTo(Candidate another) {
		return name.compareTo(another.name);
	}

}

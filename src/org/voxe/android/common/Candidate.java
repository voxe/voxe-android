package org.voxe.android.common;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.voxe.android.R;

@Deprecated
public class Candidate implements Serializable, Comparable<Candidate> {

	private static final long serialVersionUID = 1L;

	public static List<Candidate> mockCandidates() {
		List<Candidate> candidates = Arrays.asList( //
				
				);
//				new Candidate("François", "Hollande", R.drawable.francois_hollande), //
//				new Candidate("Nicolas", "Sarkozy", R.drawable.nicolas_sarkozy), //
//				new Candidate("Jean-Luc", "Mélanchon", R.drawable.jean_luc_melenchon), //
//				new Candidate("Marine", "Lepen", R.drawable.marine_le_pen), //
//				new Candidate("Nathalie", "Arthaud", R.drawable.nathalie_arthaud), //
//				new Candidate("François", "Bayrou", R.drawable.francois_bayrou), //
//				new Candidate("Christine", "Boutin", R.drawable.christine_boutin), //
//				new Candidate("Jean-Pierre", "Chevènement", R.drawable.jean_pierre_chevenement), //
//				new Candidate("Eva", "Joly", R.drawable.eva_joly), //
//				new Candidate("Nicolas", "Dupont-Aignan", R.drawable.nicolas_dupont_aignan));
		Collections.sort(candidates);
		return candidates;
	}

	private final String firstName;
	private final String lastName;
	private String name;
	private final int imageId;

	public Candidate(String firstName, String lastName, int imageId) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.imageId = imageId;
		name = firstName + " " + lastName;
	}

	public String getName() {
		return name;
	}

	public int getImageId() {
		return imageId;
	}

	@Override
	public int compareTo(Candidate another) {
		int lastNameComparison = lastName.compareTo(another.lastName);

		if (lastNameComparison == 0) {
			return firstName.compareTo(another.firstName);
		}

		return lastNameComparison;
	}

}

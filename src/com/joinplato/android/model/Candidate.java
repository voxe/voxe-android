package com.joinplato.android.model;

import java.io.Serializable;

import com.joinplato.android.R;

public class Candidate implements Serializable, Comparable<Candidate> {

	private static final long serialVersionUID = 1L;

	public String id;
	public String firstName;
	public String lastName;

	private transient String name;

	public Photo photo;

	@Override
	public int compareTo(Candidate another) {
		int lastNameComparison = lastName.compareTo(another.lastName);

		if (lastNameComparison == 0) {
			return firstName.compareTo(another.firstName);
		}

		return lastNameComparison;
	}

	public CharSequence getName() {
		if (name == null) {
			name = firstName + " " + lastName;
		}
		return name;
	}

	public static int getDefaultCandidateImageId() {
		return R.drawable.default_photo;
	}

}

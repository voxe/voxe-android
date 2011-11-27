package com.joinplato.android.model;

import java.io.Serializable;

import com.joinplato.android.R;


public class Candidate implements Serializable, Comparable<Candidate> {
	
	private static final long serialVersionUID = 1L;
	
	public String id;
	public String firstName;
	public String lastName;
	
	@Override
	public int compareTo(Candidate another) {
		int lastNameComparison = lastName.compareTo(another.lastName);

		if (lastNameComparison == 0) {
			return firstName.compareTo(another.firstName);
		}

		return lastNameComparison;
	}

	public CharSequence getName() {
		return firstName + " " + lastName;
	}

	public int getImageId() {
		return R.drawable.christine_boutin;
	}
	
}

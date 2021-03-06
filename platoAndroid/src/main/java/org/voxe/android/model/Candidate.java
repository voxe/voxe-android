package org.voxe.android.model;

import java.io.Serializable;

import org.voxe.android.R;

import android.widget.ImageView;

public class Candidate implements Serializable, Comparable<Candidate> {

	private static final long serialVersionUID = 1L;

	public String id;
	public String firstName;
	public String lastName;

	private transient String name;

	public String candidacyId;
	public String candidacyNamespace;

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

	public void insertPhoto(ImageView imageView) {
		if (photo != null && photo.photoBitmap != null) {
			imageView.setImageBitmap(photo.photoBitmap);
		} else {
			imageView.setImageResource(getDefaultCandidateImageId());
		}

	}

}

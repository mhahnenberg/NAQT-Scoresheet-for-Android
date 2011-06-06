package com.naqtscoresheet;

import java.util.List;

public class Bonus {
	private final int points;
	private final List<Boolean> parts;
	public Bonus(int points, List<Boolean> parts) {
		this.points = points;
		this.parts = parts;
	}
	
	public int getPoints() {
		return this.points;
	}
	
	public boolean isPartCorrect(int partNum) {
		return this.parts.get(partNum-1);
	}
}

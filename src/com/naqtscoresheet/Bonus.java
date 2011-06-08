package com.naqtscoresheet;

import java.io.Serializable;
import java.util.List;

public class Bonus implements Serializable {
	private static final long serialVersionUID = 2029769330695269039L;
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

package com.naqtscoresheet;

import java.io.Serializable;
import java.util.List;

import com.naqtscoresheet.dom.DOMNode;

public class Bonus implements Serializable, DOMNode {
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

	@Override
	public void outputXML(StringBuilder input) {
		// TODO Auto-generated method stub
		input.append("<bonus>");
		input.append(this.points);
		input.append("</bonus>\n");
	}
}

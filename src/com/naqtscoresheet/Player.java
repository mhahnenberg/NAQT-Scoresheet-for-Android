package com.naqtscoresheet;

import java.io.Serializable;

import com.naqtscoresheet.dom.DOMNode;

public class Player implements Serializable, DOMNode {
	private static final long serialVersionUID = -1191776898623429448L;
	private final String name;
	public Player(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean equals(Object o) {
		return o instanceof Player && ((Player)o).getName().equals(this.name);
	}
	
	public int hashCode() {
		return this.name.hashCode();
	}
	
	public String toString() {
		return this.name;
	}

	@Override
	public void outputXML(StringBuilder input) {
		input.append("<player>");
		input.append(this.name);
		input.append("</player>\n");
	}
}

package com.naqtscoresheet;

public class Player {
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
}

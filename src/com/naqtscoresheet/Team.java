package com.naqtscoresheet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team implements Serializable {
	private static final long serialVersionUID = 6446404867455448712L;
	private String name;
	private int score;
	private final List<Player> currPlayers;
	private final List<Player> allPlayers;
	
	public Team(String name) {
		this.name = name;
		this.score = 0;
		this.currPlayers = new ArrayList<Player>();
		this.allPlayers = new ArrayList<Player>();
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void addPoints(int amt) {
		this.score += amt;
	}

	public void subPoints(int amt) {
		this.score -= amt;
	}
	
	public int getScore() {
		return this.score;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Team)) {
			return false;
		}
		return ((Team)o).getName().equals(this.name);
	}
	
	public void addPlayer(Player player) {
		this.currPlayers.add(player);
		this.allPlayers.add(player);
	}
	
	public void removePlayer(String playerName) {
		for (int i = 0; i < this.currPlayers.size(); i++) {
			if (this.currPlayers.get(i).getName().equals(playerName)) {
				this.currPlayers.remove(i);
			}
		}
	}
	
	public Player getPlayer(String playerName) {
		int idx = this.currPlayers.indexOf(new Player(playerName));
		return idx == -1 ? null : this.currPlayers.get(idx);
	}
	
	public List<Player> getPlayers() {
		return Collections.unmodifiableList(this.currPlayers);
	}
	
	public String toString() {
		return this.name;
	}
}

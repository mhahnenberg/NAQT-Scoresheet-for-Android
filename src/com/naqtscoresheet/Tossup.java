package com.naqtscoresheet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.naqtscoresheet.dom.DOMNode;

public class Tossup implements Serializable, DOMNode {
	private static final long serialVersionUID = -7974143477552157831L;
	private final int winnerPoints;
	private final int loserPoints;
	private final Team winnerTeam;
	private final Team loserTeam;
	private Player winnerPlayer;
	private Player loserPlayer;
	private Bonus bonus;
	private final List<Player> aPlayersHeard;
	private final List<Player> bPlayersHeard;
	private final int tossupNum;
	private final boolean isTiebreaker;
	
	// used to get around serialization issues
	private static class BogusPlayer extends Player {
		private static final long serialVersionUID = 8448594428406499460L;

		public BogusPlayer(String name) {
			super(name);
		}
	}

	public Tossup(int tossupNum, Team winnerTeam, int winnerPoints, Team loserTeam, int loserPoints, boolean isTiebreaker) {
		this.tossupNum = tossupNum;
		this.winnerPoints = winnerPoints;
		this.loserPoints = loserPoints;
		this.winnerTeam = winnerTeam;
		this.loserTeam = loserTeam;
		this.winnerPlayer = new BogusPlayer("");
		this.loserPlayer = new BogusPlayer("");
		this.aPlayersHeard = new ArrayList<Player>();
		this.bPlayersHeard = new ArrayList<Player>();
		if (winnerTeam.getName().equals(NAQTScoresheet.teamAName)) {
			this.aPlayersHeard.addAll(winnerTeam.getPlayers());
			this.bPlayersHeard.addAll(loserTeam.getPlayers());
		}
		else {
			this.bPlayersHeard.addAll(winnerTeam.getPlayers());
			this.aPlayersHeard.addAll(loserTeam.getPlayers());
		}
		this.bonus = new Bonus(0, Arrays.asList(false, false, false));
		this.isTiebreaker = isTiebreaker;
	}
	
	public int getWinnerPoints() {
		return this.winnerPoints;
	}
	
	public Team getWinnerTeam() {
		return this.winnerTeam;
	}
	
	public int getLoserPoints() {
		return this.loserPoints;
	}
	
	public Team getLoserTeam() {
		return this.loserTeam;
	}
	
	public void setBonus(Bonus b) {
		this.bonus = b;
	}
	
	public Bonus getBonus() {
		return this.bonus;
	}
	
	public Player getWinnerPlayer() {
		if (this.winnerPlayer instanceof BogusPlayer) {
			return null;
		}
		return this.winnerPlayer;
	}
	
	public void setWinnerPlayer(Player p) {
		this.winnerPlayer = p;
	}
	
	public Player getLoserPlayer() {
		if (this.loserPlayer instanceof BogusPlayer) {
			return null;
		}
		return this.loserPlayer;
	}
	
	public void setLoserPlayer(Player p) {
		this.loserPlayer = p;
	}
	
	public void addPlayer(Team team, Player p) {
		if (team.getName().equals(NAQTScoresheet.teamAName)) {
			this.aPlayersHeard.add(p);
		}
		else {
			this.bPlayersHeard.add(p);
		}
	}
	
	public void removePlayer(Team team, String playerName) {
		List<Player> playersHeard;
		if (team.getName().equals(NAQTScoresheet.teamAName)) {
			playersHeard = this.aPlayersHeard;
		}
		else {
			playersHeard = this.bPlayersHeard;
		}
		playersHeard.remove(team.getPlayer(playerName));
	}
	
	public List<Player> getWinnerPlayers() {
		if (this.winnerTeam.getName().equals(NAQTScoresheet.teamAName)) {
			return Collections.unmodifiableList(this.aPlayersHeard);
		}
		else {
			return Collections.unmodifiableList(this.bPlayersHeard);
		}
	}
	
	public List<Player> getLoserPlayers() {
		if (this.loserTeam.getName().equals(NAQTScoresheet.teamAName)) {
			return Collections.unmodifiableList(this.aPlayersHeard);
		}
		else {
			return Collections.unmodifiableList(this.bPlayersHeard);
		}
	}
	
	public boolean isTiebreaker() {
		return this.isTiebreaker;
	}

	@Override
	public void outputXML(StringBuilder input) {
		input.append("<tossup num=\"" + this.tossupNum + "\">\n");
		input.append("<winner>\n");
		input.append("<points>" + this.winnerPoints + "</points>\n");
		this.winnerTeam.outputXML(input);
		this.winnerPlayer.outputXML(input);
		input.append("</winner>\n");
		input.append("<loser>\n");
		input.append("<points>" + this.loserPoints + "</points>\n");
		this.loserTeam.outputXML(input);
		if (this.loserPlayer != null) {
			this.loserPlayer.outputXML(input);
		}
		input.append("</loser>\n");
		if (this.bonus != null) {
			this.bonus.outputXML(input);
		}
		input.append("</tossup>\n");
	}
}

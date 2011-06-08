package com.naqtscoresheet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Game implements Serializable {
	private static final long serialVersionUID = 4152277972389886782L;
	private int currTossupNum;
	private final int maxTossups;
	private final List<Tossup> tossups;
	private final List<Tossup> tiebreakers;
	private final Team teamA;
	private final Team teamB;
	
	public Game(Team teamA, Team teamB, int maxTossups) {
		this.currTossupNum = 1;
		this.maxTossups = maxTossups;
		this.tossups = new ArrayList<Tossup>();
		this.tiebreakers = new ArrayList<Tossup>();
		this.teamA = teamA;
		this.teamB = teamB;
		
		this.tossups.add(new Tossup(this.teamA, 0, this.teamB, 0));
	}
	
	public Team getTeamA() {
		return this.teamA;
	}
	
	public Team getTeamB() {
		return this.teamB;
	}
	
	public Tossup nextTossup() {
		if (this.currTossupNum + 1 <= maxTossups) {
			this.currTossupNum += 1;
			if (this.tossups.size() < this.currTossupNum) {
				this.tossups.add(new Tossup(this.teamA, 0, this.teamB, 0));
			}
			return this.tossups.get(currTossupNum-1);
		}
		else {
			this.currTossupNum += 1;
			if (this.tiebreakers.size() < this.currTossupNum - this.maxTossups) {
				this.tiebreakers.add(new Tossup(this.teamA, 0, this.teamB, 0));
			}
			return this.tiebreakers.get(this.currTossupNum - this.maxTossups - 1);
		}
	}
	
	public Tossup prevTossup() {
		if (this.currTossupNum - 1 > 0 && this.currTossupNum <= this.maxTossups || this.currTossupNum == this.maxTossups + 1) {
			this.currTossupNum -= 1;
			return this.tossups.get(currTossupNum-1);
		}
		else if (this.currTossupNum - 1 > this.maxTossups) {
			this.currTossupNum -= 1;
			return this.tiebreakers.get(this.currTossupNum - this.maxTossups - 1);
		}
		return null;
	}
	
	public Tossup getNthTossup(int n) {
		if (n > 0 && n <= this.tossups.size()) {
			return this.tossups.get(n-1);
		}
		else if (n > this.maxTossups && n <= this.tiebreakers.size() + this.maxTossups){
			return this.tiebreakers.get(n - this.maxTossups - 1);
		}
		else {
			return null;
		}
	}
	
	public Tossup currTossup() {
		if (this.currTossupNum <= this.maxTossups) {
			return this.tossups.get(currTossupNum-1);
		}
		else {
			return this.tiebreakers.get(this.currTossupNum - this.maxTossups - 1);
		}
	}
	
	public int getCurrTossupNum() {
		return this.currTossupNum;
	}
	
	public void updateCurrTossup(Tossup t) {
		Tossup oldTossup = this.currTossup();
		if (this.currTossupNum <= this.maxTossups) {
			this.tossups.set(currTossupNum-1, t);
		}
		else {
			this.tiebreakers.set(currTossupNum-this.maxTossups-1, t);
		}
		
		// subtract the points from the old winner
		oldTossup.getWinnerTeam().subPoints(oldTossup.getWinnerPoints());
		// subtract the points from the old loser
		oldTossup.getLoserTeam().subPoints(oldTossup.getLoserPoints());
		// add the points to the new winner
		t.getWinnerTeam().addPoints(t.getWinnerPoints());
		// add the points to the new loser
		t.getLoserTeam().addPoints(t.getLoserPoints());
	}
	
	public void updateCurrBonus(Bonus b) {
		if (this.currTossupNum > this.maxTossups) {
			throw new RuntimeException("Tiebreakers can't have bonuses.");
		}
		Tossup t = this.tossups.get(currTossupNum-1);
		Bonus oldBonus = t.getBonus();
		t.setBonus(b);
		
		t.getWinnerTeam().subPoints(oldBonus.getPoints());
		t.getWinnerTeam().addPoints(b.getPoints());
	}
	
	// returns the number of the highest tossup which has been created, but may not have been completed
	private int getHighestTossupNum() {
		if (this.tossups.size() == this.maxTossups) {
			return this.maxTossups + this.tiebreakers.size();
		}
		else {
			return this.tossups.size();
		}
	}
	
	// returns the highest tossup which has been created, but may not have been completed
	private Tossup getHighestTossup() {
		int highestTossupNum = this.getHighestTossupNum();
		if (highestTossupNum > this.maxTossups) {
			return this.tiebreakers.get(highestTossupNum - this.maxTossups - 1);
		}
		else {
			return this.tossups.get(highestTossupNum - 1);
		}
	}
	
	public int tossupsHeard() {
		Tossup highestTossup = this.getHighestTossup();
		if (highestTossup.getWinnerPoints() > 0 || highestTossup.getLoserPoints() < 0) {
			return this.tossups.size() + this.tiebreakers.size();
		}
		else {
			return this.tossups.size() + this.tiebreakers.size() - 1;
		}
	}
}

package com.naqtscoresheet;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class PlayerStatsScreen extends Activity {
	private Game game;
	
	private void updateTeamNames() {
		TextView teamALabel = (TextView)findViewById(R.id.teamaplayerstatslabel);
		TextView teamBLabel = (TextView)findViewById(R.id.teambplayerstatslabel);
		teamALabel.setText(game.getTeamA().getName());
		teamBLabel.setText(game.getTeamB().getName());
	}
	
	private void updatePlayerName(Player p, TableRow row) {
		TextView name = new TextView(this);
		name.setText(p.getName());
		row.addView(name);
	}
	
	private int getPlayerPoints(Team team, Player p) {
		Tossup currTossup = game.getNthTossup(1);
		int i = 1;
		int points = 0;
		while (currTossup != null) {
			if (currTossup.getWinnerTeam().equals(team) && p.equals(currTossup.getWinnerPlayer())) {
				points += currTossup.getWinnerPoints();
			}
			else if (currTossup.getLoserTeam().equals(team) && p.equals(currTossup.getLoserPlayer())) {
				points += currTossup.getLoserPoints();
			}
			i += 1;
			currTossup = game.getNthTossup(i);
		}
		return points;
	}
	
	private void updatePlayerPoints(Team team, Player p, TableRow row) {
		TextView points = new TextView(this);
		points.setText(Integer.toString(getPlayerPoints(team, p)));
		row.addView(points);
	}
	
	private int getPlayerTens(Team team, Player p) {
		Tossup currTossup = game.getNthTossup(1);
		int i = 1;
		int tens = 0;
		while (currTossup != null) {
			if (currTossup.getWinnerTeam().equals(team) && 
				p.equals(currTossup.getWinnerPlayer()) && 
				currTossup.getWinnerPoints() == 10) {
				tens += 1;
			}
			i += 1;
			currTossup = game.getNthTossup(i);
		}
		return tens;
	}
	
	private void updatePlayerTens(Team team, Player p, TableRow row) {
		TextView tens = new TextView(this);
		tens.setText(Integer.toString(getPlayerTens(team, p)));
		row.addView(tens);
	}
	
	private int getPlayerPowers(Team team, Player p) {
		Tossup currTossup = game.getNthTossup(1);
		int i = 1;
		int powers = 0;
		while (currTossup != null) {
			if (currTossup.getWinnerTeam().equals(team) && 
				p.equals(currTossup.getWinnerPlayer()) && 
				currTossup.getWinnerPoints() == 15) {
				powers += 1;
			}
			i += 1;
			currTossup = game.getNthTossup(i);
		}
		return powers;
	}
	
	private void updatePlayerPowers(Team team, Player p, TableRow row) {
		TextView powers = new TextView(this);
		powers.setText(Integer.toString(getPlayerPowers(team, p)));
		row.addView(powers);
	}
	
	private int getPlayerNegs(Team team, Player p) {
		Tossup currTossup = game.getNthTossup(1);
		int i = 1;
		int negs = 0;
		while (currTossup != null) {
			if (currTossup.getLoserTeam().equals(team) && 
				p.equals(currTossup.getLoserPlayer()) && 
				currTossup.getLoserPoints() == -5) {
				negs += 1;
			}
			else if (currTossup.getLoserTeam().equals(team) && 
				p.equals(currTossup.getLoserPlayer())) {
				System.err.println(currTossup.getLoserPoints());
			}
			else {
				System.err.println(currTossup.getLoserTeam() + " " + currTossup.getLoserPlayer() + " " + currTossup.getLoserPoints());
			}
			i += 1;
			currTossup = game.getNthTossup(i);
		}
		return negs;
	}
	
	private void updatePlayerNegs(Team team, Player p, TableRow row) {
		TextView negs = new TextView(this);
		negs.setText(Integer.toString(getPlayerNegs(team, p)));
		row.addView(negs);
	}
	
	private void updatePlayerStats(Team team, Player p) {
		TableLayout table;
		if (team.equals(this.game.getTeamA())) {
			table = (TableLayout)findViewById(R.id.teamaplayertable);
		}
		else {
			table = (TableLayout)findViewById(R.id.teambplayertable);
		}
		// create new row for player
		TableRow newRow = new TableRow(this);
		table.addView(newRow);
		
		updatePlayerName(p, newRow);
		updatePlayerPoints(team, p, newRow);
		updatePlayerTens(team, p, newRow);
		updatePlayerPowers(team, p, newRow);
		updatePlayerNegs(team, p, newRow);
	}
	
	private void updatePlayersStats() {
		List<Player> teamAPlayers = this.game.getTeamA().getPlayers();
		List<Player> teamBPlayers = this.game.getTeamB().getPlayers();

		TableLayout teamATable = (TableLayout)findViewById(R.id.teamaplayertable);
		if (teamAPlayers.size() == 0) {
			TextView noPlayers = new TextView(this);
			noPlayers.setText("No Players Found");
			TableRow noPlayersRow = new TableRow(this);
			noPlayersRow.addView(noPlayers);
			teamATable.addView(noPlayersRow);
		}
		else {
			for (Player p : teamAPlayers) {
				updatePlayerStats(this.game.getTeamA(), p);
			}
		}
		
		TableLayout teamBTable = (TableLayout)findViewById(R.id.teambplayertable);
		if (teamBPlayers.size() == 0) {
			TextView noPlayers = new TextView(this);
			noPlayers.setText("No Players Found");
			TableRow noPlayersRow = new TableRow(this);
			noPlayersRow.addView(noPlayers);
			teamBTable.addView(noPlayersRow);
		}
		else {
			for (Player p : teamBPlayers) {
				updatePlayerStats(this.game.getTeamB(), p);
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_stats);
		
		Bundle bun = getIntent().getExtras();
		this.game = (Game)bun.getSerializable("game");
		
		this.updateTeamNames();
		this.updatePlayersStats();
	}
	
}

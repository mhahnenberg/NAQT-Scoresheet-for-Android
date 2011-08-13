package com.naqtscoresheet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONVisitor implements Visitor {
	private JSONObject retVal;
	private JSONObject game;
	private boolean winnerPlayer, loserPlayer;
	
	public JSONVisitor() {
		this.game = null;
		this.winnerPlayer = this.loserPlayer = false;
	}
	
	@Override
	public void visit(Game g) {
		this.game = new JSONObject();
		JSONArray teams = new JSONArray();
		try {
			g.getTeamA().accept(this);
			this.retVal.put("score", g.getTeamA().getScore());
			teams.put(this.retVal);
			
			g.getTeamB().accept(this);
			this.retVal.put("score", g.getTeamB().getScore());
			teams.put(this.retVal);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		JSONArray tossups = new JSONArray();
		for (int i = 0; g.getNthTossup(i) != null; i++) {
			g.getNthTossup(i).accept(this);
			tossups.put(this.retVal);
		}
		
		try {
			this.game.put("type", "game");
			this.game.put("teams", teams);
			this.game.put("tossups", tossups);
			if (g.getGameID() != null) {
				this.game.put("id", g.getGameID());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(Tossup t) {
		JSONObject tossup = new JSONObject();
		t.getBonus().accept(this);
		JSONObject bonus = this.retVal;
		
		JSONObject winnerTeam = new JSONObject();
		JSONArray winnerPlayers = new JSONArray();
		for (Player p : t.getWinnerPlayers()) {
			if (p.equals(t.getWinnerPlayer())) {
				this.winnerPlayer = true;
			}
			p.accept(this);
			winnerPlayers.put(this.retVal);
			this.winnerPlayer = false;
		}

		JSONObject loserTeam = new JSONObject();
		JSONArray loserPlayers = new JSONArray();
		for (Player p : t.getLoserPlayers()) {
			if (p.equals(t.getLoserPlayer())) {
				this.loserPlayer = true;
			}
			p.accept(this);
			loserPlayers.put(this.retVal);
			this.loserPlayer = false;
		}

		try {
			winnerTeam.put("type", "tossup-team");
			winnerTeam.put("players", winnerPlayers);
			winnerTeam.put("points", t.getWinnerPoints());
			
			loserTeam.put("type", "tossup-team");
			loserTeam.put("players", loserPlayers);
			loserTeam.put("points", t.getLoserPoints());
			
			tossup.put("type", "tossup");
			tossup.put("bonus", bonus);
			tossup.put("winner_team", winnerTeam);
			tossup.put("loser_team", loserTeam);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		this.retVal = tossup;
	}

	@Override
	public void visit(Bonus b) {
		JSONObject bonus = new JSONObject();
		try {
			bonus.put("type", "bonus");
			bonus.put("points", b.getPoints());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.retVal = bonus;
	}

	@Override
	public void visit(Player p) {
		JSONObject player = new JSONObject();
		try {
			player.put("type", "player");
			player.put("name", p.getName());
			if (this.winnerPlayer) {
				player.put("winner", true);
			}
			else if (this.loserPlayer) {
				player.put("loser", true);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		this.retVal = player;
	}

	@Override
	public void visit(Team t) {
		JSONObject team = new JSONObject();
		
		JSONArray players = new JSONArray();
		for (Player p : t.getPlayers()) {
			p.accept(this);
			players.put(this.retVal);
		}
		
		try {
			team.put("type", "game-team");
			team.put("name", t.getName());
			team.put("players", players);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		this.retVal = team;
	}
	
	@Override
	public String toString() {
		return this.game.toString();
	}

}

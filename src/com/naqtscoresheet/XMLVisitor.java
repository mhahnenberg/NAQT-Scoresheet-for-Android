package com.naqtscoresheet;

/**
 * 
 * @author mhahnenberg
 */
public class XMLVisitor implements Visitor {
	private final StringBuilder sb;
	private boolean winnerPlayer;
	private boolean loserPlayer;
	
	public XMLVisitor() {
		this.sb = new StringBuilder();
		this.winnerPlayer = false;
		this.loserPlayer = false;
	}
	
	@Override
	public void visit(Game g) {
		this.sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		this.sb.append("<game");
		if (g.getGameID() != null) {
			this.sb.append(" id=\"" + g.getGameID() + "\"");
		}
		this.sb.append(">");
		g.visitChildren(this);
		this.sb.append("</game>");
	}

	@Override
	public void visit(Tossup t) {
		this.sb.append("<tossup num=\"" + t.getTossupNum() + "\"");
		if (t.isTiebreaker()) {
			this.sb.append(" tiebreaker=\"true\"");
		}
		this.sb.append(">");
		if (!t.getWinnerTeam().isBogus()) {
			this.sb.append("<team name=\"" + t.getWinnerTeam().getName() + "\" won=\"true\" points=\"" + t.getWinnerPoints() + "\">");
			for (Player p : t.getWinnerPlayers()) {
				if (p.equals(t.getWinnerPlayer())) {
					this.winnerPlayer = true;
				}
				p.accept(this);
				this.winnerPlayer = false;
			}
			this.sb.append("</team>");
		}
		if (!t.getLoserTeam().isBogus()) {
			this.sb.append("<team name=\"" + t.getLoserTeam().getName() + "\" won=\"false\" points=\"" + t.getLoserPoints() + "\">");
			for (Player p : t.getLoserPlayers()) {
				if (p.equals(t.getLoserPlayer())) {
					this.loserPlayer = true;
				}
				p.accept(this);
				this.loserPlayer = false;
			}
			this.sb.append("</team>");
		}
		this.sb.append("</tossup>");
	}

	@Override
	public void visit(Bonus b) {
		this.sb.append("<bonus points=\"" + b.getPoints() + "\">");
		b.visitChildren(this);
		this.sb.append("</bonus>");
	}

	@Override
	public void visit(Player p) {
		this.sb.append("<player name=\"" + p.getName() + "\"");
		if (this.winnerPlayer) {
			this.sb.append(" winner=\"true\"");
		}
		else if (this.loserPlayer) {
			this.sb.append(" loser=\"true\"");
		}
		this.sb.append(">");
		p.visitChildren(this);
		this.sb.append("</player>");
	}

	@Override
	public void visit(Team t) {
		this.sb.append("<team name=\"" + t.getName() + "\" score=\"" + t.getScore() + "\">");
		t.visitChildren(this);
		this.sb.append("</team>");
	}

	@Override
	public String toString() {
		return this.sb.toString();
	}
}

package com.naqtscoresheet;

public interface Visitor {
	public void visit(Game g);
	public void visit(Tossup t);
	public void visit(Bonus b);
	public void visit(Player p);
	public void visit(Team t);
}

package com.naqtscoresheet;

public interface Visitable {
	public void accept(Visitor v);
	public void visitChildren(Visitor v);
}

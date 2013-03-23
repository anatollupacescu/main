package net.parser;

import java.util.Iterator;

public abstract class Parser {
	
	protected Parser delegate;
	
	public Parser getDelegate() {
		return delegate;
	}

	public void setDelegate(Parser delegate) {
		this.delegate = delegate;
	}

	public abstract boolean parse(Iterator<Character> iterator);
}

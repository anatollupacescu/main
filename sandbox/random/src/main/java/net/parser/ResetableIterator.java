package net.parser;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ResetableIterator implements Iterator<Character> {

	private final CharSequence sequence;
	private int index = 0;
	
	public ResetableIterator(CharSequence seq) {
		this.sequence = seq;
	}
	
	@Override
	public boolean hasNext() {
		return index < sequence.length();
	}

	@Override
	public Character next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return sequence.charAt(index++);
	}

	int freezed = -1;
	
	public void reset() {
		if(freezed > 0) {
			index = freezed;
		}
	}
	
	public void freeze() {
		freezed = index;
	}
	
	public void forget() {
		freezed = -1;
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();		
	}
	
	@Override
	public String toString() {
		return (String)sequence.subSequence(index, sequence.length());
	}
}

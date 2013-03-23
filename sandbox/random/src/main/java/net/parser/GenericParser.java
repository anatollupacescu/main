package net.parser;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenericParser {

	private final static Logger logger = Logger.getAnonymousLogger();

	private GenericParser delegate;

	protected boolean parse(Iterator<Character> iterator) {
		if (delegate != null) {
			logger.log(Level.INFO, "Going to next delegate: {0}", new Object[] { delegate });
			return delegate.parse(iterator);
		} else {
			logger.log(Level.INFO, "this was the last delegate in this chain, returning true");
		}
		return true;
	}

	protected void sanitizeIterator(Iterator<Character> iterator) {
		if (!iterator.hasNext()) {
			throw new IllegalArgumentException("Unexpected end of content");
		}
	}

	public void setDelegate(GenericParser delegate) {
		this.delegate = delegate;
	}
}

package net.parser;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ParserUtils {

	public static Iterator<Character> stringIterator(final String string) {
		  if (string == null)
		    throw new NullPointerException();

		  return new Iterator<Character>() {
		    private int index = 0;

		    public boolean hasNext() {
		      return index < string.length();
		    }

		    public Character next() {
		      /*
		       * Throw NoSuchElementException as defined by the Iterator contract,
		       * not IndexOutOfBoundsException.
		       */
		      if (!hasNext())
		        throw new NoSuchElementException();
		      return string.charAt(index++);
		    }

		    public void remove() {
		      throw new UnsupportedOperationException();
		    }
		  };
		}
}

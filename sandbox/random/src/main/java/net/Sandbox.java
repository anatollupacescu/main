package net;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Sandbox {

	private final static Logger log = Logger.getAnonymousLogger();

	public static void main(String[] args) {
		log.log(Level.INFO, "Response is {0}", new Object[] { true });
	}
}

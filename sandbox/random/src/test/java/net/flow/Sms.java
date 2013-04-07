package net.flow;

public class Sms {
	
	private final String text;

	public Sms(String s) {
		text = s;
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return "[text: " + text + "]";
	}
}
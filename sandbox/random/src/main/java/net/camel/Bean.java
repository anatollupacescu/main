package net.camel;

public class Bean {

	public void go(Integer input) {
		System.out.println("received " + input);
		input++;
		System.out.println("made " + input);
	}
}

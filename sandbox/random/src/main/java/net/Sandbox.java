package net;

import net.parser2.DummyParser;

public class Sandbox {

	public static void main(String[] args) {
		DummyParser p1 = DummyParser.builder().name("jora").one('W').many().one('Q').build();
		p1.parse("WazQ");
	}
}

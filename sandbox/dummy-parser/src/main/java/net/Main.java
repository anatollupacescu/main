package net;

import net.parser.NamedParser;

public class Main {

	//Subscribers(guid=124)/accounts
	private static final String ALPHA_NUMERIC = "jora134";

	public static void main(String[] args) {
		NamedParser.newBuilder("odataUri")
			.one("/")
			.one(NamedParser.newBuilder("entitySet").one("Subscribers", "Accounts").build())
			.zeroOrOne("/")
			.zeroOrOne(NamedParser.newBuilder("entitySet").one("Subscribers", "Accounts").build())
			.oneOrMore(NamedParser.newBuilder().any(ALPHA_NUMERIC).build());
	}
}

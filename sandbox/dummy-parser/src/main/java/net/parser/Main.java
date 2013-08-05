package net.parser;

public class Main {

	//Subscribers(guid=124)/accounts
	private static final String ALPHA_NUMERIC = "jora134";

	public static void main(String[] args) {
		Parser.newParser("odataUri")
			.one("/")
			.one(Parser.newParser("entitySet").one("Subscribers", "Accounts"))
			.zeroOrOne("/")
			.zeroOrOne(Parser.newParser("entitySet").one("Subscribers", "Accounts"))
			.oneOrMore(Parser.newParser().any(ALPHA_NUMERIC));
	}
}

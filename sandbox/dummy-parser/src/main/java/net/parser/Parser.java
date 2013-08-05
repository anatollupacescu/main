package net.parser;

public class Parser {

	private String name;

	public Parser(String string) {
		this.name = string;
	}

	public static Parser newParser(String string) {
		return new Parser(string);
	}

	public static Parser newParser() {
		return new Parser(null);
	}
	
	public String getName() {
		return name;
	}

	public Parser zeroOrOne(String string) {
		// TODO Auto-generated method stub
		return this;
	}

	public Parser zeroOrOne(Parser one) {
		return this;
	}

	public Parser one(String... string) {
		return this;
	}

	public Parser one(Parser one) {
		return this;
	}
	
	public Parser any(String alphaNumeric) {
		return this;
	}
	
	public Parser any(Parser parser) {
		return this;
	}

	public Parser oneOrMore(String any) {
		return this;
	}
	
	public Parser oneOrMore(Parser any) {
		return this;
	}
}

package net.parser2;

public class ParserTest {

	public static void main(String[] args) {
		Parser p = Parser.builder().name("vasea").one('/').maybe('\'').many('a').maybe('\'').build();
		Parser p1 = Parser.builder().name("jora").one('W').one(p).one('Q').build();
		p1.parse(new CharIterator("W/seaQ"));
	}
}

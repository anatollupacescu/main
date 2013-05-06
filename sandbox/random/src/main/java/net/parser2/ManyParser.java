package net.parser2;


public class ManyParser implements IParser {

	private final IParser parser;

	public ManyParser(char ch) {
		this.parser = new SingleParser(ch);
	}

	public ManyParser(Parser parser) {
		this.parser = parser;
	}

	public ManyParser(String string) {
		this.parser = new MultiParser(string);
	}

	@Override
	public boolean parse(CharIterator i) {
		int index = i.getIndex();
		while (parser.parse(i)) {
			index = i.getIndex();
		}
		i.reset(index);
		return true;
	}

	@Override
	public String toString() {
		return "ManyParser: " + parser.toString();
	}

}

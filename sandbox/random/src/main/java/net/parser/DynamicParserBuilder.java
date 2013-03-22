package net.parser;

public class DynamicParserBuilder {
	
	private Parser start;
	private Parser body;
	private Parser end;
	
	public DynamicParserBuilder start(Predefined pre) {
		this.start = pre.parser;
		return this;
	}
	
	public DynamicParserBuilder start(String string) {
		this.start = new StringParser(string);
		return this;
	}
	
	public DynamicParserBuilder start(String ... string) {
		this.start = new AnyParser(string);
		return this;
	}
	
	public DynamicParserBuilder start(Parser parser) {
		this.start = parser;
		return this;
	}
	
	public DynamicParserBuilder start(Parser ... parser) {
		this.start = new AnyParser(parser);
		return this;
	}

	/*body*/
	public DynamicParserBuilder body(String string) {
		this.body = new StringParser(string);
		return this;
	}

	public DynamicParserBuilder body(Parser parser) {
		this.body = parser;
		return this;
	}
	
	public DynamicParserBuilder any(String ... string) {
		this.body = new AnyParser(string);
		return this;
	}
	
	public DynamicParserBuilder any(Parser ... parser) {
		this.body = new AnyParser(parser);
		return this;
	}
	
	public DynamicParserBuilder many(String string) {
		this.body = new ManyParser(new StringParser(string));
		return this;
	}
	
	public DynamicParserBuilder many(Predefined pre) {
		this.body = new ManyParser(pre.parser);
		return this;
	}

	public DynamicParserBuilder many(Parser parser) {
		this.body = new ManyParser(parser);
		return this;
	}
	
	/*end*/
	public DynamicParserBuilder end(String string) {
		this.end = new StringParser(string);
		return this;
	}

	public DynamicParserBuilder end(Predefined pre) {
		this.end = pre.parser;
		return this;
	}
	
	public DynamicParserBuilder end(String ... string) {
		this.end = new AnyParser(string);
		return this;
	}
	
	public DynamicParserBuilder end(Parser parser) {
		this.end = parser;
		return this;
	}
	
	public DynamicParserBuilder end(Parser ... parser) {
		this.end = new AnyParser(parser);
		return this;
	}

	/*getters*/
	Parser getStart() {
		return start;
	}
	
	Parser getBody() {
		return body;
	}
	
	Parser getEnd() {
		return end;
	}
	
	public DynamicParser build() {
		return new DynamicParser(this);
	}
}

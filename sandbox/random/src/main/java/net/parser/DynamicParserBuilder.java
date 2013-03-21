package net.parser;

public class DynamicParserBuilder {
	
	private Parser start;
	private Parser body;
	private Parser end;
	
	public DynamicParserBuilder start(Predefined pre) {
		this.start = new PredefinedParser(pre);
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
	
	public DynamicParserBuilder start(DynamicParser parser) {
		this.start = parser;
		return this;
	}
	
	public DynamicParserBuilder start(DynamicParser ... parser) {
		this.start = new AnyParser(parser);
		return this;
	}

	/*body*/
	public DynamicParserBuilder body(String string) {
		this.body = new StringParser(string);
		return this;
	}

	public DynamicParserBuilder body(DynamicParser parser) {
		this.body = parser;
		return this;
	}
	
	public DynamicParserBuilder any(String ... string) {
		this.body = new AnyParser(string);
		return this;
	}
	
	public DynamicParserBuilder any(DynamicParser ... parser) {
		this.body = new AnyParser(parser);
		return this;
	}
	
	public DynamicParserBuilder many(String string) {
		this.body = new ManyParser(new StringParser(string));
		return this;
	}
	
	public DynamicParserBuilder many(Predefined pre) {
		this.body = new ManyParser(new PredefinedParser(pre));
		return this;
	}

	public DynamicParserBuilder many(DynamicParser parser) {
		this.body = new ManyParser(parser);
		return null;
	}
	
	/*end*/
	public DynamicParserBuilder end(String string) {
		this.end = new StringParser(string);
		return this;
	}

	public DynamicParserBuilder end(Predefined pre) {
		this.end = new PredefinedParser(pre);
		return this;
	}
	
	public DynamicParserBuilder end(String ... string) {
		this.end = new AnyParser(string);
		return this;
	}
	
	public DynamicParserBuilder end(DynamicParser parser) {
		this.end = parser;
		return this;
	}
	
	public DynamicParserBuilder end(DynamicParser ... parser) {
		this.end = new AnyParser(parser);
		return this;
	}

	/*getters*/
	public Parser getStart() {
		return start;
	}
	
	public Parser getBody() {
		return body;
	}
	
	public Parser getEnd() {
		return end;
	}
	
	public DynamicParser build() {
		return new DynamicParser(this);
	}
}

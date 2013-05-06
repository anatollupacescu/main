package net.parser2;

import com.google.common.collect.ImmutableSet;

public class AnyParser implements IParser {

	private final ImmutableSet<IParser> parsers;
	
	public AnyParser(char[] chars) {
		ImmutableSet.Builder<IParser> builder = new ImmutableSet.Builder<IParser>();
		for(char ch : chars) {
			IParser parser = new SingleParser(new CharPredicate(ch));
			builder.add(parser);
		}
		this.parsers = builder.build();
	}
	
	public AnyParser(Parser[] parsers) {
		ImmutableSet.Builder<IParser> builder = new ImmutableSet.Builder<IParser>();
		for(IParser parser : parsers) {
			builder.add(parser);
		}
		this.parsers = builder.build();
	}

	public AnyParser(String[] strings) {
		ImmutableSet.Builder<IParser> builder = new ImmutableSet.Builder<IParser>();
		for(String string : strings) {
			builder.add(new MultiParser(string));
		}
		this.parsers = builder.build();
	}

	@Override
	public boolean parse(CharIterator i) {
		boolean result = false;
		int index = 0;
		for (IParser p : parsers) {
			index = i.getIndex();
			if (p.parse(i)) {
				result = true;
				break;
			}
			i.reset(index);
		}
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (IParser p : parsers) {
			sb.append(p.toString());
		}
		return sb.toString();
	}

}

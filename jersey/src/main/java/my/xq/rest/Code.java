package my.xq.rest;

public final class Code {

	private final String code;
	
	public Code(String message) {
		code = message;
	}
	
	@Override
	public String toString() {
		return "<code>" + code + "</code>";
	}
}

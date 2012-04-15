package core.model;

public class Message {

	private String state;

	public Message(String s) {
		state = s;
	}
	
	public String getState() {
		return state;
	}

	public void setState(String s) {
		state = s;
	}
}

package core.model.message;

import net.trivial.wf.iface.Message;

public class XMLMessage extends Message {

	public XMLMessage(String s) {
		super(s);
	}

	private String text;
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}

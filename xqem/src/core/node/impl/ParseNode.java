package core.node.impl;

import net.trivial.wf.iface.Action;
import net.trivial.wf.iface.Message;
import core.model.message.XMLMessage;
import core.node.Node;

public class ParseNode extends Node implements Action{
	
	public ParseNode(String ... args) {
		super(args);
	}

	@Override
	public String execute(Message obj, Object... arg1) {

		if (obj == null)
			return error;

		String message = ((XMLMessage) obj).getText();

		if (message == null)
			return error;

		try {
			String parsedMessage = executeQuery(message);
			((XMLMessage) obj).setText(parsedMessage);
			return success;
		} catch (Exception e) {
			e.printStackTrace();
			return error;
		}
	}
}

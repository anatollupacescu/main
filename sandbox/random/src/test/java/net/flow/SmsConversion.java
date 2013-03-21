package net.flow;

import net.flow.Message;
import net.flow.Message.Resolution;

import com.google.common.base.Function;

public class SmsConversion implements Function<Message<Sms>, Message<Sms>> {

	public Message<Sms> apply(Message<Sms> incoming) {
		Sms incomingSms = incoming.getPayload();
		String text = incomingSms.getText();
		if(text == null || text.isEmpty()) {
			return new Message<Sms>(null, null, Resolution.FAIL);
		}
		Sms sms = new Sms("received: " + text);
		return new Message<Sms>(sms, null, Resolution.DONE);
	}
}

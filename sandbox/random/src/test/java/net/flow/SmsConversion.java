package net.flow;

import com.google.common.base.Function;

public class SmsConversion implements Function<Sms, Sms> {

	@Override
	public Sms apply(Sms incomingSms) {
		String text = incomingSms.getText();
		if(text == null || text.isEmpty()) {
			return null;
		}
		return new Sms("received: " + text);
	}
}

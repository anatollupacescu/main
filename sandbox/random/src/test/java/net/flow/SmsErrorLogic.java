package net.flow;

import com.google.common.base.Function;

public class SmsErrorLogic implements Function<Sms, Sms> {

	@Override
	public Sms apply(Sms incomingSms) {
		throw new RuntimeException("All bad");
	}
}

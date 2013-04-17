package net.flow;

import com.google.common.base.Function;

public class SmsEvaluator implements Function<Sms, String> {

	@Override
	public String apply(Sms arg0) {
		return arg0.getText().equals("toFail") ? "FAIL" : "SUCCESS";
	}
}

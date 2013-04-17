package net.flow;

import java.util.logging.Logger;

import org.junit.Test;

public class SmsTest {

	private final static Logger log = Logger.getAnonymousLogger();
	private static final String SMS_TEXT = "toFail";
	
	@Test
	public void test1() {
		SmsConversion logic = new SmsConversion();
		SmsErrorLogic error = new SmsErrorLogic();
		SmsSuccessLogic success = new SmsSuccessLogic();
		
		SmsEvaluator evaluator = new SmsEvaluator();
		
		FlowState<Sms> start = new FlowState<Sms>("start", logic, evaluator);
		FlowState<Sms> errorState = new FlowState<Sms>("FAIL", error);
		FlowState<Sms> successState = new FlowState<Sms>("SUCCESS", success);
		Flow<Sms> flow = Flow.<Sms>newBuilder().add(start).add(errorState).add(successState).build();
		flow.execute("start", new Sms(SMS_TEXT));
	}
}

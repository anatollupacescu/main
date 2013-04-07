package net.flow;

import static org.junit.Assert.assertEquals;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

public class SmsTest {

	private final static Logger log = Logger.getAnonymousLogger();
	private static final String SMS_TEXT = "privet";
	
	@Test
	public void test1() {
		SmsConversion logic = new SmsConversion();
		SmsEvaluator evaluator = new SmsEvaluator();
		
		FlowState<Sms> start = new FlowState<Sms>("start", logic, evaluator);
		Flow<Sms> flow = Flow.<Sms>newBuilder().add(start).build();
		Sms sms2 = flow.execute(new Sms(SMS_TEXT));
		
		log.log(Level.INFO, "Incoming sms {0}", sms2);
		assertEquals(sms2.getText(), "received: " + SMS_TEXT);
	}
}

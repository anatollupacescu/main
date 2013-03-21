package net.flow;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import org.junit.Test;

public class SmsTest {

	private final static Logger log = Logger.getAnonymousLogger();
	private static final String SMS_TEXT = "privet";
	
	@Test
	public void test() {
		FlowStateBuilder<Sms> startBuilder = Flow.<Sms>newState("Start");
		FlowState<Sms> end = Flow.<Sms>newState("End").build();
		FlowState<Sms> start = startBuilder.transition("end", end, new SmsConversion()).build();
		Flow<Sms> flow = new Flow<Sms>("myFlow", start);
		Sms sms = new Sms(SMS_TEXT);
		Message<Sms> message = new Message<Sms>(sms, "end");
		Sms sms2 = flow.execute(message);
		log.log(Level.INFO, "Incoming sms {0}", sms2);
		assertEquals(sms2.getText(), "received: " + SMS_TEXT);
	}
}

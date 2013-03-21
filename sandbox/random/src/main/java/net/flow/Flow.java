package net.flow;

import net.flow.Message.Resolution;

import com.google.common.base.Function;


public class Flow<A> {
	
	private final String name;
	private final FlowState<A> firstState;
	
	public Flow(String string, FlowState<A> start) {
		this.name = string;
		this.firstState = start;
	}

	public static <A>FlowStateBuilder<A> newState(String name) {
		return new FlowStateBuilder<A>(name);
	}
	
	public A execute(final Message<A> message) {
		Message<A> response = message;
		FlowState<A> state = firstState;
		String code = response.getTransactionCode();
		while(Resolution.SUCCESS.equals(response.getResolution())) {
			FlowState<A> nextState = state.getStateForCode(code);
			if(nextState == null) {
				throw new RuntimeException("Could not find any associate states to code " + code);
			}
			Function<Message<A>, Message<A>> func = state.getFunctionForState(nextState);
			response = func.apply(response);
			state = nextState;
		}
		return response.getPayload();
	}
	
	public String getName() {
		return name;
	}
}

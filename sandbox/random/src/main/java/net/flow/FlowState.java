package net.flow;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

public class FlowState<A> {

	private final String name;
	private final ImmutableMap<String, FlowState<A>> codeDest;		
	private final ImmutableMap<FlowState<A>, Function<Message<A>, Message<A>>> destFunc;
	
	public FlowState(final FlowStateBuilder<A> builder, 
			ImmutableMap<String, FlowState<A>> codeDest,
			ImmutableMap<FlowState<A>, Function<Message<A>, Message<A>>> destFunc) {
		this.name = builder.getName();
		this.codeDest = codeDest;
		this.destFunc = destFunc;
	}
	
	public FlowState<A> getStateForCode(String code) {
		return codeDest.get(code);
	}
	
	public Function<Message<A>, Message<A>> getFunctionForState(FlowState<A> state) {
		return destFunc.get(state);
	}
	
	public String getName() {
		return name;
	}
}

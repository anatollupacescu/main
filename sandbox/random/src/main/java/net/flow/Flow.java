package net.flow;

import com.google.common.collect.ImmutableMap;

public class Flow<A> {
	
	private final ImmutableMap<String, FlowState<A>> states;	
	
	public Flow(ImmutableMap<String, FlowState<A>> immutableMap) {
		states = immutableMap;
	}

	public static <A>FlowBuilder<A> newBuilder() {
		return new FlowBuilder<A>();
	}
	
	public void execute(final String startingPoint, final A message) {
		String stateName = startingPoint;
		while(stateName != null) {
			FlowState<A> state = states.get(stateName);
			stateName = state.execute(message);
		}
	}
	
	public static final class FlowBuilder<A> {
		
		private final ImmutableMap.Builder<String, FlowState<A>> states = new ImmutableMap.Builder<String, FlowState<A>>(); 

		public FlowBuilder<A> add(FlowState<A> start) {
			states.put(start.getName(), start);
			return this;
		}
		
		public Flow<A> build() {
			return new Flow<A>(states.build());
		}
	}
}

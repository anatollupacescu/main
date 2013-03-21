package net.flow;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

public class FlowStateBuilder<A> {

	private final String name;
	private final ImmutableMap.Builder<String, FlowState<A>> codeDestBuilder = new ImmutableMap.Builder<String, FlowState<A>>();
	private final ImmutableMap.Builder<FlowState<A>, Function<Message<A>, Message<A>>> destFuncBuilder = new ImmutableMap.Builder<FlowState<A>, Function<Message<A>, Message<A>>>();

	public FlowStateBuilder(String s) {
		name = s;
	}

	public FlowState<A> build() {
		return new FlowState<A>(this, codeDestBuilder.build(), destFuncBuilder.build());
	}

	public FlowStateBuilder<A> transition(String code, FlowState<A> dest, Function<Message<A>, Message<A>> func) {
		codeDestBuilder.put(code, dest);
		destFuncBuilder.put(dest, func);
		return this;
	}

	public String getName() {
		return name;
	}
}

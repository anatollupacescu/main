package net.flow;

import com.google.common.base.Function;

public class FlowState<A> {

	protected final String name;
	protected final Function<A, A> logic;
	protected final Function<A, String> evaluator;
	
	public FlowState(final String name, final Function<A, A> logic, final Function<A, String> evaluator) {
		this.name = name;
		this.logic = logic;
		this.evaluator = evaluator;
	}
	
	public String execute(A input) {
		A output = logic.apply(input);
		return evaluator.apply(output);
	}
	
	public String getName() {
		return name;
	}
}

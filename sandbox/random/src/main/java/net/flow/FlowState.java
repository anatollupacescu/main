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
	
	public FlowState(String name, final Function<A, A> logic) {
		this.name = name;
		this.logic = logic;
		this.evaluator = null;
	}

	public String execute(A input) {
		A output = logic.apply(input);
		if(evaluator == null) {
			return null;
		}
		return evaluator.apply(output);
	}
	
	public String getName() {
		return name;
	}
}

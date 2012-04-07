package net.trivial.wf;

import java.util.HashMap;
import java.util.Map;

public class StateMachine <S, T> {
	
	private Map<S, Map<T, S>> states = new HashMap<S, Map<T, S>>();
	
	public void put(S startState, T key, S endState) {
		
		Map<T, S> state = states.get(startState);
		
		if(state == null) {
			state = new HashMap<T, S>();
		}
		
		state.put(key, endState);
		
		states.put(startState, state);
		
	}
	
	public S get(S _state, T key) {
		
		Map<T, S> state = states.get(_state);
		
		if(state == null) return null;
		
		return state.get(key); 
		
	}
	
}
package net.map;

public class Transformation {

	final Operation operation;
	final String[] args;
	
	public Transformation(Operation operation, String[] args) {
		this.operation = (operation == null) ? Operation.copy : operation;
		this.args = args;
	}
}

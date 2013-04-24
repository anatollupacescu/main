package net.map;

public class Transformation {

	final Operation operation;
	final Type type;
	final String[] args;
	
	public Transformation(Operation operation, Type type, String[] args) {
		this.operation = (operation == null) ? Operation.copy : operation;
		this.type = type;
		this.args = args;
	}
}

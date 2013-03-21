package net.flow;

public class Message<T> {

	public enum Resolution {
		FAIL,
		SUCCESS, 
		DONE
	}

	private final T payload;
	private final Resolution resolution;
	private final String transactionCode;
	
	public Message(T payload, String transactionCode) {
		this.payload = payload;
		this.resolution = Resolution.SUCCESS;
		this.transactionCode = transactionCode;
	}
	
	public Message(T payload, String transactionCode, Resolution s) {
		this.payload = payload;
		this.resolution = s;
		this.transactionCode = transactionCode;
	}

	public T getPayload() {
		return payload;
	}

	public Resolution getResolution() {
		return resolution;
	}

	public String getTransactionCode() {
		return transactionCode;
	}
}	

package net.disruptor;

import com.lmax.disruptor.EventFactory;

class LogEntry {
	
	private String text;

	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public static final EventFactory<LogEntry> FACTORY = new EventFactory<LogEntry>() {
		public LogEntry newInstance() {
			return new LogEntry();
		}
	};
}

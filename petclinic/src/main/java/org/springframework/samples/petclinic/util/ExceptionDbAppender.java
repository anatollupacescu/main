package org.springframework.samples.petclinic.util;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.model.ExceptionLog;
import org.springframework.samples.petclinic.repository.ExceptionRepository;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

public class ExceptionDbAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

	private ExceptionRepository repository;

	@Override
	protected void append(ILoggingEvent eventObject) {
		if(eventObject.getLevel() != Level.ERROR) {
			return;
		}
		final String mesaj = eventObject.getFormattedMessage();
		final String parametru = null;
		final Exception exceptia = null;
		ExceptionLog ex = new ExceptionLog(mesaj, parametru, exceptia);
		repository.log(ex);
	}

	public void setRepository(ExceptionRepository repo) {
		this.repository = repo;
		initAppender();

	}

	private void initAppender() {
		final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		final Logger rootLogger = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		this.setContext(lc);
		this.setName("EXCEPTION_DB");
		rootLogger.setLevel(Level.DEBUG);
		rootLogger.addAppender(this);
		this.start();
	}
}

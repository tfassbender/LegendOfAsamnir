package net.jfabricationgames.gdx.desktop.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.ApplicationLogger;

public class LogAdapter implements ApplicationLogger {
	
	private Logger logger = LoggerFactory.getLogger(LogAdapter.class);
	
	@Override
	public void log(String tag, String message) {
		logger.info("[{}] {}", tag, message);
	}
	
	@Override
	public void log(String tag, String message, Throwable exception) {
		logger.info("[{}] {}", tag, message, exception);
	}
	
	@Override
	public void error(String tag, String message) {
		logger.error("[{}] {}", tag, message);
	}
	
	@Override
	public void error(String tag, String message, Throwable exception) {
		logger.error("[{}] {}", tag, message, exception);
	}
	
	@Override
	public void debug(String tag, String message) {
		logger.debug("[{}] {}", tag, message);
	}
	
	@Override
	public void debug(String tag, String message, Throwable exception) {
		logger.debug("[{}] {}", tag, message, exception);
	}
}

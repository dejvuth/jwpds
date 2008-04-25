package de.tum.in.wpds;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A utility class.
 * 
 * @author suwimont
 *
 */
public class Utils {
	
	/**
	 * Gets a logger for the class specified by <code>c</code>.
	 * The logger will reside in the machine-dependent temporary folder.
	 * 
	 * @param c the class.
	 * @return the logger.
	 */
	public static Logger getLogger(Class<? extends Object> c) {
		return getLogger(c, "%t/" + c.getSimpleName() + "%g.log");
	}

	/**
	 * Gets a logger for the class specified by <code>c</code>.
	 * 
	 * @param c the class.
	 * @param pattern the pattern.
	 * @return the logger.
	 */
	public static Logger getLogger(Class<? extends Object> c, String pattern) {
		
		// Creates a log handler
		Handler handler;
		try {
			handler = new FileHandler(pattern);
		} catch (IOException e) {
			return null;
		}
		handler.setFormatter(new VerySimpleFormatter());
		
		// Creates logger
		Logger logger = Logger.getLogger(c.getName());
		logger.addHandler(handler);
		logger.setLevel(Level.ALL);
		
		return logger;
	}
	
	/**
	 * A very simple log formatter.
	 * 
	 * @author suwimont
	 *
	 */
	static class VerySimpleFormatter extends Formatter {
		
		@Override
		public String format(LogRecord record) {
			return record.getMessage();
		}
	}
	
	/**
	 * Appends the StringBuilder <code>b</code> with the format string 
	 * <code>s</code>, where <code>o</code> are the arguments of the
	 * format string.
	 * 
	 * @param b the StringBuilder.
	 * @param s the format string.
	 * @param o the arguments.
	 */
	public static void append(StringBuilder b, String s, Object... o) {
		b.append(String.format(s, o));
	}
}

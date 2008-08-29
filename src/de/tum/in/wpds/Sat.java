package de.tum.in.wpds;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An abstract class for saturation procedures.
 * 
 * @author suwimont
 *
 */
public abstract class Sat {

	/**
	 * The listener.
	 */
	private SatListener listener;
	
	/**
	 * The cancel monitor.
	 */
	protected CancelMonitor monitor;
	
	/**
	 * Determines whether to log debug information.
	 */
	private static int DEBUG = 0;
	
	/**
	 * The logger.
	 */
	public static Logger logger = Utils.getLogger(Sat.class);

	/**
	 * Sets the listener.
	 * 
	 * @param listener the listener.
	 */
	public void setListener(SatListener listener) {
		this.listener = listener;
	}

	/**
	 * Notifies the listener that the label is reached.
	 * 
	 * @param label the label.
	 */
	protected void updateListener(String label) {
		if (listener == null) return;
		listener.reach(label);
	}

	/**
	 * Computes post* of the given fa.
	 * 
	 * @param fa the initial automataon.
	 * @param monitor the monitor.
	 * @return the saturated automaton.
	 */
	public abstract Object poststar(Fa fa, CancelMonitor monitor);

	/**
	 * Computes post* of the given fa. The default monitor is used.
	 * 
	 * @param fa the initial automataon.
	 * @return the saturated automaton.
	 */
	public Object poststar(Fa fa) {
		return poststar(fa, new DefaultMonitor());
	}
	
	/**
	 * Sets the verbosity level.
	 * 
	 * @param level the verbosity level.
	 */
	public static void setVerbosity(int level) {
		DEBUG = level;
		if (level >= 2)
			logger.setLevel(Level.ALL);
		else if (level == 1)
			logger.setLevel(Level.INFO);
		else
			logger.setLevel(Level.SEVERE);
		
		
//		// Level 3: prints everything
//		if (level >= 3) {
//			DEBUG = 3;
//			logger.setLevel(Level.ALL);
//		}
//		
//		else if (level >= 2) {
//			DEBUG = 2;
//			logger.setLevel(Level.ALL);
//		}
//		
//		// Level 1: prints very little
//		else if (level == 1) {
//			DEBUG = 1;
//			logger.setLevel(Level.INFO);
//		}
//		
//		// Level 0: prints nothing
//		else if (level == 0) { logger.setLevel(Level.SEVERE);
//		
//		// Default level 2: prints progresses without BDDs.
	}
	
	public static boolean all() {
		return DEBUG >= 3;
	}
	
	public static boolean debug() {
		return DEBUG >=2;
	}
	
	public static void info(String msg, Object... args) {
		if (logger.getLevel().equals(Level.INFO))
			logger.info(String.format(msg, args));
	}

	/**
	 * Logs the message <code>msg</code>.
	 * 
	 * @param msg A format string as described in Format string syntax.
	 * @param args Arguments referenced by the format specifiers in the format string.
	 */
	public static void log(String msg, Object... args) {
		logger.fine(String.format(msg, args));
	}
}
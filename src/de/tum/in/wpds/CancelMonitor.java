package de.tum.in.wpds;

/**
 * A monitor that controls whether an analysis is canceled.
 * 
 * @author suwimont
 *
 */
public interface CancelMonitor {

	/**
	 * Sets cancel to <code>value</code>.
	 * 
	 * @param value the cancel value.
	 */
	public void setCanceled(boolean value);
	
	/**
	 * Returns <code>true</code> if canceled.
	 * 
	 * @return <code>true</code> if canceled.
	 */
	public boolean isCanceled();
	
	/**
	 * Tells the monitor about the task <code>name</code>.
	 * 
	 * @param name the task name.
	 */
	public void subTask(String name);
}

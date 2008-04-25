package de.tum.in.wpds;

/**
 * The sat listener.
 * 
 * @author suwimont
 *
 */
public interface SatListener {

	/**
	 * Notifies that <code>label</code> is reached.
	 * 
	 * @param label the label.
	 */
	public void reach(String label);
}

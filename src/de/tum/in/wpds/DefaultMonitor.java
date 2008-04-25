package de.tum.in.wpds;

public class DefaultMonitor implements CancelMonitor {

	private boolean canceled = false;
	
	public void setCanceled(boolean value) {
		
		canceled = value;
	}
	
	public boolean isCanceled() {
		
		return canceled;
	}

	public void subTask(String name) {
	}
}

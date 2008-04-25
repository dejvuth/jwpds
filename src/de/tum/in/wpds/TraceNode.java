package de.tum.in.wpds;

public class TraceNode {

	public Transition t;
	public Semiring d;
	public Rule r;
	
	public TraceNode(Transition t, Semiring d, Rule r) {
		
		this.t = t;
		this.d = d;
		this.r = r;
	}
	
	public String toString() {
		
		if (r == null)
			return String.format("%s (%s)", t, d);
		
		return String.format("\t%s%n%s (%s)", r, t, d);
	}
}

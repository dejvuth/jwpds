package de.tum.in.wpds;

/**
 * Finite automaton's transition.
 * 
 * @author suwimont
 *
 */
public class Transition {

	String p;
	String a;
	String q;
	
	public Transition() {
		
	}
	
	public Transition(String p, String a, String q) {
		
		this.p = p;
		this.a = a;
		this.q = q;
	}
	
	public String getToState() {
		return q;
	}
	
	public Object clone() {
		return new Transition(new String(p), new String(a), new String(q));
	}
	
	public boolean equals(Object o) {
		
		if (o == null) return false;
		
		if (!(o instanceof Transition)) return false;
		
		Transition t = (Transition) o;
		return p.equals(t.p) && a.equals(t.a) && q.equals(t.q);
	}
	
	public int hashCode() {
		
		int result = p.hashCode();
		result ^= a.hashCode();
		result ^= q.hashCode();
		
		return result;
	}
	
	public String toString() {
		
		return String.format("%s -%s-> %s", p, a, q);
	}
}

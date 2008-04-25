package de.tum.in.wpds;

/**
 * A pushdown configuration.
 * 
 * @author suwimont
 *
 */
public class Config {

	public String p;
	public String[] w;
	
	public Config(String p, String y) {
		
		this (p, new String[] { y });
	}
	
	public Config(String p, String... w) {
		
		this.p = p;
		this.w = w;
	}
	
	public boolean equals(Object o) {
		
		if (!(o instanceof Config))
			return false;
		
		Config c = (Config) o;
		if (p != c.p) return false;
		if (w.length != c.w.length) return false;
		for (int i = 0; i < w.length; i++) {
			if (!w[i].equals(c.w[i])) return false;
		}
		
		return true;
	}
	
	public int hashCode() {
		
		int result = p.hashCode();
		for (int i = 0; i < w.length; i++) {
			result ^= w[i].hashCode();
		}
		
		return result;
	}
	
	public String toString() {
		
		if (w.length == 0)
			return String.format("<%s>", p);
		
		StringBuilder out = new StringBuilder();
		out.append(String.format("<%s,", p));
		for (int i = 0; i < w.length; i++) {
			out.append(" ");
			out.append(w[i]);
		}
		out.append(">");
		
		return out.toString();
	}
}

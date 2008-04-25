package de.tum.in.wpds;

/**
 * A pushdown rule.
 * 
 * @author suwimont
 *
 */
public class Rule {

	/**
	 * The lhs configuration.
	 */
	public Config left;
	
	/**
	 * The rhs configuration.
	 */
	public Config right;
	
	/**
	 * The rhs dynamic configuration (for dynamic rule).
	 */
	public Config dynamic;
	
	/**
	 * The semiring value.
	 */
	public Semiring d;
	
	private boolean global;
	
	/**
	 * Creates a new rule py -&gt; qw (d).
	 * 
	 * @param d the semiring value.
	 * @param p the left-hand-side control location.
	 * @param y the left-hand-side stack symbol.
	 * @param q the right-hand-side control location.
	 * @param w the right-hand-side stack symbol(s).
	 */
	public Rule(Semiring d, String p, String y, String q, String... w) {
		
		this(d, new Config(p, y), new Config(q, w));
	}
	
	/**
	 * Creates a new rule left -&gt; right (d).
	 * 
	 * @param d the semiring value.
	 * @param left the left-hand-side configuration.
	 * @param right the right-hand-side configuration.
	 */
	public Rule(Semiring d, Config left, Config right) {
		
		this(d, left, right, null);
	}
	
	/**
	 * Creates a new dynamic rule left -&gt; right |&rang; dynamic (d).
	 * 
	 * @param d the semiring value.
	 * @param left the left-hand-side configuration.
	 * @param right the right-hand-side configuration.
	 * @param dynamic the right-hand-side dynamic configuration.
	 */
	public Rule(Semiring d, Config left, Config right, Config dynamic) {
		
		this.left = left;
		this.right = right;
		this.dynamic = dynamic;
		this.d = d;
	}
	
	/**
	 * Returns the weight of this rule.
	 * 
	 * @return the weight of this rule.
	 */
	public Semiring getWeight() {
		return d;
	}
	
	/**
	 * Gets the label of this rule. The label is the stack symbol
	 * of the left-hand-side configuration.
	 * 
	 * @return the label of this rule.
	 */
	public String getLabel() {
		return left.w[0];
	}
	
	/**
	 * Gets the right label of this rule.
	 * 
	 * @return the right label of this rule.
	 */
	public String getRightLabel() {
		return right.w[0];
	}
	
	/**
	 * Returns <code>true</code> if this rule is a dynamic rule; or
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> iff this rule is a dynamic rule.
	 */
	public boolean isDynamic() {
		return dynamic != null;
	}
	
	public void setGlobal(boolean global) {
		this.global = global;
	}
	
	public boolean isGlobal() {
		return global;
	}
	
	/**
	 * Returns the string representation of this rule.
	 * 
	 * @return the string representation of this rule.
	 */
	public String toString() {
		
		StringBuilder out = new StringBuilder();
		out.append(String.format("%s -> %s", left, right));
		if (dynamic != null)
			out.append(String.format(" |> %s", dynamic));
		out.append(String.format(" (%s)", d));
		if (isGlobal())
			out.append(" [global]");
		
		return out.toString();
	}
}

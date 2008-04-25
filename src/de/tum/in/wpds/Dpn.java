package de.tum.in.wpds;

import java.util.Set;

/**
 * A dynamic pushdown network.
 * 
 * @author suwimont
 *
 */
public class Dpn extends Pds {

	/**
	 * Adds dynamic rule left -&gt; right |&rang; dynamic (d) to this pds.
	 * 
	 * @param d the semiring value.
	 * @param left the left-hand-side configuration.
	 * @param right the right-hand-side configuration.
	 * @param dynamic the right-hand-side dynamic configuration.
	 */
	public void add(Semiring d, Config left, Config right, Config dynamic) {
		
		add(new Rule(d, left, right, dynamic));
	}
	
	/**
	 * Returns the set of stack symbols of this pds.
	 * 
	 * @return the set of stack symbols.
	 */
	public Set<String> getStackSymbols() {
		
		Set<String> symbols = super.getStackSymbols();
		for (Rule rule : rules) {
			
			// Adds the dynamic rhs
			if (rule.dynamic == null) continue;
			String[] w = rule.dynamic.w;
			if (w == null) continue;
			for (int i = 0; i < w.length; i++)
				symbols.add(w[i]);
		}
		return symbols;
	}
}

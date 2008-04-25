package de.tum.in.wpds;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * A pushdown system.
 * 
 * @author suwimont
 *
 */
public class Pds {

	public HashSet<Rule> rules = new HashSet<Rule>();
	
	HashMap<Config, Set<Rule>> leftMapper;
	
	/**
	 * Adds rule py -&gt; qw (d) to this pds.
	 * 
	 * @param d the semiring value.
	 * @param p the left-hand-side control location.
	 * @param y the left-hand-side stack symbol.
	 * @param q the right-hand-side control location.
	 * @param w the right-hand-side stack symbol(s).
	 */
	public void add(Semiring d, String p, String y, String q, String... w) {
		
		add(new Rule(d, new Config(p, y), new Config(q, w)));
	}
	
	/**
	 * Adds rule <code>r</code> to this pds.
	 * 
	 * @param r the rule.
	 */
	public void add(Rule r) {
		
		rules.add(r);
		
		if (leftMapper != null) {
			addRuleToMapper(leftMapper, r.left, r);
		}
	}
	
	/**
	 * Returns the number of rules of this pds.
	 * 
	 * @return the number of rules of this pds.
	 */
	public int size() {
		return rules.size();
	}
	
	/**
	 * Returns the set of stack symbols of this pds.
	 * 
	 * @return the set of stack symbols.
	 */
	public Set<String> getStackSymbols() {
		
		HashSet<String> symbols = new HashSet<String>(rules.size());
		for (Rule rule : rules) {
			
			// Adds the lhs
			symbols.add(rule.left.w[0]);
			
			// Adds the rhs
			String[] w = rule.right.w;
			if (w == null) continue;
			for (int i = 0; i < w.length; i++)
				symbols.add(w[i]);
		}
		
		return symbols;
	}
	
	private static void addRuleToMapper(HashMap<Config, Set<Rule>> mapper, Config c, Rule r) {
		
		Set<Rule> set = mapper.remove(c);
		if (set == null) set = new HashSet<Rule>();
		set.add(r);
		mapper.put(c, set);
	}
	
	/**
	 * Returns a mapper that maps configs to sets of rules having configs
	 * on the left-hand side.
	 * 
	 * @return the left mapper.
	 */
	public HashMap<Config, Set<Rule>> getLeftMapper() {
		
		if (leftMapper != null) return leftMapper;
		
		leftMapper = new HashMap<Config, Set<Rule>>(rules.size());
		for (Rule rule : rules) {
			
			addRuleToMapper(leftMapper, rule.left, rule);
		}
		
		return leftMapper;
	}
	
	/**
	 * Returns the string representation of this pds.
	 * 
	 * @param the string representation of this pds.
	 */
	public String toString() {
		
		StringBuilder out = new StringBuilder();
		for (Rule rule : rules) {
			out.append(rule);
			out.append("\n");
		}
		return out.toString();
	}
}

package de.tum.in.wpds;

import java.util.Set;

/**
 * A semiring.
 * 
 * @author suwimont
 *
 */
public interface Semiring {
	
	/**
	 * Returns <code>true</code> if this semiring value is zero.
	 * 
	 * @return <code>true</code> if this semiring value is zero.
	 */
	public boolean isZero();
	
	/**
	 * Extends this semiring with <code>a</code>.
	 * 
	 * @param a the semiring to be extended with.
	 * @param monitor the cancel monitor.
	 * @return the extended semiring.
	 */
	public Semiring extend(Semiring a, CancelMonitor monitor);
	
	/**
	 * Extends this semiring with <code>a</code> 
	 * in the case where this semiring belongs to an epsilon transition,
	 * and <code>a</code> belongs to another transition.
	 * 
	 * @param a the semiring to be extended with.
	 * @param monitor the cancel monitor.
	 * @return the extended semiring.
	 */
	public Semiring extendPop(Semiring a, CancelMonitor monitor);
	
	/**
	 * Extends this semiring with <code>a</code> when saturating a push rule.
	 * The result will be a new weight of the first transition.
	 * 
	 * @param a the semiring to be extended with.
	 * @param monitor the cancel monitor.
	 * @return the extended semiring.
	 */
	public Semiring extendPush(Semiring a, CancelMonitor monitor);
	
	/**
	 * Extends this semiring with <code>a</code> when saturating a dynamic rule.
	 * The result will be the new weight of the new automaton.
	 * 
	 * @param a the semiring to be extended with.
	 * @param monitor the cancel monitor.
	 * @return the extended semiring.
	 */
	public Semiring extendDynamic(Semiring a, CancelMonitor monitor);
	
	/**
	 * Combines this semiring with <code>a</code>
	 * 
	 * @param a the semiring to be combined with.
	 * @return the combined semiring.
	 */
	public Semiring combine(Semiring a);
	
	/**
	 * [Eager & Lazy] Lifts this semiring value with <code>a</code>.
	 * 
	 * @param a the semiring to be lifted with.
	 * @return the lifted semiring.
	 */
	public Semiring lift(Semiring a);
	
	/**
	 * [Eager] Restricts this semiring value with <code>a</code>.
	 * 
	 * @param a the semiring to be restricted with.
	 * @return the restrcited semiring.
	 */
	public Semiring restrict(Semiring a);
	
	/**
	 * [Eager] Gets all possible global values from this semiring.
	 * 
	 * @return all possible global values.
	 */
	public Set<Semiring> getGlobals();
	
	/**
	 * [Lazy] Gets the equivalence relation out of this semiring value.
	 * 
	 * @param approach one or two.
	 * @return the equivalence relation.
	 */
	public Semiring getEqRel(int approach);
	
	/**
	 * [Lazy] Gets an equivalence class. 
	 * This semiring must be an equivalence relation.
	 * 
	 * @param approach one or two.
	 * @return an equivalence class.
	 */
	public Semiring getEqClass(int approach);
	
	/**
	 * [Lazy] Gets global values from this semiring.
	 * 
	 * @return global values.
	 */
	public Semiring getGlobal();
	
	/**
	 * [Lazy] Updates this semiring value to the new value specified by <code>a</code>
	 * 
	 * @param a the new semiring value.
	 */
	public void updateGlobal(Semiring a);
	
	/**
	 * [Lazy] Slices this equivalence relation with <code>eqclass</code>.
	 * <code>eqclass</code> is freed.
	 * 
	 * @param eqclass the equivalence class.
	 * @param approach one or two.
	 * @return the sliced equivalence relation.
	 */
	public void sliceWith(Semiring eqclass, int approach);
	
	public Semiring andWith(Semiring a);
	public Semiring orWith(Semiring a);
	
	/**
	 * Copies this semiring.
	 * 
	 * @return a copy of this semiring.
	 */
	public Semiring id();
	
	/**
	 * Frees this semiring.
	 */
	public void free();
	
	/**
	 * Returns a raw string representation of this semiring.
	 * 
	 * @return a raw string representation.
	 */
	public String toRawString();
}

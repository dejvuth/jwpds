package de.tum.in.wpds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Finite automaton.
 * 
 * @author suwimont
 *
 */
public class Fa {

	/**
	 * Maps transition to its semiring value.
	 */
	HashMap<Transition, Semiring> trans = new HashMap<Transition, Semiring>();
	
	/**
	 * Header maps: Maps a state to transitions starting from this state.
	 */
	public HashMap<String, Set<Transition>> hmaps = new HashMap<String, Set<Transition>>();
	
	/**
	 * Epsilon map: Maps a state to epsilon-transitions going to this state.
	 */
	HashMap<String, Set<Transition>> emaps = new HashMap<String, Set<Transition>>();
	
	/**
	 * The epsilon symbol.
	 */
	public static final String epsilon = "epsilon";
	
	/**
	 * The initial state.
	 */
	public static final String q_i = "p";
	
	/**
	 * The final state.
	 */
	public static final String q_f = "s";
	
	/**
	 * Adds a transition <code>t</code> with weight r to this fa.
	 * If the transition already exists, 
	 * the weight r is combined with the existing semiring value.
	 * The method returns <code>true</code> 
	 * if the transition is new or r changes the existing
	 * semiring value of the transition;
	 * otherwise <code>false</code> is returned.
	 * 
	 * @param r the semiring value.
	 * @param t the transition.
	 * @return <code>true</code> if the transition is new or r changes
	 * 			the existing semiring value of the transition; 
	 * 			otherwise <code>false</code> is returned.
	 */
	public boolean add(Semiring r, Transition t) {
		
		boolean changed = false;
		Semiring oldr = trans.remove(t);
		Semiring newr;
		if (oldr == null) {
			Sat.log("\t\tAdding new ");
			newr = r;
			changed = true;
		} else {
			if (oldr.equals(r)) {
				
				newr = oldr;
				Sat.log("\t\tIgnoring ");
			} else {
			
				newr = r.combine(oldr);
				if (!newr.equals(oldr)) {
					changed = true;
					Sat.log("\t\tAdding modified ");
					if (Sat.DEBUG) {
						Sat.log("oldr: %s%n%n", oldr.toRawString());
						Sat.log("\t\tr: %s%n%n\t\t", r.toRawString());
					}
				} else {
					Sat.log("\t\tIgnoring ");
				}
				oldr.free();
			}
		}
		
		Sat.log("%s%n", t);
		if (Sat.DEBUG)
			Sat.log("\t\t%s%n%n", newr.toRawString());
		trans.put(t, newr);
		
		// Update hmaps
		Set<Transition> set = hmaps.get(t.p);
		if (set == null) {
			set = new HashSet<Transition>();
			hmaps.put(t.p, set);
		}
		set.add(t);
		
		// Update emaps
		if (t.a.equals(epsilon)) {
			set = emaps.get(t.q);
			if (set == null) {
				set = new HashSet<Transition>();
				emaps.put(t.q, set);
			}
			set.add(t);
		}
		
		return changed;
	}
	
	/**
	 * Adds a transition p -a-> q with weight r to this fa.
	 * If the transition already exists, 
	 * the weight r is combined with the existing semiring value.
	 * The method returns <code>true</code> 
	 * if the transition is new or r changes the existing
	 * semiring value of the transition;
	 * otherwise <code>false</code> is returned.
	 * 
	 * @param r the semiring value.
	 * @param p the from-state.
	 * @param a the transition's label.
	 * @param q the to state.
	 * @return <code>true</code> if the transition is new or r changes
	 * 			the existing semiring value of the transition; 
	 * 			otherwise <code>false</code> is returned.
	 */
	public boolean add(Semiring r, String p, String a, String q) {
		return add(r, new Transition(p, a, q));
	}
	
	/**
	 * Returns the number of transitions in this automaton.
	 * 
	 * @return the number of transitions.
	 */
	public int size() {
		return trans.size();
	}
	
	/**
	 * Returns the weight of the transition (p,a,q).
	 * 
	 * @param p the from-state.
	 * @param a the transition's label.
	 * @param q the to-state.
	 * @return the weight of the transition.
	 */
	public Semiring getWeight(String p, String a, String q) {
		return getWeight(new Transition(p, a, q));
	}
	
	/**
	 * Returns the weight of the transition <code>t</code>
	 * 
	 * @param t the transition.
	 * @return the weight of the transition.
	 */
	public Semiring getWeight(Transition t) {
		return trans.get(t);
	}
	
	/**
	 * Gets all transitions that start from the initial state.
	 * 
	 * @return all transitions that start from the initial state.
	 */
	public Set<Transition> getInitialTransitions() {
		return hmaps.get(q_i);
	}
	
	/**
	 * Gets all transitions leaving the state <code>q</code>.
	 * 
	 * @param q the state.
	 * @return the set of transitions leaving the state <code>q</code>.
	 */
	public Set<Transition> getTransitions(String q) {
		return hmaps.get(q);
	}
	
	/**
	 * Gets all transitions leaving the state <code>q</code> with symbol <code>a</code>.
	 * An empty set is returned if there is none.
	 * 
	 * @param q
	 * @param a
	 * @return
	 */
	public Set<Transition> getTransitions(String q, String a) {
		
		Set<Transition> trans = new HashSet<Transition>();
		Set<Transition> all = hmaps.get(q);
		if (all == null) return trans;
		
		for (Transition t : all) {
			if (t.a.equals(a))
				trans.add(t);
		}
		
		return trans;
	}
	
	/**
	 * Returns <code>true</code> if this fa contains a transition leaving
	 * the state {@link Fa#q_i} with symbol <code>a</code>. 
	 * 
	 * @param a the transtion symbol.
	 * @return <code>true</code> if this fa contains a transition leaving
	 *		 the state {@link Fa#q_i} with symbol <code>a</code>. 
	 */
	public boolean reachable(String a) {
		
		Set<Transition> all = hmaps.get(q_i);
		if (all == null) return false;
		
		for (Transition t : all) {
			if (t.a.equals(a))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Gets epsilon-transitions that going to q.
	 * 
	 * @param p the state where epsilon-transitions start.
	 * @return the set of epsilon-transitions.
	 */
	public Set<Transition> getEpsilonTransitionsTo(String q) {
		return emaps.get(q);
	}
	
	/**
	 * Returns all transitions that do not start from an initial transition.
	 * 
	 * @return all transitions that do not start from an initial transition.
	 */
	public Set<Transition> getNonInitialTransitions() {
		Set<Transition> set = new HashSet<Transition>();
		for (Map.Entry<String, Set<Transition>> hmap : hmaps.entrySet()) {
			if (!isInitial(hmap.getKey()))
				set.addAll(hmap.getValue());
			
//			if (map.getKey().matches("\\(.*,.*\\)"))
//				set.addAll(map.getValue());
		}
		
		return set;
	}
	
	/**
	 * Gets all labels of this automaton.
	 * 
	 * @return labels of this automaton.
	 */
	public Set<String> getLabels() {
		HashSet<String> set = new HashSet<String>((int) (1.4*trans.size()));
		for (Transition t : trans.keySet()) {
			set.add(t.a);
		}
		return set;
	}
	
	/**
	 * Returns <code>true</code> if the <code>q</code> is an initial state.
	 * 
	 * @param q the state.
	 * @return <code>true</code> if the <code>q</code> is an initial state.
	 */
	public boolean isInitial(String q) {
		return q.equals(q_i);
	}
	
	/**
	 * Lifts this automaton with <code>g</code>.
	 * The method returns a new automaton.
	 * This automaton remains unchanged.
	 * 
	 * @param g the semiring to be lifted with.
	 * @return the lifted automaton.
	 */
	public Fa lift(Semiring g) {
		Fa lifted = new Fa();
		for (Map.Entry<Transition, Semiring> maps : trans.entrySet()) {
			Transition t = maps.getKey();
			Semiring d = maps.getValue();
			if (isInitial(t.p)) {
				d = d.lift(g);
			} else {
				d = d.id();
			}
			lifted.add(d, t);
		}
		return lifted;
	}
	
	/**
	 * Lazily splits this automaton.
	 * 
	 * @param tid the active thread id.
	 * @param monitor the cancel monitor.
	 * @return a set of splitted aggregates.
	 */
	public List<Splitted> split(int tid, CancelMonitor monitor) {
		
		ArrayList<Splitted> splitted = new ArrayList<Splitted>();
		for (Map.Entry<Transition, Semiring> maps : trans.entrySet()) {
			
			// Returns if canceled
			if (monitor.isCanceled()) return splitted;
			
			Transition t = maps.getKey();
			
			// Skips noninitial or epsilon transition
			if (!isInitial(t.p) || t.a.equals(epsilon))
				continue;
			
			Semiring d = maps.getValue();
			Set<Semiring> set = d.getGlobals();
			for (Semiring g : set) {
				
				// Returns if canceled
				if (monitor.isCanceled()) return splitted;
				
				boolean added = false;
				for (Splitted s : splitted) {
					if (s.g.equals(g)) {
						Sat.log("\tIndex: %d", splitted.indexOf(s));
						s.fa.add(d.restrict(g), t);
						added = true;
						break;
					}
				}
				if (!added) {
					Sat.log("\tNew index: %d", splitted.size());
					Splitted s = new Splitted(g);
					s.fa.add(d.restrict(g), t);
					splitted.add(s);
				}
			}
		}
		Sat.log("splitted.size(): %d%n", splitted.size());
		
		// Copies non-initial transitions
		for (Map.Entry<Transition, Semiring> maps : trans.entrySet()) {
			Transition t = maps.getKey();
			
			if (isInitial(t.p))
				continue;
			
			for (Splitted s : splitted)
				s.fa.add(getWeight(t).id(), t);
		}
		
		return splitted;
	}
	
	/**
	 * A tuple (g,fa), where g is a semiring value and fa is an automaton.
	 * 
	 * @author suwimont
	 *
	 */
	class Splitted {
		Semiring g;
		Fa fa;
		
		/**
		 * Creates a new tuple (g,fa), where fa is newly created.
		 * 
		 * @param g the semiring value.
		 */
		Splitted(Semiring g) {
			this.g = g;
			fa = new Fa();
		}
	}
	
	/**
	 * [Lazy] Approach 2: Gets the relations between globals that can be equivalence.
	 * 
	 * @return the relations.
	 */
	private Semiring getEqRel2() {
		
		// Disjuncts all possible globals
		Semiring w = null;
		for (Transition t : getInitialTransitions()) {
			Semiring f = getWeight(t).getGlobal();
			if (w == null) w = f;
			else w.orWith(f);
		}
		
		Semiring d = w.getEqRel(2);
		w.free();
		return d;
	}
	
	/**
	 * Gets the equivalence relation.
	 * 
	 * @param approach one or two.
	 * @return
	 */
	public Semiring getEqRel(int approach) {
		
		if (approach == 2) {
			return getEqRel2();
		}
		
		Semiring d = null;
		for (Transition t : getInitialTransitions()) {
			
			if (t.a.equals(epsilon)) {
				continue;
			}
			Semiring e = getWeight(t).getEqRel(1);
			
			if (d == null) {
				d = e;
			} else {
				d.andWith(e);
			}
		}
		return d;
	}
	
	/**
	 * Creates a new automaton which is a copy of this automaton but
	 * all its initial transitions are conjoined with <code>eqclass</code>.
	 * The method creates a new automaton.
	 * This automaton remains unchanged.
	 * 
	 * @param eqclass
	 * @return
	 */
	public Fa and(Semiring eqclass) {
		Fa A = new Fa();
		for (Map.Entry<Transition, Semiring> map : trans.entrySet()) {
			
			Transition t = map.getKey();
			Semiring d = map.getValue();
			
			if (isInitial(t.p)) {
				Semiring newd = d.id().andWith(eqclass.id());
				if (newd.isZero()) 
					continue;
				A.add(newd, t);
			} else {
				A.add(d.id(), t);
			}
		}
		return A;
	}
	
	/**
	 * Returns a copy of this automaton.
	 * Note that transition objects are reused; only semiring values are copied.
	 * 
	 * @return a copy of this automaton.
	 */
	public Fa id() {
		Fa A = new Fa();
		for (Map.Entry<Transition, Semiring> map : trans.entrySet()) {
			A.add(map.getValue().id(), map.getKey());
		}
		return A;
	}
	
	/**
	 * [Lazy] Gets disjunctions of all globals of this automaton.
	 * 
	 * @return
	 */
	public Semiring getGlobal() {
		
		Set<Transition> inits = getInitialTransitions();
		if (inits == null) return null;
		
		Semiring d = null;
		for (Transition t : inits) {
			Semiring e = getWeight(t).getGlobal();
			if (d == null) {
				d = e;
			} else {
				d.orWith(e);
			}
		}
		return d;
	}
	
	/**
	 * [Lazy] Updates every transition in this automaton with <code>newglobal</code>.
	 * 
	 * @param newglobal the new global values.
	 * @see Semiring#updateGlobal(Semiring).
	 */
	public void updateGlobal(Semiring newglobal) {
		for (Semiring d : trans.values()) {
			d.updateGlobal(newglobal);
		}
//		Set<Transition> inits = getInitialTransitions();
//		if (inits == null) return;
//		for (Transition t : inits) {
//			getWeight(t).updateGlobal(newglobal);
//		}
	}
	
	/**
	 * Frees the semirings associated with this automaton.
	 */
	public void free() {
		for (Semiring d : trans.values()) {
			if (d == null) continue;
			d.free();
			d = null;
		}
	}
	
	/**
	 * Returns a string representation of this automaton.
	 * 
	 * @return a string representation of this automaton.
	 */
	public String toString() {
		
		StringBuilder out = new StringBuilder();
		for (Map.Entry<Transition, Semiring> pair : trans.entrySet()) {
			out.append(pair.getKey());
			out.append(" (");
			out.append(pair.getValue());
			out.append(")\n");
		}
		return out.toString();
	}
}

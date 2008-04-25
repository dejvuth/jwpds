package de.tum.in.wpds;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.tum.in.wpds.Fa.Splitted;

/**
 * A saturation procedure for DPNs.
 * 
 * @author suwimont
 *
 */
public class DpnSat extends Sat {
	
	/**
	 * The DPN.
	 */
	private Dpn dpn;
	
	/**
	 * The initial global valuation.
	 */
	private Semiring g0;
	
	/**
	 * The thread bound.
	 */
	private int n;
	
	/**
	 * The context-switch bound.
	 */
	private int k;
	
	/**
	 * Determines if the analysis is symbolic.
	 */
	private boolean symbolic;
	
	/**
	 * Maps config <p,a> to rules having <p,a> on the left-hand-side, 
	 * i.e. rules of the form <p,a> -> <q,w>
	 */
	HashMap<Config, Set<Rule>> rmap;
	
	/**
	 * The set of reachable global configurations.
	 */
	private DpnReach reach;
	
	/**
	 * The workset.
	 */
	private WorkSet<WorkItem> workset;
	
	/**
	 * The id of the currently active thread.
	 */
	private static int currentThreadId;
	
	/**
	 * Records the time required for splitting.
	 */
	private float splittingTime = 0;
	
	/**
	 * The constructor.
	 * 
	 * @param dpn the DPN.
	 * @param g0 the initial global values.
	 * @param n the thread bound.
	 * @param k the context bound.
	 * @param symbolic determines if the analysis is symbolic.
	 */
	public DpnSat(Dpn dpn, Semiring g0, int n, int k, boolean symbolic) {
		this.dpn = dpn;
		this.g0 = g0;
		this.n = n;
		this.k = k;
		this.symbolic = symbolic;
	}
	
	/**
	 * Gets the current thread id. The main thread has id one.
	 * 
	 * @return the current thread id.
	 */
	public static int getCurrentThreadId() {
		return currentThreadId;
	}

	/**
	 * Updates the transition <code>t</code> of the automaton <code>fa</code>
	 * with the new semiring value <code>d</code>.
	 * The method returns <code>true</code> if the automaton was modified.
	 * 
	 * @param fa the automaton.
	 * @param trans the set of transitions to be considered in the saturation algorithm.
	 * @param d the new semiring value.
	 * @param t the transition.
	 * @return <code>true</code> if the semiring value of <code>t</code> was changed.
	 */
	private static boolean update(Fa fa, WorkSet<Transition> trans, Semiring d, Transition t) {
		
		// Do not add if d is zero
		if (d.isZero()) {
			log("\t\t\tZero found, not adding %s%n", t);
			return false;
		}
		
		// Adds the transition to fa
		boolean updated = false;
		if (fa.add(d, t)) {
			trans.add(t);
			updated = true;
		}
		
		return updated;
	}
	
	/**
	 * Updates the transition <code>(p,a,q)</code> of the automaton <code>fa</code>
	 * with the new semiring value <code>d</code>.
	 * The method returns <code>true</code> if the automaton was modified.
	 * 
	 * @param fa the automaton.
	 * @param trans the set of transitions to be considered in the saturation algorithm.
	 * @param d the new semiring value.
	 * @param p the transition's from-state.
	 * @param a the transition's symbol.
	 * @param q the transition's to-state.
	 * @return <code>true</code> if the semiring value of <code>t</code> was changed.
	 */
	private static boolean update(Fa fa, WorkSet<Transition> trans, Semiring d, 
			String p, String a, String q) {
		return update(fa, trans, d, new Transition(p, a, q));
	}
	
	/**
	 * Gets the equivalence relation from the automata specified by A.
	 * The result is too fine. Not used at the moment.
	 * 
	 * @param c the active automaton index.
	 * @param A the automata.
	 * @return the equivalence relation.
	 */
	private Semiring getEqRel1(int c, Fa[] A) {
		
		Semiring eqrel = null;//A[c].getEqRel(0);
		for (int i = 0; i < A.length; i++) {
			
			// Returns if canceled
			if (monitor.isCanceled()) return null;
			
			if (i == c)
				continue;
			
			if (eqrel == null)
				eqrel = A[i].getEqRel(1);
			else
				eqrel.andWith(A[i].getEqRel(1));
		}
		return eqrel;
	}
	
	/**
	 * The first splitting approach from Javier.
	 * The result is too fine. Not used at the moment.
	 * 
	 * @param level the depth of the analysis.
	 * @param c the active automaton index.
	 * @param A the automata.
	 */
	@SuppressWarnings("unused")
	private void approach1(int level, int c, Fa[] A) {
		
		// Finds equivalence relation
		Semiring eqrel = getEqRel1(c, A);
		if (eqrel == null) return;
		
		while (!eqrel.isZero()) {
//			log("eqrel: %s%n", eqrel.toRawString());
			
			// Returns if canceled
			if (monitor.isCanceled()) return;
			
			Semiring eqclass = eqrel.getEqClass(1);
			log("eqclass: %s%n%n", eqclass.toRawString());
			
			// Restricts to the new equivalence class
			Fa[] newA = new Fa[A.length];
			for (int i = 0; i < A.length; i++) {
				log("Creating automaton %d%n", i);
				newA[i] = A[i].and(eqclass);
			}
			
			// Updates globals
			Semiring newglobal = newA[c].getGlobal();
			if (monitor.isCanceled()) return;
			if (newglobal != null) {
//				log("Updating with newglobal: %s%n", newglobal.toRawString());
				for (int i = 0; i < A.length; i++) {
					newA[i].updateGlobal(newglobal);
				}
				newglobal.free();
				
				WorkItem item = new WorkItem(level, c, null, newA);
				log("Adding to worklist with id=%d: (level: %d, c: %d, j: %d)%n%n", 
						item.id, level, c, newA.length);
				workset.add(item);
				reach.add(null, newA);
			} else {
				for(int i = 0; i < A.length; i++)
					newA[i].free();
			}
			
			eqrel.sliceWith(eqclass, 1);
		}
		
		A[c].free();
	}
	
	private static float[] times = new float[5];
	
	private static float elapsedTime(long startTime) {
		return (float) ((System.currentTimeMillis() - startTime) / 1000.0);
	}
	
	/**
	 * Lazily splits the aggregate, updates globals, 
	 * and puts them into the workset.
	 * 
	 * @param level the depth of the analysis.
	 * @param c the index of the active automaton.
	 * @param A the automata.
	 */
	private void approach2(int level, int c, Fa[] A) {
		
		long start = System.currentTimeMillis();
		int splitCount = 0;
		
		// Finds equivalence relation
		long before = System.currentTimeMillis();
		Semiring eqrel = A[c].getEqRel(2);
		times[0] = elapsedTime(before);
		if (eqrel == null) return;
		
		while (!eqrel.isZero()) {
			
			// Returns if canceled
			splitCount++;
			if (monitor.isCanceled()) return;
			
			// Gets an equivalence class
			before = System.currentTimeMillis();
			Semiring eqclass = eqrel.getEqClass(2);
			times[1] = elapsedTime(before);
			
//			log("eqclass: %s%n", eqclass.toRawString());
			
			// Restricts to the new equivalence class
			Fa[] newA = new Fa[A.length];
			for (int i = 0; i < A.length; i++) {
				log("Creating automaton %d%n", i);
				newA[i] = A[i].and(eqclass);
			}
			
			// Retrieves globals
			before = System.currentTimeMillis();
			Semiring newglobal = newA[c].getGlobal();
			times[2] = elapsedTime(before);
			
			if (newglobal != null) {
				// Updates globals
				if (Sat.DEBUG)
					log("Updating with newglobal: %s%n", newglobal.toRawString());
				before = System.currentTimeMillis();
				for (int i = 0; i < A.length; i++) {
					newA[i].updateGlobal(newglobal);
				}
				times[3] = elapsedTime(before);
				newglobal.free();
				
				WorkItem item = new WorkItem(level, c, null, newA);
				log("Adding to worklist with id=%d: (level: %d, c: %d, j: %d)%n%n", 
						item.id, level, c, newA.length);
				workset.add(item);
				reach.add(null, newA);
			} else {
				// Deletes automata in case of canceled
				for(int i = 0; i < A.length; i++)
					newA[i].free();
			}
			
			before = System.currentTimeMillis();
			eqrel.sliceWith(eqclass, 2);
			times[4] = elapsedTime(before);
			
			log(
					"Time: [rel=%.2f, eqclass=%.2f, extract=%.2f, update=%.2f, slice=%.2f]%n",
					times[0], times[1], times[2], times[3], times[4]);
		}
		
		// Statistics
		float elapsed = (float) ((System.currentTimeMillis() - start) / 1000.0);
		log("Splitting %d times required: %.2fs%n%n", splitCount, elapsed);
		splittingTime += elapsed;
	}
	
	/**
	 * Saturates the automaton <code>A[c]</code>,
	 * splits, updates globals of the other automata,
	 * and puts them into the workset 
	 * if <code>level</code> does not exceed the bound.
	 * 
	 * @param level the depth of the analysis.
	 * @param c the index of the active automaton.
	 * @param g the global values before saturating.
	 * @param A the automata.
	 * @param ind the indices to be saturated.
	 */
	private void sat(int level, int c, Semiring g, Fa[] A, WorkSet<Integer> ind) {
		
		log("sat(level: %d, c: %d, g: %s, j: %d, ind: %s)%n", 
				level, c, ""/*g.toRawString()*/, A.length, ind);
		int j = A.length;
		boolean addtoworklist = (ind.size() > 1) ? true : false;
		while (!ind.isEmpty()) {
			
			// Returns if canceled
			if (monitor.isCanceled()) return;
			
			// Removes i from ind
			int i = (Integer) ind.remove();
			log("i: %d%n", i);
			currentThreadId = i + 1;
			
			// Initializes trans by adding all transitions of A[i] to it
			Fa Ai = A[i];
			WorkSet<Transition> trans = new LifoWorkSet<Transition>();
			for (Transition t : Ai.trans.keySet())
				trans.add(t);
			
			// Loops until trans is empty
			while (!trans.isEmpty()) {
				
				// Returns if canceled
				if (monitor.isCanceled()) return;
				
				// Removes t = (p,a,q) from trans
				Transition t = (Transition) trans.remove();
				Semiring d = Ai.getWeight(t);
				
				// a is epsilon
				if (t.a.equals(Fa.epsilon)) {
					Set<Transition> tqSet = Ai.hmaps.get(t.q);
					if (tqSet == null) continue;
					for (Transition tq : tqSet) {
						log("\t\t\tTransition reached from epsilon %s%n", tq);
						if (DEBUG) log("%n\t\t\t%s%n%n", Ai.getWeight(tq).toRawString());
						Semiring newd = d.extendPop(Ai.getWeight(tq), monitor);
						if (update(Ai, trans, newd, t.p, tq.a, tq.q)) {
							updateListener(tq.a);
							addtoworklist = true;
						}
					}
					continue;
				}
				
				// a is not epsilon
				Set<Rule> rules = rmap.get(new Config(t.p, t.a));
				if (rules == null) {
					log("\tNo matching rule found\n");
					continue;
				}
				for (Rule rule : rules) {
					
					// Skips the global rule if A_i doesn't control the context
					log("\tRule %s%n", rule);
					if (i != c && rule.isGlobal()) {
						log("\t\tSkip global rule%n");
						continue;
					}
					
					// Extend: computes new semiring value
					Semiring newd = d.extend(rule.d, monitor);
					if (newd.isZero()) {
						log("\t\tZero after extended%n");
						continue;
					}
					
					// Dynamic rule
					String p = rule.right.p;
					String[] w = rule.right.w;
					if (rule.isDynamic()) {
						if (j >= n) {
							log("\t\tThread bound exceeded%n");
							if(update(Ai, trans, newd, p, w[0], t.q)) {
								updateListener(w[0]);
								addtoworklist = true;
							}
							continue;
						}
						
						// Creates A_i'
						log("\t\tNew A_i'%n");
						Fa newAi = new Fa();
						newAi.add(newd, p, w[0], t.q);
						updateListener(w[0]);
						Set<Transition> noninits = Ai.getNonInitialTransitions();
						if (noninits != null) {
							for (Transition noninit : noninits) {
								newAi.add(Ai.getWeight(noninit).id(), 
										(Transition) noninit.clone());
							}
						}
						
						// Creates A_j
						log("\t\tNew A_j%n");
						Fa Aj = new Fa();
						Aj.add(d.extendDynamic(rule.d, monitor), 
								rule.dynamic.p, rule.dynamic.w[0], Fa.q_f);
						
						// Recursive call
						Fa[] newA = new Fa[j + 1];
						System.arraycopy(A, 0, newA, 0, j);
						newA[i] = newAi;
						newA[j] = Aj;
						WorkSet<Integer> newInd = new LifoWorkSet<Integer>();
						newInd.addAll(ind);
						newInd.add(i);
						newInd.add(j);
						sat(level, c, g, newA, newInd);
						
						continue;
					}
					
					// Pop rule
					if (w.length == 0) {	
						if (update(Ai, trans, newd, p, Fa.epsilon, t.q))
							addtoworklist = true;
						continue;
					}
					
					// Normal rule
					if (w.length == 1) {
						if(update(Ai, trans, newd, p, w[0], t.q)) {
							updateListener(w[0]);
							addtoworklist = true;
						}
						continue;
					}
					
					// Push rule
					String s = String.format("(%s,%s)", p, w[0]);
					if (update(Ai, trans, d.extendPush(rule.d, monitor), p, w[0], s)) {
						updateListener(w[0]);
						addtoworklist = true;
					}
					if (update(Ai, trans, newd, s, w[1], t.q))
						addtoworklist = true;
					Set<Transition> set = Ai.getEpsilonTransitionsTo(s);
					if (set != null) {
						for (Transition ts : set) {
							log("\t\t\tTransition reached from epsilon %s%n", ts);
							if (DEBUG) log("%n\t\t\t%s%n%n", Ai.getWeight(ts).toRawString());
							if (update(Ai, trans, newd.extendPop(Ai.getWeight(ts), monitor), 
									ts.p, w[1], t.q)) {
								updateListener(w[1]);
								addtoworklist = true;
							}
						}
					}
				}
			}
		}
		
		/* 
		 * Returns if sat is called with ind having one element { i }, and
		 * the saturation procedure does not change the automaton A_i.
		 */
		if (!addtoworklist) {
			log("Automaton unchanged%n%n");
			return;
		}
		
		if (j == 1) {
			log("Only one automaton. Do not split.%n%n");
			return;
		}
		
		if (level >= k) {
			log("Context bound reached%n%n");
			return;
		}
		
		log("Splitting...%n");
		if (!symbolic) {
			List<Splitted> splitted = A[c].split(currentThreadId, monitor);
			log("Split count: %d%n", splitted.size());
			for (Splitted s : splitted) {
				
				// Returns if canceled
				if (monitor.isCanceled()) return;
				
				// Creates new automata
				Fa[] newA = new Fa[j];
				for (int i = 0; i < j; i++) {
					newA[i] = (i == c) ? s.fa : A[i].id();
				}
				
				WorkItem item = new WorkItem(level, c, s.g, newA);
				log("Adding to worklist with id=%d: (level: %d, c: %d, s.g: %s, j: %d)%n%n", 
						item.id, level, c, ""/*s.g.toRawString()*/, newA.length);
				workset.add(item);
				reach.add(s.g, newA);
			}
		} else {
			approach2(level, c, A);
		}
		
		log("Returning from sat(level: %d, c: %d, j: %d, ind: %s)%n%n", 
				level, c, A.length, ind);
	}

	/**
	 * Performs bounded context-switch analysis.
	 * 
	 * @param fa the initial automaton.
	 * @param monitor the cancel monitor.
	 * @return nothing for now.
	 */
	public DpnReach poststar(Fa fa, CancelMonitor monitor) {
		
		this.monitor = monitor;
		workId = 0;
		int processed = 0;
		
		reach = new DpnReach();
		workset = new FifoWorkSet<WorkItem>();
		workset.add(new WorkItem(0, -1, g0, fa));
		
		rmap = dpn.getLeftMapper();
		
		while (!workset.isEmpty()) {
			
			// Returns if canceled
			processed++;
			if (monitor.isCanceled()) {
				Sat.info("Analyzed: %d aggregates (%d left)%n", 
						processed, workset.size());
				return reach;
			}
			
			// Removes a work item
			WorkItem wi = workset.remove();
			monitor.subTask(String.format(
					"Analyzing aggregate %d (level %d) ...", wi.id, wi.level + 1));
			log("Removing from worklist (id=%d): (level: %d, last: %d, g: %s, j: %d)%n",
					wi.id, wi.level, wi.last, (symbolic || !DEBUG) ? "" : wi.g.toRawString(), wi.A.length);
//			System.out.printf("%d (level %d): %n", wi.id, wi.level);
			
			// Returns if the threshold reached
			if (wi.level >= k) {
				
				//FIXME deletes the result to save some space
				if (wi.g != null) wi.g.free();
				if (!symbolic) wi.A[wi.last].free();
				
				continue;
			}
			
			// Iterates each automaton (A_i) in the work item
			Fa[] A = wi.A;
			for (int i = 0; i < A.length; i++) {
				
				// Do not saturate the previous automaton
				log("i: %d, wi.last: %d%n", i, wi.last);
				if (i == wi.last) continue;
				
				// Lifts A_i with g
				log("Lifting ...");
				Fa[] newA;
				newA = new Fa[A.length];
				System.arraycopy(A, 0, newA, 0, A.length);
				newA[i] = A[i].lift(wi.g);
				log("done%n");
				
				// Saturates
				WorkSet<Integer> ind = new LifoWorkSet<Integer>();
				ind.add(i);
				sat(wi.level + 1, i, wi.g, newA, ind);
			}
			
			// Deletes g
			if (wi.g != null) {
				wi.g.free();
				wi.g = null;
			}
			
			// Keeps the initial automaton, it must be deleted later
			if (wi.level > 0) {
				for (int i = 0; i < A.length; i++) {
					A[i].free();
				}
			}
		}
		
		Sat.info("Analyzed: %d aggregates (%d left)%n", 
				processed, workset.size());
		return reach;
	}
	
	/**
	 * Counts the number of work items
	 */
	static int workId = 0;
	
	/**
	 * Worklist entry.
	 * 
	 * @author suwimont
	 *
	 */
	private class WorkItem {
		int level;
		int last;
		Semiring g;
		Fa[] A;
		int id;
		
		public WorkItem(int level, int last, Semiring g, Fa... A) {
			this.level = level;
			this.last = last;
			this.g = g;
			this.A = A;
			
			this.id = workId++;
		}
	}
}

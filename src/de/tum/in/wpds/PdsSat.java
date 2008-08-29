package de.tum.in.wpds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PdsSat extends Sat {

	private Pds pds;
	private Fa sat;
	private WorkSet<Transition> workset = new LifoWorkSet<Transition>();
//	private WitnessGraph wgraph = new WitnessGraph();
	
	public PdsSat(Pds pds) {
		this.pds = pds;
	}
	
//	public WitnessGraph getWitnessGraph() {
//		return wgraph;
//	}
	
	/**
	 * Updates the saturating automaton with the transition <code>t</code> 
	 * and the semiring value <code>d</code>.
	 * The method should add more elements to the worklist
	 * if the transition is new or the semiring value is not already included.
	 * The rule <code>r</code> and transitions <code>T</code>
	 * are witnesses.
	 * 
	 * @param r the witness rule.
	 * @param d the sermiring value.
	 * @param t the transition.
	 * @param T the witness transitions.
	 * @return <code>true</code> iff the transition is new or the semiring
	 * 			value is not already included.
	 */
	private boolean update(Rule r, Semiring d, Transition t, Transition... T) {
		
		if (d.isZero())
			return false;
		
		boolean updated = false;
		if (sat.add(d, t)) {
			workset.add(t);
			updated = true;
		}
		
//		WitnessNode node = wgraph.removeNode(t);
//		if (node == null) node = new WitnessNode(t);
//		ArrayList<WitnessNode> P = new ArrayList<WitnessNode>();
//		if (T != null) {
//			for (Transition t1 : T) {
//				WitnessNode n = wgraph.removeNode(t1);
//				if (n == null) n = new WitnessNode(t1);
//				P.add(n);
//				n.N.add(node);
//				wgraph.putNode(t1, n);
//			}
//		}
//		node.S.add(new WitnessStruct(d, t, r, P));
//		wgraph.putNode(t, node);
		
		return updated;
	}
	
	/**
	 * A convenient method for 
	 * {@link #update(Rule, Semiring, Transition, Transition...)}.
	 * 
	 * @param r the witness rule.
	 * @param d the sermiring value.
	 * @param p the transition's from-state
	 * @param a the transition's letter.
	 * @param q the transition's to-state
	 * @param T the witness transitions.
	 * @return <code>true</code> iff the transition is new or the semiring
	 * 			value is not already included.
	 */
	private boolean update(Rule r, Semiring d, String p, String a, String q, 
			Transition... T) {
		return update(r, d, new Transition(p, a, q), T);
	}
	
	/**
	 * Repeatedly removes an element from the workset and saturates.
	 */
	private void depleteWorkset() {
		
		HashMap<Config, Set<Rule>> rmap = pds.getLeftMapper();
		while (!workset.isEmpty()) {
			
			if (monitor.isCanceled()) return;
			
			Transition t = (Transition) workset.remove();
			Semiring d;
			log("%nSaturating %s%n", t);
			
			// For all rules beginning with <p,a>
			Set<Rule> rules = rmap.get(new Config(t.p, t.a));
			if (rules == null) {
				log("\tNo matching rule found\n");
				continue;
			}
			for (Rule rule : rules) {
				
				log("\tRule %s%n", rule);
				String p = rule.right.p;
				String[] w = rule.right.w;
				d = sat.getWeight(t).extend(rule.d, monitor);
				if (d.isZero()) {
					log("\t\tZero after extended\n");
					continue;
				}
				
				// Pop rule
				if (w.length == 0) {
					
					// Adds epsilon transition
					update(rule, d, p, Fa.epsilon, t.q, t);
					
					// Adds transitions that are reachable from this epsilon transition
					Set<Transition> trans = sat.hmaps.get(t.q);
					if (trans == null) continue;
					for (Transition tq : trans) {
						log("\t\t\tTransition reached from epsilon %s%n", tq);
						if (update(rule, d.extendPop(sat.getWeight(tq), monitor), 
								p, tq.a, tq.q, t, tq))
							updateListener(tq.a);
					}
					continue;
				}
				
				// Normal rule
				if (w.length == 1) {
					
					if(update(rule, d, p, w[0], t.q, t))
						updateListener(w[0]);
					continue;
				}
				
				// Push rule
				String s = String.format("(%s,%s)", p, w[0]);
				if (update(rule, sat.getWeight(t).extendPush(rule.d, monitor), p, w[0], s, t)) {
					updateListener(w[0]);
				} 
//				else {
//					Set<Transition> trans = sat.getEpsilonTransitions(p);
//					for (Transition tp : trans) {
//						if (!tp.q.equals(s)) continue;
//						log("\t\t\tTransition reached from epsilon %s%n", tp);
//						if (update(rule, d.extendPop(sat.trans.get(tp), monitor), p, w[1], t.q, t, tp))
//							updateListener(tp.a);
//					}
//				}
				Set<Transition> set = sat.getEpsilonTransitionsTo(s);
				if (set != null) {
					for (Transition ts : set) {
						log("\t\t\tTransition reached from epsilon %s%n", ts);
						if (update(rule, sat.getWeight(ts).extendPop(d, monitor), 
								ts.p, w[1], t.q, t, ts))
							updateListener(w[1]);
					}
				}
				update(rule, d, s, w[1], t.q, t);
			}
		}
	}
	
	/**
	 * Computes post* of the given fa.
	 * 
	 * @param fa the initial automaton.
	 * @param monitor the monitor.
	 * @return the saturated automaton.
	 */
	public Fa poststar(Fa fa, CancelMonitor monitor) {
		
		log("Beginning post*%n");
		this.monitor = monitor;
		
		// Creates new FA and adds all transitions to it.
		sat = new Fa();
		for (Map.Entry<Transition, Semiring> e : fa.trans.entrySet()) {
			
			update(null, e.getValue().id(), e.getKey());
		}
		
		// Depletes the workset
		depleteWorkset();
		
		log("Ending post*%n");
		return sat;
	}
	
//	public Fa addRuleAndSaturate(Rule r) {
//		
//		pds.add(r);
//		HashMap<Config, Set<WitnessNode>> nodeMapper = wgraph.getNodeMapper();
//		for (WitnessNode node : nodeMapper.get(r.left))
//			workset.add(node.t);
//		
//		depleteWorkset();
//		return sat;
//	}
	
	// TODO
//	public Fa removeRule(Rule r) {
//		
//		return sat;
//	}
}

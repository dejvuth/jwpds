package de.tum.in.wpds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WitnessGraph {

	public HashMap<Transition, WitnessNode> nodes = new HashMap<Transition, WitnessNode>();
	HashMap<Config, Set<WitnessNode>> nodeMapper;
	
	/**
	 * Puts the witness node n for the transition t.
	 * 
	 * @param t the transition.
	 * @param n the witness node.
	 */
	public void putNode(Transition t, WitnessNode n) {
		
		nodes.put(t, n);
		if (nodeMapper != null) 
			addWitnessNodeToMapper(nodeMapper, new Config(t.p, t.a), n);
	}
	
	/**
	 * Removes the witness node of transition t from the graph.
	 * Note that this method does not effect the node mapper,
	 * i.e. nodeMapper remains unchanged.
	 * 
	 * @param t
	 * @return
	 */
	public WitnessNode removeNode(Transition t) {
		
		return nodes.remove(t);
	}
	
	/**
	 * Returns a list of TraceNode representing a trace from the transition
	 * "from" to the transition "to".
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public List<TraceNode> trace(Transition from, Transition to) {
		
		return trace(new HashSet<WitnessNode>(), from, to);
	}
	
	private List<TraceNode> trace(HashSet<WitnessNode> visited, Transition from, Transition to) {
		
		if (from.equals(to)) {
			List<TraceNode> trace = new ArrayList<TraceNode>();
			trace.add(new TraceNode(from, nodes.get(from).S.get(0).d, null));
			return trace;
		}
		
		WitnessNode tnode = nodes.get(to);
		visited.add(tnode);
		if (visited.size() == nodes.size()) return null;
		
		for (WitnessStruct s : tnode.S) {
			
			//for (WitnessNode p : s.previous) {
			WitnessNode p = s.previous.get(0);
				
				if (visited.contains(p)) continue;
				
				List<TraceNode> trace = trace(visited, from, p.t);
				if (trace != null) {
					trace.add(new TraceNode(to, s.d, s.r));
					return trace;
				}
			//}
		}
		
		return null;
	}
	
	public static void print(List<TraceNode> trace) {
		
		if (trace == null) {
			System.out.println("No trace found");
			return;
		}
		for (TraceNode node : trace) {
			System.out.println(node);
		}
	}
	
	private static void addWitnessNodeToMapper(
			HashMap<Config, Set<WitnessNode>> mapper, Config c, WitnessNode n) {
		
		Set<WitnessNode> set = mapper.remove(c);
		if (set == null) set = new HashSet<WitnessNode>();
		set.add(n);
		mapper.put(c, set);
	}
	
	public HashMap<Config, Set<WitnessNode>> getNodeMapper() {
		
		if (nodeMapper != null) return nodeMapper;
		
		nodeMapper = new HashMap<Config, Set<WitnessNode>>();
		for (WitnessNode node : nodes.values()) {
			System.out.println(node);
			addWitnessNodeToMapper(nodeMapper, new Config(node.t.p, node.t.a), node);
		}
		
		return nodeMapper;
	}
}

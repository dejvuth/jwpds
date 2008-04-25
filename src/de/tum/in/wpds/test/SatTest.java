package de.tum.in.wpds.test;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import de.tum.in.wpds.Fa;
import de.tum.in.wpds.Pds;
import de.tum.in.wpds.Rule;
import de.tum.in.wpds.PdsSat;
import de.tum.in.wpds.TraceNode;
import de.tum.in.wpds.Transition;
import de.tum.in.wpds.WitnessGraph;
import de.tum.in.wpds.WitnessNode;


public class SatTest {

	@Test public void testEx() {
		
		String p = "p", q = "q", s = "s";
		String a = "a", b = "b", c = "c", d = "d";
		
		Pds pds = new Pds();
		pds.add(new MinSemiring(5), p, a, q, b);
		pds.add(new MinSemiring(4), p, a, p, c);
		pds.add(new MinSemiring(3), q, b, p, d);
		pds.add(new MinSemiring(2), p, c, p, a, d);
		pds.add(new MinSemiring(1), p, d, p);
		System.out.println(pds);
		
		Fa fa = new Fa();
		fa.add(new MinSemiring(0), p, a, s);
		System.out.println(fa);
		
		PdsSat sat = new PdsSat(pds);
		Fa post = (Fa) sat.poststar(fa);
		System.out.println("\npost*:");
		System.out.println(post);
		
//		System.out.println("\nwgraph:");
//		WitnessGraph wgraph = sat.getWitnessGraph();
//		for (Map.Entry<Transition, WitnessNode> entry : wgraph.nodes.entrySet())
//			System.out.println(entry);
//		
//		System.out.println("\ntrace:");
//		List<TraceNode> trace = wgraph.trace(new Transition(p, a, s), new Transition(p, d, s));
//		WitnessGraph.print(trace);
//		
//		post = sat.addRuleAndSaturate(new Rule(new MinSemiring(6), q, b, q));
//		trace = wgraph.trace(new Transition(p, a, s), new Transition(q, d, s));
//		System.out.println("\ntrace:");
//		WitnessGraph.print(trace);
	}
}

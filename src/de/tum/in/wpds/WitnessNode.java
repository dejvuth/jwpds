package de.tum.in.wpds;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class WitnessNode {

	Transition t;
	ArrayList<WitnessStruct> S = new ArrayList<WitnessStruct>();
	HashSet<WitnessNode> N = new HashSet<WitnessNode>();
	
	protected int id;
	private static int count = 0;
	
	public WitnessNode(Transition t) {
		
		id = count++;
		this.t = t;
	}
	
	public String toString() {
		
		StringBuilder out = new StringBuilder(String.format("(%d) N: {", id));
		Iterator<WitnessNode> itr = N.iterator();
		if (itr.hasNext()) out.append(itr.next().id);
		while (itr.hasNext()) {
			out.append(",");
			out.append(itr.next().id);
		}
		out.append("} S: ");
		out.append(S);
		
		return out.toString();
	}
}
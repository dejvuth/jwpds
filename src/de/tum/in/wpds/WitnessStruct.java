package de.tum.in.wpds;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class WitnessStruct {

	public Semiring d;
	public Transition t;
	public Rule r;
	public ArrayList<WitnessNode> previous;
	
	public WitnessStruct(Semiring d, Transition t, Rule r, ArrayList<WitnessNode> previous) {
		
		this.d = d;
		this.t = t;
		this.r = r;
		this.previous = previous;
	}
	
	public String toString() {
		
		StringBuilder out = new StringBuilder(String.format("(%s, %s, %s, {", d, t, r));
		Iterator<WitnessNode> itr = previous.iterator();
		if (itr.hasNext()) out.append(itr.next().id);
		while (itr.hasNext()) {
			out.append(",");
			out.append(itr.next().id);
		}
		out.append("})");
		
		return out.toString();
	}
}

package de.tum.in.wpds;

import java.util.HashSet;
import java.util.Set;

public class DpnReach {
	
	private Set<Reach> reach = new HashSet<Reach>();
	
	public void add(Semiring g, Fa... A) {
		reach.add(new Reach(g, A));
	}
	
	public boolean reachable(String a, String b) {
		for (Reach r : reach) {
			if (r.reachable(a, b))
				return true;
		}
		return false;
	}
	
	public void free() {
		for (Reach r : reach) {
			r.free();
		}
	}

	private class Reach {
		Semiring g;
		Fa[] A;
		
		Reach(Semiring g, Fa... A) {
			this.g = g;
			this.A = A;
		}
		
		/**
		 * Returns <code>true</code> if <code>a</code> and <code>b</code>
		 * are reachable in two different automata.
		 * 
		 * @param a the symbol a.
		 * @param b the symbol b.
		 * @return <code>true</code> if <code>a</code> and <code>b</code>
		 * 		are reachable at the same time.
		 */
		boolean reachable(String a, String b) {
			
			// Returns false if there are less than two automata
			if (A.length < 2) return false;

			// Finds a in every A[i]
			Boolean[] foundb = new Boolean[A.length];
			for (int i = 0; i < A.length; i++) {
				
				// Continues if a is not reachable in A[i]
				if (!A[i].reachable(a)) continue;
				
				// Finds b in every A[j], where j != i
				for (int j = 0; j < A.length; j++) {
					
					if (j == i) continue;
					
					// Looks in buffer first
					if (foundb[j] != null) {
						if (foundb[j]) return true;
						continue;
					}
					
					// Returns true if b is reachable in A[j]
					foundb[j] = A[j].reachable(b);
					if (foundb[j]) return true;
				}
			}
			
			return false;
		}
		
		void free() {
			//FIXME there are already freed, sorry
		}
	}
}

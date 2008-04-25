/**
 * 
 */
package de.tum.in.wpds;

import java.util.Iterator;
import java.util.Stack;

/**
 * A last-in-first-out implementation of <code>WorkSet</code>.
 * 
 * @author suwimont
 *
 * @param <E>
 */
public class LifoWorkSet<E> implements WorkSet<E> {

	private Stack<E> stack;
	
	public LifoWorkSet() {
		stack = new Stack<E>();
	}
	
	public void add(E t) {
		stack.push(t);
	}
	
	public void addAll(WorkSet<E> s) {
		Iterator<E> itr = s.itr();
		while (itr.hasNext())
			add(itr.next());
	}
	
	public Iterator<E> itr() {
		return stack.iterator();
	}

	public E remove() {
		return stack.pop();
	}

	public boolean isEmpty() {
		return stack.isEmpty();
	}
	
	public int size() {
		return stack.size();
	}
	
	public String toString() {
		return stack.toString();
	}
}
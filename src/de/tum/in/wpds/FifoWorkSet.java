package de.tum.in.wpds;

import java.util.Iterator;
import java.util.LinkedList;

public class FifoWorkSet<E> implements WorkSet<E> {
	
	private LinkedList<E> list = new LinkedList<E>();

	public void add(E o) {
		list.add(o);
	}

	public void addAll(WorkSet<E> s) {
		Iterator<E> itr = s.itr();
		while (itr.hasNext())
			add(itr.next());
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public Iterator<E> itr() {
		return list.iterator();
	}

	public E remove() {
		return list.remove();
	}

	public int size() {
		return list.size();
	}

}

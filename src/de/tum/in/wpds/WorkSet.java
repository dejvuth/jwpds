package de.tum.in.wpds;

import java.util.Iterator;

public interface WorkSet<E> {

	public void add(E o);
	public void addAll(WorkSet<E> s);
	public Iterator<E> itr();
	public E remove();
	public boolean isEmpty();
	public int size();
}

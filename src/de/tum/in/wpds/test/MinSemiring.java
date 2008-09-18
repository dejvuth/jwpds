package de.tum.in.wpds.test;

import java.util.Set;

import de.tum.in.wpds.CancelMonitor;
import de.tum.in.wpds.Semiring;

public class MinSemiring implements Semiring {

	public Integer v;
	
	public MinSemiring(Integer v) {
		
		this.v = v;
	}
	
	public Semiring combine(Semiring a) {
		
		return new MinSemiring(Math.min(v, ((MinSemiring) a).v));
	}

	public Semiring extend(Semiring a, CancelMonitor monitor) {
		
		return new MinSemiring(v + ((MinSemiring) a).v);
	}
	
	public Semiring extendPush(Semiring a, CancelMonitor monitor) {
		
		return new MinSemiring(0);
	}

	public boolean equals(Object o) {
		
		if (o == null) return false;
		
		if (!(o instanceof MinSemiring)) return false;
		
		MinSemiring r = (MinSemiring) o;
		return v.intValue() == r.v.intValue();
	}
	
	public String toString() {
		
		return v.toString();
	}

	public Semiring id() {
		
		return new MinSemiring(new Integer(v.intValue()));
	}

	public Semiring extendPop(Semiring a, CancelMonitor monitor) {
		
		return extend(a, monitor);
	}

	public Semiring one() {
		
		return new MinSemiring(0);
	}

	public Semiring zero() {
		
		return new MinSemiring(Integer.MAX_VALUE);
	}

	public Semiring extendDynamic(Semiring a, CancelMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Semiring> getGlobals() {
		// TODO Auto-generated method stub
		return null;
	}

	public Semiring andWith(Semiring a) {
		// TODO Auto-generated method stub
		return null;
	}

	public void free() {
		// TODO Auto-generated method stub
		
	}

	public Semiring getEqClass(int approach) {
		// TODO Auto-generated method stub
		return null;
	}

	public Semiring getEqRel(int approach) {
		// TODO Auto-generated method stub
		return null;
	}

	public Semiring getGlobal() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isZero() {
		// TODO Auto-generated method stub
		return false;
	}

	public Semiring lift(Semiring a) {
		// TODO Auto-generated method stub
		return null;
	}

	public Semiring orWith(Semiring a) {
		// TODO Auto-generated method stub
		return null;
	}

	public Semiring restrict(Semiring a) {
		// TODO Auto-generated method stub
		return null;
	}

	public void sliceWith(Semiring eqclass, int approach) {
		// TODO Auto-generated method stub
		
	}

	public String toRawString() {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateGlobal(Semiring a) {
		// TODO Auto-generated method stub
		
	}

	public Semiring diff(Semiring a) {
		// TODO Auto-generated method stub
		return null;
	}
}

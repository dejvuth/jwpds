package de.tum.in.wpds.test;

import org.junit.Assert;
import org.junit.Test;

import de.tum.in.wpds.Fa;
import de.tum.in.wpds.Semiring;
import de.tum.in.wpds.Transition;


public class FaTest {

	@Test public void testHashCode() {
		
		Fa fa = new Fa();
		fa.add(new MinSemiring(1), "(p,q)", "a", "q");
		fa.add(new MinSemiring(1), "(p,q)", "a", "q");
		fa.add(new MinSemiring(2), "(p,q)", "a", "s");
		
		Semiring r = fa.getWeight(new Transition("(p,q)", "a", "q"));
		Assert.assertNotNull(r);
		Assert.assertTrue(((MinSemiring) r).v.intValue() == 1);
	}
}

package de.tum.in.wpds.test;


import java.util.HashMap;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import de.tum.in.wpds.Config;
import de.tum.in.wpds.Pds;
import de.tum.in.wpds.Rule;


public class PdsTest {

	@Test public void testLeftMapper() {
		
		Pds pds = new Pds();
		pds.add(new MinSemiring(1), "p", "a", "q", "b", "c");
		pds.add(new MinSemiring(2), "p", "a", "r", "d");
		pds.add(new MinSemiring(3), "q", "b", "r");
		System.out.println(pds);
		
		HashMap<Config, Set<Rule>> map = pds.getLeftMapper();
		Set<Rule> set = map.get(new Config("p", "a"));
		Assert.assertNotNull(set);
		Assert.assertTrue(set.size() == 2);
		System.out.println(set);
	}
}

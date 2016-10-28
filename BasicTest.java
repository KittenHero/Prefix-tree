import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class BasicTest {
	
	@Test
	public void testPut() {
		PrefixMap t = new PrefixTree("ACTG");
		
		assertNull(t.put("ACTG", "foo"));
		assertEquals("foo", t.put("ACTG", "bar"));
		
		assertNull(t.put("A", "for effort"));
	}
	
	@Test
	public void testSizeAndKeySum() {
		PrefixMap t = new PrefixTree("ACTG");
		assertEquals(0, t.sumKeyLengths());
		assertEquals(0, t.size());
		
		assertNull(t.put("ACTG", "foo"));
		assertEquals(4, t.sumKeyLengths());
		assertEquals(1, t.size());
		
		
		assertNull(t.put("ATCC", "bar"));
		assertEquals(8, t.sumKeyLengths());
		assertEquals(2, t.size());
		
		assertNull(t.remove("ATC"));
		assertEquals(8, t.sumKeyLengths());
		assertEquals(2, t.size());
		
		assertNull(t.get("ATC"));
		assertEquals(8, t.sumKeyLengths());
		assertEquals(2, t.size());
		
		assertNull(t.put("ACACACACACACTGGT", "baz"));
		assertEquals(24, t.sumKeyLengths());
		assertEquals(3, t.size());
		
		assertEquals("foo", t.remove("ACTG"));
		assertEquals(20, t.sumKeyLengths());
		assertEquals(2, t.size());
	}
	
	@Test
	public void testRemove() {
		PrefixMap t = new PrefixTree("ACTG");
		assertNull(t.put("ACTG", "foo"));
		assertNull(t.put("ATCC", "bar"));
		assertNull(t.put("ACACACACACACTGGT", "baz"));
		assertEquals(3, t.size());
		
		assertEquals("foo", t.remove("ACTG"));
		assertEquals("baz", t.remove("ACACACACACACTGGT"));
		assertEquals(4, t.countPrefixes());
		assertEquals("bar", t.remove("ATCC"));
		assertEquals(0, t.size());
		assertEquals(0, t.countPrefixes());
		assertEquals(0, t.sumKeyLengths());
		
		assertNull(t.remove("ATTATT"));
	}
	
	@Test
	public void testRemovePrefix() {
		PrefixMap t = new PrefixTree("ACTG");
		
		assertNull(t.put("ACCACCACCACC", "foo"));
		assertNull(t.put("ACC", "bar"));
		
		assertNull(t.remove("ACCACC"));
		assertEquals("foo", t.get("ACCACCACCACC"));
		
		assertEquals("bar", t.remove("ACC"));
		assertEquals("foo", t.get("ACCACCACCACC"));
	}
	
	@Test
	public void testCountKeysMatchingPrefix() {
		PrefixMap t = new PrefixTree("ACTG");
		assertEquals(0, t.countKeysMatchingPrefix("ACA"));
		
		assertNull(t.put("ACTG", "foo"));
		assertEquals(1, t.countKeysMatchingPrefix("ACT"));
		
		
		assertNull(t.put("ATCC", "bar"));
		assertEquals(1, t.countKeysMatchingPrefix("ACT"));
		assertEquals(2, t.countKeysMatchingPrefix("A"));
		
		assertNull(t.put("ACACACACACACTGGT", "baz"));
		assertEquals(1, t.countKeysMatchingPrefix("ACA"));
		assertEquals(2, t.countKeysMatchingPrefix("AC"));
		assertEquals(3, t.countKeysMatchingPrefix("A"));
	}

	@Test
	public void testGetKeysMatchingPrefix() {
		PrefixMap t = new PrefixTree("ACTG");
		assertEquals(Arrays.asList(), t.getKeysMatchingPrefix("ACA"));
		
		assertNull(t.put("ACTG", "foo"));
		assertEquals(sortedList("ACTG"), t.getKeysMatchingPrefix("ACT"));
		
		
		assertNull(t.put("ATCC", "bar"));
		assertEquals(sortedList("ACTG"), t.getKeysMatchingPrefix("ACT"));
		assertEquals(sortedList("ACTG", "ATCC"), sorted(t.getKeysMatchingPrefix("A")));
		
		assertNull(t.put("ACACACACACACTGGT", "baz"));
		assertEquals(sortedList("ACACACACACACTGGT"), t.getKeysMatchingPrefix("ACA"));
		assertEquals(sortedList("ACACACACACACTGGT", "ACTG"), sorted(t.getKeysMatchingPrefix("AC")));
		assertEquals(sortedList("ACACACACACACTGGT", "ACTG", "ATCC"), sorted(t.getKeysMatchingPrefix("A")));
	}

	@Test
	public void testCountPrefixes() {
		PrefixMap t = new PrefixTree("ACTG");
		assertEquals(0, t.countPrefixes());
		
		//----------------1234
		assertNull(t.put("ACTG", "foo"));
		assertEquals(4, t.countPrefixes());
		
		//-----------------567
		assertNull(t.put("ATCC", "bar"));
		assertEquals(7, t.countPrefixes());
		
		//------------------89012345678901
		assertNull(t.put("ACACACACACACTGGT", "baz"));
		assertEquals(21, t.countPrefixes());
		
		assertEquals("bar", t.remove("ATCC"));
		assertEquals(18, t.countPrefixes());
		
		assertEquals("baz", t.remove("ACACACACACACTGGT"));
		assertEquals(4, t.countPrefixes());
	}
	
	@Test
	public void testSelfPrefix() {
		PrefixMap t = new PrefixTree("ACTG");
		assertNull(t.put("ACTCA", "a palindrome emor dni lap a"));
		assertNull(t.put("ACTCACTCA", "reversible el bis r ever"));
		
		assertEquals(sortedList("ACTCA", "ACTCACTCA"), sorted(t.getKeysMatchingPrefix("ACTCA")));
		assertEquals(sortedList("ACTCACTCA"), sorted(t.getKeysMatchingPrefix("ACTCACTCA")));
	}
	
	
	@Test
	public void testEmptyString() {
		
		PrefixMap t = new PrefixTree("ACTG");
		
		assertNull(t.put("", "..."));
		assertNull(t.put("GATTACA", "who cares"));
		assertNull(t.put("ATAC"+"ACAT", "then attack a dog"));
		assertEquals(3, t.size());
		
		
		assertEquals(sortedList("", "GATTACA", "ATACACAT"), sorted(t.getKeysMatchingPrefix("")));
		assertEquals("...", t.remove(""));
		assertNull(t.get(""));
	}
	
	@Test
	public void testScope() {
		PrefixMap frankenstiens = new PrefixTree("ACTG");
		PrefixMap einstien = new PrefixTree("ACTG");
		
		assertNull(frankenstiens.put("A"+"CAT"+"ATTACCA", "is a monster"));
		assertEquals(1, frankenstiens.size());
		assertEquals(Arrays.asList(), einstien.getKeysMatchingPrefix("A"));
	}
	
	@Test
	public void testListNotModified() {
		PrefixMap t = new PrefixTree("ACTG");
		
		assertNull(t.put("ACTGATC", "goo"));
		
		List<String> aprefixed = t.getKeysMatchingPrefix("A");
		assertEquals(sortedList("ACTGATC"), aprefixed);
		aprefixed.clear();
		assertEquals(0, aprefixed.size());
		aprefixed = t.getKeysMatchingPrefix("A");
		assertEquals(sortedList("ACTGATC"), aprefixed);
	}
	
	@Test
	public void testSizeEquivalence() {
		PrefixMap t = new PrefixTree("ACTG");
		
		assertNull(t.put("", "fat cats sat in hats on mats"));
		assertNull(t.put("ATACAT", "a hat!"));
		assertNull(t.put("ACAT", "on a mat"));
		assertEquals(t.size(), t.countKeysMatchingPrefix(""));
		assertEquals(t.size(), t.getKeysMatchingPrefix("").size());
	}
	
	@Test
	public void testListOrdered() {
		PrefixMap t = new PrefixTree("ACTG");
		
		List<String> keys = Arrays.asList("", "TACC", "GCC", "CAT", "AGG", "C");
		for (String key : keys)
			assertNull(t.put(key, key));
		assertEquals(sorted(keys), t.getKeysMatchingPrefix(""));
	}
	
	private <K extends Comparable<K>> List<K> sorted(List<K> list) {
		Collections.sort(list);
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private <K extends Comparable<K>> List<K> sortedList(K... args) {
		return sorted((List<K>) Arrays.asList(args));
	}	
		
	@Test
	public void testInvalidInput() {
		PrefixMap t = new PrefixTree("ACTG");
		String e = "expected ",
				m = "MalformedKeyException",
				i = "IllegalArgumentException";
		try {
			t.get("KK");
			fail("Get(KK) : " + e + m);
		} catch (MalformedKeyException x) {	}
		try {
			t.get("AKK");
			fail("Get(AKK) : " + e + m);
		} catch (MalformedKeyException x) {	}
		try {
			t.get(null);
			fail("get(null) : " + e + i);
		} catch (IllegalArgumentException x) {	}
		
		
		
		try {
			t.put("K" , "foo");
			fail("Put(K, val) : " + e + m);
		} catch (MalformedKeyException x) {	}
		try {
			t.put("A", null);
			fail("Put(A, null) : " + e + i);
		} catch (IllegalArgumentException x) {}
		try {
			t.put(null, "bar");
			fail("Put(null, bar) : " + e + i);
		} catch (IllegalArgumentException x) {}
		try {
			t.put("K", null);
			fail("Put(K, null) : " + e + i + " or " + m);
		} catch (MalformedKeyException x) {}
		catch (IllegalArgumentException x) {}
		
		
		
		try {
			t.remove("BAZ");
			fail("Remove(BAZ) : " + e + m);
		} catch (MalformedKeyException x) {}
		try {
			t.remove("AZ");
			fail("Remove(AZ) : " + e + m);
		} catch (MalformedKeyException x) {}
		try {
			t.remove(null);
			fail("Remove(null) : " + e + i);
		} catch (IllegalArgumentException x) {}
		
		
		try {
			t.countKeysMatchingPrefix("FOO");
			fail("CountMatch(FOO) : " + e + m);
		} catch (MalformedKeyException x) {}
		try {
			t.countKeysMatchingPrefix("AZ");
			fail("CountMatch(AB) : " + e + m);
		} catch (MalformedKeyException x) {}
		try {
			t.countKeysMatchingPrefix(null);
			fail("CountMatch(null) : " + e + i);
		} catch (IllegalArgumentException x) {}
		
		
		try {
			t.getKeysMatchingPrefix("BAR");
			fail("GetMatch(BAR) : " + e + m);
		} catch (MalformedKeyException x) {}
		try {
			t.getKeysMatchingPrefix("ADD");
			fail("GetMatch(ADD) : " + e + m);
		} catch (MalformedKeyException x) {}
		try {
			t.getKeysMatchingPrefix(null);
			fail("GetMatch(null) : " + e + i);
		} catch (IllegalArgumentException x) {}
	}	
}

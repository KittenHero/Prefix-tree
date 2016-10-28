import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NonDeterministicTest {
	
  @Test
  public void bigTest() {
	  Random RNG = new Random();
	  StringBuilder sb = new StringBuilder();
	  String[] keys = new String[10000];
	  
	  for (int i = 0; i < 20; i++) {
		  sb.append(chooseBase(RNG));
	  }
	  
	  Lab11 checker = new Lab11();
	  Assignment tester = new Assignment();
	  
	  for (int i = 0; i < keys.length; i++) {
		  double num = RNG.nextDouble();
		  int len = sb.length();
		  if (num < 0.3) {
			  sb.delete(len - RNG.nextInt(len/2), len);
		  } else if (num < 0.7) {
			  sb.append(chooseBase(RNG));
		  } else if (num < 0.8) {
			  sb.reverse();
		  } else {
			  int ind = RNG.nextInt(len);
			  sb.replace(ind, ind + 1, chooseBase(RNG));
		  }
		  
		  keys[i] = sb.toString();
		  assertEquals(checker.put(keys[i], keys[i]), tester.put(keys[i], keys[i]));
	  }
	  
	  for (int i = 0; i < keys.length; i++) {
		  double num = RNG.nextDouble();
		  
		  if (num < 0.1) {
			  assertEquals(sorted(checker.getKeysMatchingPrefix(keys[i])),
					  sorted(tester.getKeysMatchingPrefix(keys[i])));
		  } else if (num < 0.2){
			  assertEquals(checker.countKeysMatchingPrefix(keys[i]),
					  tester.countKeysMatchingPrefix(keys[i]));
		  } else if (num < 0.6) {
			  assertEquals(checker.remove(keys[i]), tester.remove(keys[i]));
		  } else if (num < 0.7) {
			  assertEquals(checker.get(keys[i]), tester.get(keys[i]));
		  } else if (num < 0.8) {
			  assertEquals(checker.size(), tester.size());
		  } else if (num < 0.9) {
			  assertEquals(checker.countPrefixes(), tester.countPrefixes());
		  } else  {
			  assertEquals(checker.sumKeyLengths(), tester.sumKeyLengths());
		  }
	  }
  }
  
  static final String[] BASES = new String[] {"A", "C", "G", "T"};
  private static String chooseBase(Random r) {
	  return BASES[r.nextInt(BASES.length)];
  }
  
  private static <K extends Comparable<K>> List<K> sorted(List<K> list) {
	  Collections.sort(list);
	  return list;
  }
}

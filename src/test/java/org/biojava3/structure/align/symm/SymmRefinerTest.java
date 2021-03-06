/**
 * 
 */
package org.biojava3.structure.align.symm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.biojava.bio.structure.StructureException;
import org.biojava.bio.structure.align.util.AlignmentTools;

import junit.framework.TestCase;

/**
 * @author blivens
 *
 */
@SuppressWarnings("deprecation")
public class SymmRefinerTest extends TestCase {
	List<Map<Integer,Integer>> alignments;
	List<Map<Integer,Integer>> expecteds;
	
	protected void setUp() throws Exception {
		super.setUp();

		Map<Integer,Integer> align;
		Map<Integer,Integer> expect;

		// Note that all the expecteds rely on scoreAbsError being used internally
		// by refineSymmetry
		
		alignments = new ArrayList<Map<Integer,Integer>>();
		expecteds = new ArrayList<Map<Integer,Integer>>();
		
		/* 0. Chained alignment
		 * 1\
		 * 2>6>10<--
		 * 3>7>11</
		 * 4>8>12</
		 * 5>9>13<--
		 */
		align = new HashMap<Integer,Integer>();
		align.put(	1,	6);
		align.put(	2,	6);
		align.put(	3,	7);
		align.put(	4,	8);
		align.put(	5,	9);
		align.put(	6,	10);
		align.put(	7,	11);
		align.put(	8,	12);
		align.put(	9,	13);
		align.put(	10,	2);
		align.put(	11,	4);
		align.put(	12,	5);
		align.put(	13,	5);
		alignments.add(align);
		/* 0. Expect
		 * 2>6>10<--
		 * 3>7>11<--
		 * 4>8>12<--
		 * 5>9>13<--
		 */
		expect = new HashMap<Integer,Integer>();
		expect.put(	2,	6);
		expect.put(	3,	7);
		expect.put(	4,	8);
		expect.put(	5,	9);
		expect.put(	6,	10);
		expect.put(	7,	11);
		expect.put(	8,	12);
		expect.put(	9,	13);
		expect.put(	10,	2);
		expect.put(	11,	3);
		expect.put(	12,	4);
		expect.put(	13,	5);
		expecteds.add( expect);
		
		
		/* 1. Entering ring
		 * 1->5->9<--
		 *    4->8<\
		 */
		align = new HashMap<Integer,Integer>();
		align.put(	1,	5);
		align.put(	5,	9);
		align.put(	9,	1);
		align.put(	4,	8);
		align.put(	8,	1);
		alignments.add(align);
		/* 1. Expect
		 * 1->5->9<--
		 */
		expect = new HashMap<Integer,Integer>();
		expect.put(	1,	5);
		expect.put(	5,	9);
		expect.put(	9,	1);
		expecteds.add(align);
		
		/* 2. independent chain, lowest res
		 * 1->5->9<--
		 * 2->6->10<\
		 */
		align = new HashMap<Integer,Integer>();
		align.put(	1,	5);
		align.put(	5,	9);
		align.put(	9,	1);
		align.put(	2,	6);
		align.put(	6,	10);
		align.put(	10,	1);
		alignments.add(align);
		/* 2. Expect
		 * 1->5->9<--
		 * 2->6->10<--
		 */
		expect = new HashMap<Integer,Integer>();
		expect.put(	1,	5);
		expect.put(	5,	9);
		expect.put(	9,	1);
		expect.put(	2,	6);
		expect.put(	6,	10);
		expect.put(	10,	2);
		expecteds.add(align);
		
		/* 3. independent chain, not lowest res
		 * 1->5->9<--
		 * 2/^6->10<--
		 */
		align = new HashMap<Integer,Integer>();
		align.put(	1,	5);
		align.put(	5,	9);
		align.put(	9,	1);
		align.put(	2,	5);
		align.put(	6,	10);
		align.put(	10,	2);
		alignments.add(align);
		/* 3. Expect
		 * 1->5->9<--
		 * 2->6->10<--
		 */
		expect = new HashMap<Integer,Integer>();
		expect.put(	1,	5);
		expect.put(	5,	9);
		expect.put(	9,	1);
		expect.put(	2,	6);
		expect.put(	6,	10);
		expect.put(	10,	2);
		expecteds.add(align);
		
		/* 4. Wrong order
		 * 3->7<--
		 */
		align = new HashMap<Integer,Integer>();
		align.put(	3,	7);
		align.put(	7,	3);
		alignments.add(align);
		/* 4. Expect
		 */
		expect = new HashMap<Integer,Integer>();
		expecteds.add(align);
		
		/* 5. Prioritize score over chain length
		 * 1->5->9</
		 * 3->6->10
		 */
		align = new HashMap<Integer,Integer>();
		align.put(	1,	5);
		align.put(	5,	9);
		align.put(	9,	3);
		align.put(	3,	6);
		align.put(	6,	10);
		alignments.add(align);
		/* 5. Expect
		 * 3->5->9<--
		 */
		expect = new HashMap<Integer,Integer>();
		expect.put(	3,	5);
		expect.put(	5,	9);
		expect.put(	9,	3);
		expecteds.add(align);
		
		assertEquals("Error with setup",alignments.size(),expecteds.size());
	}
	

	public void testExpected() throws StructureException {
		
		for(int i=0;i<alignments.size(); i++) {
			Map<Integer,Integer> align = alignments.get(i);
			Map<Integer,Integer> expect = expecteds.get(i);

			Map<Integer,Integer> refined = SymmRefiner.refineSymmetry(align,3);
			assertEquals("Alignment "+i+" refinement wrong",expect, refined);
		}
	}
	
	/**
	 * Refined alignments should be automorphic (one-to-one)
	 * @param align
	 */
	private void testAutomorphism(Map<Integer,Integer> align) {
		for(Integer pre:align.keySet()) {
			Integer post = align.get(pre);
			assertNotNull("Not automorphic: f("+pre+") not defined",post);
			assertTrue("not automorphic: f("+pre+") = "+post+" but f("+post+") undefined", align.containsKey(post));
		}
	}
	
	public void testAutomorphism() throws StructureException {
		for(int i=0;i<alignments.size(); i++) {
			Map<Integer,Integer> align = alignments.get(i);

			Map<Integer,Integer> refined = SymmRefiner.refineSymmetry(align,3);
			testAutomorphism(refined);
		}
	}

	private void testSymmetric(Map<Integer,Integer> align, int k) {
		Map<Integer,Integer> alignK = AlignmentTools.applyAlignment(align, k);
		assertEquals(align.size(),alignK.size()); // Assumption; Should be tested by AlignmentToolsTest
		
		for(Integer res : alignK.keySet()) {
			Integer resK = alignK.get(res);
			assertEquals(String.format("Asymmetric alignment. f^%d(%d)=%d",k,res,resK),res,resK);
		}	
	}
	
	public void testSymmetric() throws StructureException {
		for(int i=0;i<alignments.size(); i++) {
			Map<Integer,Integer> align = alignments.get(i);

			Map<Integer,Integer> refined = SymmRefiner.refineSymmetry(align,3);
			testSymmetric(refined,3);
		}
	}
	
	/* Didn't end up being a useful function, so never implemented.

	public void testPartitionAlignment() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		// test private partitionAlignment method
		Method partitionAlignment = SymmRefiner.class.getDeclaredMethod("partitionAlignment", new Class[] {Map.class});
		partitionAlignment.setAccessible(true);
		
		Map<Integer,Integer> alignment = new HashMap<Integer, Integer>();
		alignment.put(2, 11);
		alignment.put(3, 12);
		alignment.put(4, 13);
		alignment.put(6, 14);
		alignment.put(7, 15);
		alignment.put(8, 16);
		alignment.put(10, 18);
		alignment.put(12, 20);
		alignment.put(13, 21);
		alignment.put(15, 22);
		alignment.put(16, 23);
		alignment.put(17, 24);
		alignment.put(18, 2);
		alignment.put(20, 3);
		alignment.put(21, 4);
		alignment.put(22, 6);
		alignment.put(23, 8);
		alignment.put(24, 10);
		
		List<Map<Integer,Integer>> expected = new ArrayList<Map<Integer,Integer>>();
		//Note: should be insensitive to order (like a set), but that's harder to compare
		Map<Integer,Integer> expectedAlign = new HashMap<Integer, Integer>();
		expectedAlign.put(2, 11);
		expectedAlign.put(10, 18);
		expectedAlign.put(17, 24);
		expectedAlign.put(18, 2);
		expectedAlign.put(24, 10);
		expected.add(expectedAlign);
		
		expectedAlign = new HashMap<Integer, Integer>();
		expectedAlign.put(6, 14);
		expectedAlign.put(7, 15);
		expectedAlign.put(15, 22);
		expectedAlign.put(22, 6);
		expected.add(expectedAlign);


		List<Map<Integer,Integer>> result;
		
		result = (List<Map<Integer, Integer>>) partitionAlignment.invoke(null, expectedAlign);
		
		assertEquals("Expected number of partitions differs.",expected.size(), result.size());
		
		Iterator<Map<Integer, Integer>> expIt = expected.iterator();
		Iterator<Map<Integer, Integer>> resultIt = result.iterator();
		while(expIt.hasNext()) {
			Map<Integer, Integer> exp = expIt.next();
			Map<Integer, Integer> res = resultIt.next();
			
			assertEquals("Mapping differs.",exp,res);
		}
		
	}
	*/
	
}

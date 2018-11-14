package architecture;


import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.TestCase;

public class PurposeHierarchyTest extends TestCase {

	PurposeHierarchy MyPurposeHierarchy;

	@BeforeClass public void setUp() throws Exception {
		MyPurposeHierarchy = new PurposeHierarchy();
		assertEquals( "The purposes should include exactly two elements.",
				2, MyPurposeHierarchy.getPurposes().size() );
	}

	@AfterClass public void tearDown() throws Exception {
		MyPurposeHierarchy = null;
	}
	
	@Test public void testNonEmpty() throws Exception {
		Purpose p1 = new Purpose("Test1");
		Purpose p2 = new Purpose("Test2");
		boolean [][] am = {{false, false}, {true, false}};
		MyPurposeHierarchy = new PurposeHierarchy(List.of(p1, p2), am);
		assertEquals( "The purpose hierarchy should contain 4 purposes.",
				4, MyPurposeHierarchy.getPurposes().size() );
	}
	
	@Test public void testNonEmptyComplex() throws Exception {
		Purpose p1 = new Purpose("Test1");
		Purpose p2 = new Purpose("Test2");
		boolean [][] am = {{false, true}, {false, false}};
		MyPurposeHierarchy = new PurposeHierarchy(List.of(p1, p2), am);
		assertEquals( "The purpose hierarchy have links from top to p1 to p2 to bot.",
				true, MyPurposeHierarchy.getAM()[2][0] && MyPurposeHierarchy.getAM()[0][1] && MyPurposeHierarchy.getAM()[1][3] );
	}
	
	@Test public void testAddSinglePurpose() throws Exception {
		Purpose a = new Purpose("a", Collections.emptySet());
		MyPurposeHierarchy.addPurpose(a, Collections.emptySet(), Collections.emptySet());
		assertTrue("There should be edges between a and top/bottom.", 
				MyPurposeHierarchy.getAM()[0][2] && MyPurposeHierarchy.getAM()[2][1] );
	}

	@Test public void testAddPurposeSimple() throws Exception {
		Purpose a = new Purpose("a", Collections.emptySet());
		Purpose b = new Purpose("b", Collections.emptySet());
		MyPurposeHierarchy.addPurpose(a, Collections.emptySet(), Collections.emptySet());
		MyPurposeHierarchy.addPurpose(b, Collections.singleton(a), Collections.emptySet());
		assertTrue("There should be an edge between a and b.", 
				MyPurposeHierarchy.getAM()[2][3]);
	}

	@Test public void testAddPurposeComplex() throws Exception {
		Purpose a = new Purpose("a", Collections.emptySet());
		Purpose b = new Purpose("b", Collections.emptySet());
		Purpose c = new Purpose("c", Collections.emptySet());
		MyPurposeHierarchy.addPurpose(a, Collections.emptySet(), Collections.emptySet());
		MyPurposeHierarchy.addPurpose(b, Collections.singleton(a), Collections.emptySet());
		MyPurposeHierarchy.addPurpose(c, Collections.singleton(a), Collections.singleton(b));
		assertTrue("C should have edges to a and b.", 
				MyPurposeHierarchy.getAM()[2][4] && MyPurposeHierarchy.getAM()[4][3]);
		assertTrue("The edge between a and b should be deleted.", 
				!MyPurposeHierarchy.getAM()[2][3] );
	}

	@Test public void testIsChildSimple() throws Exception {
		Purpose a = new Purpose("a", Collections.emptySet());
		Purpose b = new Purpose("b", Collections.emptySet());
		MyPurposeHierarchy.addPurpose(a, Collections.emptySet(), Collections.emptySet());
		MyPurposeHierarchy.addPurpose(b, Collections.singleton(a), Collections.emptySet());
		assertTrue("b should be child of a.", 
				MyPurposeHierarchy.compare(b, a));
		assertTrue("a should not be the child of b.", 
				!MyPurposeHierarchy.compare(a, b));
	}
	
	@Test public void testIsChildComplex() throws Exception {
		Purpose a = new Purpose("a", Collections.emptySet());
		Purpose b = new Purpose("b", Collections.emptySet());
		Purpose c = new Purpose("c", Collections.emptySet());
		MyPurposeHierarchy.addPurpose(a, Collections.emptySet(), Collections.emptySet());
		MyPurposeHierarchy.addPurpose(b, Collections.singleton(a), Collections.emptySet());
		MyPurposeHierarchy.addPurpose(c, Collections.singleton(a), Collections.singleton(b));
		assertTrue("b should be child of a.", 
				MyPurposeHierarchy.compare(b, a));
	}

}

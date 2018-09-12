package architecture;


import java.util.Collections;

import junit.framework.TestCase;

public class PurposeHierarchyTest extends TestCase {

	PurposeHierarchy MyPurposeHierarchy;

	@Override public void setUp() throws Exception {
		MyPurposeHierarchy = new PurposeHierarchy();
		assertEquals( "The purposes should include exactly two elements.",
				2, MyPurposeHierarchy.getPurposes().size() );
	}

	@Override public void tearDown() throws Exception {
		MyPurposeHierarchy = null;
	}

	public void testAddPurposeSimple() throws Exception {
		Purpose a = new Purpose("a", Collections.emptySet());
		Purpose b = new Purpose("b", Collections.emptySet());
		MyPurposeHierarchy.addPurpose(a, Collections.emptySet(), Collections.emptySet());
		MyPurposeHierarchy.addPurpose(b, Collections.singleton(a), Collections.emptySet());
		assertTrue("There should be an edge between a and b.", 
				MyPurposeHierarchy.getAM()[2][3]);
	}

	public void testAddPurposeComplex() throws Exception {
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

	public void testIsChildSimple() throws Exception {
		Purpose a = new Purpose("a", Collections.emptySet());
		Purpose b = new Purpose("b", Collections.emptySet());
		MyPurposeHierarchy.addPurpose(a, Collections.emptySet(), Collections.emptySet());
		MyPurposeHierarchy.addPurpose(b, Collections.singleton(a), Collections.emptySet());
		assertTrue("B should be child of a.", 
				MyPurposeHierarchy.compare(b, a));
		assertTrue("A should not be the child of b.", 
				!MyPurposeHierarchy.compare(a, b));
	}
	
	public void testIsChildComplex() throws Exception {
		Purpose a = new Purpose("a", Collections.emptySet());
		Purpose b = new Purpose("b", Collections.emptySet());
		Purpose c = new Purpose("c", Collections.emptySet());
		MyPurposeHierarchy.addPurpose(a, Collections.emptySet(), Collections.emptySet());
		MyPurposeHierarchy.addPurpose(b, Collections.singleton(a), Collections.emptySet());
		MyPurposeHierarchy.addPurpose(c, Collections.singleton(a), Collections.singleton(b));
		assertTrue("B should be child of a.", 
				MyPurposeHierarchy.compare(b, a));
	}

}

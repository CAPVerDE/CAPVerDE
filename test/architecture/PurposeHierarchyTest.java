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
	
	public void testAddPurpose() throws Exception {
		Purpose a = new Purpose("a", Collections.emptySet());
		Purpose b = new Purpose("b", Collections.emptySet());
		MyPurposeHierarchy.addPurpose(a, Collections.emptySet(), Collections.emptySet());
		MyPurposeHierarchy.addPurpose(b, Collections.singleton(a), Collections.emptySet());
		assertTrue("There should be an edge between a and b.", 
				MyPurposeHierarchy.getAM()[2][3]);
	}

}

package architecture;

import java.util.Collections;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import architecture.Action.ActionType;
import junit.framework.TestCase;
import properties.Property;
import properties.Property.PropertyType;
import properties.RulesOfInferenceParserBottomup;

public class ParserTest extends TestCase {
	
	RulesOfInferenceParserBottomup parser;
	Architecture arch;
	Component comp1;
	Component comp2;
	Component comp3;
	Variable var1;
	Variable var2;
	
	@BeforeClass public void setUp() throws Exception {
		comp1 = new Component("c1");
		comp2 = new Component("c2");
		comp3 = new Component("c3");
		arch = new Architecture(Set.of(comp1, comp2, comp3));
		parser = new RulesOfInferenceParserBottomup(arch);
		assertTrue( "There is nothing to assert", true );
	}
	
	@AfterClass public void tearDown() throws Exception {
		parser = null;
	}
	
	@Test
	public void testPurposeSimple() {
		var1 = new Variable("x");
		arch.addVariable(var1);
		Purpose purp1 = new Purpose("general");
		arch.getPurposeHierarchy().addPurpose(purp1, Collections.emptySet(), Collections.emptySet());
		Action pr1 = new Action(ActionType.PRECEIVE, comp1, comp2, purp1, Collections.singleton(var1));
		arch.addAction(pr1);
		Property prop = new Property(PropertyType.NOTPURP, comp1);
		assertTrue( "The property notPurp_comp1 should hold because the purpose does not contain x.",
				parser.verifyStatement(prop, 0) );
	}
	
	@Test
	public void testPurposeSimple2() {
		var1 = new Variable("x");
		var2 = new Variable("y");
		arch.addVariable(var1);
		arch.addVariable(var2);
		Purpose purp1 = new Purpose("general", Set.of(var2));
		arch.getPurposeHierarchy().addPurpose(purp1, Collections.emptySet(), Collections.emptySet());
		Action pr1 = new Action(ActionType.PRECEIVE, comp1, comp2, purp1, Collections.singleton(var1));
		arch.addAction(pr1);
		Property prop = new Property(PropertyType.NOTPURP, comp1);
		assertTrue( "The property notPurp_comp1 should hold because the variables do not match.",
				parser.verifyStatement(prop, 0) );
	}
	
	@Test
	public void testPurposeSame() {
		var1 = new Variable("x");
		var2 = new Variable("y");
		arch.addVariable(var1);
		arch.addVariable(var2);
		Purpose purp1 = new Purpose("general", Set.of(var1, var2));
		Purpose purp2 = new Purpose("specific", Set.of(var1));
		arch.getPurposeHierarchy().addPurpose(purp1, Collections.emptySet(), Collections.emptySet());
		arch.getPurposeHierarchy().addPurpose(purp2, Collections.singleton(purp1), Collections.emptySet());
		Action pr1 = new Action(ActionType.PRECEIVE, comp1, comp2, purp1, Collections.singleton(var1));
		Action pr2 = new Action(ActionType.PRECEIVE, comp3, comp1, purp1, Collections.singleton(var1));
		arch.addAction(pr1);
		arch.addAction(pr2);
		Property prop = new Property(PropertyType.NOTPURP, comp1);
		assertTrue( "The property notPurp_comp1 should not hold because the purpose is the same.",
				!parser.verifyStatement(prop, 0) );
	}

	@Test
	public void testPurposeCompatible() {
		var1 = new Variable("x");
		var2 = new Variable("y");
		arch.addVariable(var1);
		arch.addVariable(var2);
		Purpose purp1 = new Purpose("general", Set.of(var1, var2));
		Purpose purp2 = new Purpose("specific", Set.of(var1));
		arch.getPurposeHierarchy().addPurpose(purp1, Collections.emptySet(), Collections.emptySet());
		arch.getPurposeHierarchy().addPurpose(purp2, Collections.singleton(purp1), Collections.emptySet());
		Action pr1 = new Action(ActionType.PRECEIVE, comp1, comp2, purp1, Collections.singleton(var1));
		Action pr2 = new Action(ActionType.PRECEIVE, comp3, comp1, purp2, Collections.singleton(var1));
		arch.addAction(pr1);
		arch.addAction(pr2);
		Property prop = new Property(PropertyType.NOTPURP, comp1);
		assertTrue( "The property notPurp_comp1 should not hold because the specific purpose is derived from the general one.",
				!parser.verifyStatement(prop, 0) );
		//TODO test
	}
	
	@Test
	public void testPurposeIncompatible() {
		var1 = new Variable("x");
		var2 = new Variable("y");
		arch.addVariable(var1);
		arch.addVariable(var2);
		Purpose purp1 = new Purpose("general", Set.of(var1, var2));
		Purpose purp2 = new Purpose("specific", Set.of(var1));
		arch.getPurposeHierarchy().addPurpose(purp1, Collections.emptySet(), Collections.emptySet());
		arch.getPurposeHierarchy().addPurpose(purp2, Collections.emptySet(), Collections.emptySet());
		Action pr1 = new Action(ActionType.PRECEIVE, comp1, comp2, purp1, Collections.singleton(var1));
		Action pr2 = new Action(ActionType.PRECEIVE, comp3, comp1, purp2, Collections.singleton(var1));
		arch.addAction(pr1);
		arch.addAction(pr2);
		Property prop = new Property(PropertyType.NOTPURP, comp1);
		assertTrue( "The property notPurp_comp1 should hold because the two purposes are not related.",
				parser.verifyStatement(prop, 0) );
		//TODO test
	}

	@Test
	public void testPurposeIncompatibleWithDep() {
		var1 = new Variable("x");
		var2 = new Variable("y");
		arch.addVariable(var1);
		arch.addVariable(var2);
		comp1.addDependence(new Dep(var2, Set.of(var1), 1));
		Purpose purp1 = new Purpose("general", Set.of(var1));
		Purpose purp2 = new Purpose("specific", Set.of(var2));
		arch.getPurposeHierarchy().addPurpose(purp1, Collections.emptySet(), Collections.emptySet());
		arch.getPurposeHierarchy().addPurpose(purp2, Collections.emptySet(), Collections.emptySet());
		Action pr1 = new Action(ActionType.PRECEIVE, comp1, comp2, purp1, Collections.singleton(var1));
		Action pr2 = new Action(ActionType.PRECEIVE, comp3, comp1, purp2, Collections.singleton(var2));
		arch.addAction(pr1);
		arch.addAction(pr2);
		Property prop = new Property(PropertyType.NOTPURP, comp1);
		assertTrue( "The property notPurp_comp1 should hold because the variable y can be deduced from x.",
				parser.verifyStatement(prop, 0) );
		//TODO test
	}
	
	@Test
	public void testPurposeIncompatibleHierarchy() {
		var1 = new Variable("x");
		arch.addVariable(var1);
		Purpose purp1 = new Purpose("general", Set.of(var1));
		Purpose purp2 = new Purpose("specific", Set.of(var1));
		arch.getPurposeHierarchy().addPurpose(purp1, Collections.emptySet(), Collections.emptySet());
		arch.getPurposeHierarchy().addPurpose(purp2, Set.of(purp1), Collections.emptySet());
		Action pr1 = new Action(ActionType.PRECEIVE, comp1, comp2, purp2, Collections.singleton(var1));
		Action pr2 = new Action(ActionType.PRECEIVE, comp3, comp1, purp1, Collections.singleton(var1));
		arch.addAction(pr1);
		arch.addAction(pr2);
		Property prop = new Property(PropertyType.NOTPURP, comp1);
		assertTrue( "The property notPurp_comp1 should hold because second purpose is more general than the first one.",
				parser.verifyStatement(prop, 0) );
		//TODO test
	}
	
	@Test
	public void testPurposeTransitiveDep() {
		var1 = new Variable("x");
		var2 = new Variable("y");
		Variable var3 = new Variable("z");
		arch.addVariable(var1);
		arch.addVariable(var2);
		arch.addVariable(var3);
		comp1.addDependence(new Dep(var2, Set.of(var1), 1));
		comp1.addDependence(new Dep(var3, Set.of(var2), 1));
		Purpose purp1 = new Purpose("general", Set.of(var1));
		Purpose purp2 = new Purpose("specific", Set.of(var3));
		arch.getPurposeHierarchy().addPurpose(purp1, Collections.emptySet(), Collections.emptySet());
		arch.getPurposeHierarchy().addPurpose(purp2, Collections.emptySet(), Collections.emptySet());
		Action pr1 = new Action(ActionType.PRECEIVE, comp1, comp2, purp1, Collections.singleton(var1));
		Action pr2 = new Action(ActionType.PRECEIVE, comp3, comp1, purp2, Collections.singleton(var3));
		arch.addAction(pr1);
		arch.addAction(pr2);
		Property prop = new Property(PropertyType.NOTPURP, comp1);
		assertTrue( "The property notPurp_comp1 should hold because second purpose is more general than the first one.",
				parser.verifyStatement(prop, 0) );
		//TODO test
	}
	
	@Test
	public void testPurposeTransitiveDepComplex() {
		var1 = new Variable("x");
		var2 = new Variable("y");
		Variable var3 = new Variable("z");
		Variable var4 = new Variable("w");
		arch.addVariable(var1);
		arch.addVariable(var2);
		arch.addVariable(var3);
		comp1.addDependence(new Dep(var1, Set.of(var2), 1));
		comp1.addDependence(new Dep(var4, Set.of(var1), 1));
		comp1.addDependence(new Dep(var2, Set.of(var1), 1));
		comp1.addDependence(new Dep(var3, Set.of(var2), 1));
		Purpose purp1 = new Purpose("general", Set.of(var1));
		Purpose purp2 = new Purpose("specific", Set.of(var3));
		arch.getPurposeHierarchy().addPurpose(purp1, Collections.emptySet(), Collections.emptySet());
		arch.getPurposeHierarchy().addPurpose(purp2, Collections.emptySet(), Collections.emptySet());
		Action pr1 = new Action(ActionType.PRECEIVE, comp1, comp2, purp1, Collections.singleton(var1));
		Action pr2 = new Action(ActionType.PRECEIVE, comp3, comp1, purp2, Collections.singleton(var3));
		arch.addAction(pr1);
		arch.addAction(pr2);
		Property prop = new Property(PropertyType.NOTPURP, comp1);
		assertTrue( "The property notPurp_comp1 should hold because second purpose is more general than the first one.",
				parser.verifyStatement(prop, 0) );
	}
	
	@Test
	public void testConsentViolationSimple() {
		var1 = new Variable("x");
		var2 = new Variable("y");
		arch.addVariable(var1);
		arch.addVariable(var2);
		DataType dt = new DataType("dt", Set.of(var1, var2));
		Action a1 = new Action(ActionType.RECEIVE, comp1, comp2, Collections.emptySet(), Collections.singleton(var1));
		arch.addAction(a1);
		Property prop = new Property(PropertyType.CONSENTVIOLATED, comp2, dt);
		assertTrue( "The property consentViolated_comp2(dt) should hold because there is no permission for the data type.",
				parser.verifyStatement(prop, 0) );
	}
	
	@Test
	public void testConsentViolationPermission() {
		var1 = new Variable("x");
		var2 = new Variable("y");
		arch.addVariable(var1);
		arch.addVariable(var2);
		DataType dt = new DataType("dt", Set.of(var1, var2));
		Action a1 = new Action(ActionType.RECEIVE, comp1, comp2, Collections.emptySet(), Collections.singleton(var1));
		Action a2 = new Action(ActionType.PERMISSION, comp3, comp2, dt);
		arch.addAction(a1);
		arch.addAction(a2);
		Property prop = new Property(PropertyType.CONSENTVIOLATED, comp2, dt);
		assertTrue( "The property consentViolated_comp2(dt) should not hold because there is a permission for the data type.",
				!parser.verifyStatement(prop, 0) );
	}
	
	@Test
	public void testConsentViolationRevoke() {
		var1 = new Variable("x");
		var2 = new Variable("y");
		arch.addVariable(var1);
		arch.addVariable(var2);
		DataType dt = new DataType("dt", Set.of(var1, var2));
		Action a1 = new Action(ActionType.RECEIVE, comp1, comp2, Collections.emptySet(), Collections.singleton(var1));
		Action a2 = new Action(ActionType.REVOKE, comp3, comp2, dt);
		Action a3 = new Action(ActionType.PERMISSION, comp3, comp2, dt);
		arch.addAction(a1);
		arch.addAction(a2);
		arch.addAction(a3);
		Property prop = new Property(PropertyType.CONSENTVIOLATED, comp2, dt);
		assertTrue( "The property consentViolated_comp2(dt) should hold because there is a revoke for the data type.",
				parser.verifyStatement(prop, 0) );
	}
	
	@Test
	public void testConsentViolationNoDT() {
		var1 = new Variable("x");
		var2 = new Variable("y");
		arch.addVariable(var1);
		arch.addVariable(var2);
		DataType dt = new DataType("dt", Set.of(var2));
		Action a1 = new Action(ActionType.RECEIVE, comp1, comp2, Collections.emptySet(), Collections.singleton(var1));
		Action a2 = new Action(ActionType.REVOKE, comp3, comp2, dt);
		Action a3 = new Action(ActionType.PERMISSION, comp3, comp2, dt);
		arch.addAction(a1);
		arch.addAction(a2);
		arch.addAction(a3);
		Property prop = new Property(PropertyType.CONSENTVIOLATED, comp2, dt);
		assertTrue( "The property consentViolated_comp2(dt) should not hold because the variable is not contained in the data type.",
				!parser.verifyStatement(prop, 0) );
	}
}

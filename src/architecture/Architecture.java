package architecture;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import architecture.Action.ActionType;
import properties.Property;

/**
 * Objects describing an architecture as a whole with all its components,
 * relations properties, and so on.
 */
public class Architecture implements Serializable {

	/**
	 * @serial Serial ID for storing architecture objects in files.
	 */
	private static final long serialVersionUID = -4831058795686423966L;

	// class fields
	private List<Component> compList;
	private List<Action> interCompActions;
	private List<Action> allActions;
	private List<Trust> trusts;
	private List<Property> allProperties;
	private List<Variable> allVariables;
	private List<DataType> allDataTypes;
	private List<Equation> allEquations;
	private List<Statement> allStatements;
	private List<Composition> compositions;
	private PurposeHierarchy purpHier;

	/**
	 * The full Constructor of an architecture that is typically only invoked for
	 * complete architecture objects or by the nearly empty constructor
	 * Architecture(Set) in this class.
	 * 
	 * @param compList
	 *          the list of components
	 * @param interCompActions
	 *          the interactions between two components, e.g. Receive
	 * @param trusts
	 *          a list of trust relations between the components
	 */
	public Architecture(Set<Component> compList, Set<Action> interCompActions,
			Set<Trust> trusts, Set<Composition> compositions, PurposeHierarchy purpHier) {
		this.compList = new ArrayList<Component>(compList);
		this.interCompActions = new ArrayList<Action>(interCompActions);
		this.purpHier = purpHier;
		if (trusts == null) {
			this.trusts = new ArrayList<Trust>();
		} else {
			this.trusts = new ArrayList<Trust>(trusts);
		}
		if (compositions == null) {
			this.compositions = new ArrayList<Composition>();
		} else {
			this.compositions = new ArrayList<Composition>(compositions);
		}
		allProperties = new ArrayList<Property>();
		allVariables = new ArrayList<Variable>();
		allDataTypes = new ArrayList<DataType>();
		allEquations = new ArrayList<Equation>();
		allStatements = new ArrayList<Statement>();
		// Collect a list of all actions in the architecture
		allActions = new ArrayList<Action>();
		collectActions();
		// Collect lists of all variables and equations in the architecture
		collectVariables();
		collectDataTypes();
		collectSimpleEquations();
		// also update the counter for inter-component-actions
		makeCounter();
		updateCompositions();
	}

	/**
	 * The Constructor called for nearly empty architecture with at least one
	 * component.
	 * 
	 * @param compList
	 *          the list of components
	 */
	public Architecture(Set<Component> compList) {
		this(compList, new LinkedHashSet<Action>(), new LinkedHashSet<Trust>(), new LinkedHashSet<Composition>(), new PurposeHierarchy());
	}

	private void makeCounter() {
		// TODO test this!
		for (Component comp : compList) {
			for (Variable var : allVariables) {
				int count = 0;
				int maxCount = 0;
				for (Action a : allActions) {
					if (a.getComponent().equals(comp)) {
						switch (a.getAction()) {
						case CHECK:
							for (Equation e : a.getEqSet()) {
								if (e.getAtoms().contains(var)) {
									count++;
								}
							}
							break;
						case COMPUTE:
							if (a.getEq().getAtoms().contains(var)) {
								count++;
							}
							break;
						case DELETE:
							if (a.getVar().equals(var)) {
								count = 0;
							}
							break;
						default:
							break;
						}
					} else if ((a.getAction() == ActionType.RECEIVE || a.getAction() == ActionType.PRECEIVE || a.getAction() == ActionType.CRECEIVE) && a.getComPartner().equals(comp)) {
						// also consider the receives
						if (a.getVarSet().contains(var)) {
							count++;
						}
					}
					if (count > maxCount) {
						maxCount = count;
					}
				}
				// update the counter
				comp.setCounter(var, maxCount);
			}
		}
	}

	/**
	 * Method to check if a component trusts the other component.
	 * 
	 * @param comp1
	 *          the trusting component
	 * @param comp2
	 *          the trusted component
	 * @return true, if there exists such a trust relation
	 */
	public boolean trust(Component comp1, Component comp2) {
		boolean trusted = false;
		for (Trust trust : trusts) {
			if (trust.isEqual(comp1, comp2, Collections.emptySet())) {
				trusted = true;
				break;
			}
		}
		return trusted;
	}

	private void collectSimpleEquations() {
		for (Action a : allActions) {
			switch (a.getAction()) {
			case CHECK:
				// fall through
			case COMPUTE:
				// add the involved equation
				addEquation(a.getEq());
				break;
			case DELETE:
				break;
			case HAS:
				break;
			case RECEIVE:
				break;
			case TRUST:
				break;
			case VERIF_A:
				break;
			case VERIF_P:
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Helper method to collect all different equations in the architecture.
	 */
	public void collectEquations() {
		// go through all properties and extract the equations
		for (Property prop : allProperties) {
			switch (prop.getType()) {
			case KNOWS:
				addEquation(prop.getEq());
				break;
			default:
				// do nothing
				break;
			}
		}
		// also share this list of equations with its components
		for (Component comp : compList) {
			comp.setEqSet(new LinkedHashSet<Equation>(allEquations));
		}
	}

	/**
	 * Helper method to collect all different statements in the architecture.
	 */
	public void collectStatements() {
		// go through all events and extract the statements
		for (Action action : allActions) {
			switch (action.getAction()) {
			case RECEIVE:
				for (Statement st : action.getStSet()) {
					addStatement(st);
				}
				break;
			default:
				// do nothing
				break;
			}
		}
		// also share this list of equations with its components
		for (Component comp : compList) {
			comp.setEqSet(new LinkedHashSet<Equation>(allEquations));
		}
	}

	/**
	 * Helper method to collect all action performed by all components including
	 * the inter-component-actions.
	 */
	private void collectActions() {
		// Go through all actions of the components
		for (Component component : compList) {
			for (Action action : component.getActions()) {
				allActions.add(action);
			}
		}
		// Also add the inter-component ones
		for (Action interAct : interCompActions) {
			allActions.add(interAct);
		}
	}

	/**
	 * Helper method to collect all variables in the architecture.
	 */
	private void collectVariables() {
		// go through all components
		// no need to check the inter-component action,
		// as these only share variables that at least one component already knows
		for (Component comp : compList) {
			for (Variable var : comp.getVarSet()) {
				// collect all the variables in a list
				addVariable(var);
			}
		}
	}

	/**
	 * Helper method to collect all data types in the architecture.
	 */
	private void collectDataTypes() {
		// go through all actions
		for (Action a : allActions) {
			if (a.getAction() == ActionType.CRECEIVE || a.getAction() == ActionType.PERMISSION || a.getAction() == ActionType.REVOKE) {
				// add all data types
				addDataType(a.getDt());
			}
		}
	}

	/**
	 * Helper method to pass the compositions information to the involved components
	 */
	private void updateCompositions() {
		for (Composition compos : compositions) {
			// update the component's associate
			compos.getComponent().setAssociate(compos.getContainer());
		}
	}

	/**
	 * Method that adds a statement to the list if not already contained.
	 * 
	 * @param stmnt
	 *          the statement to add
	 */
	public void addStatement(Statement stmnt) {
		// only add new equations
		if ((stmnt != null) && (!allStatements.contains(stmnt))) {
			allStatements.add(stmnt);
		}
	}

	/**
	 * Method that adds an equation to the list if not already contained.
	 * 
	 * @param eq
	 *          the equation to add
	 */
	public void addEquation(Equation eq) {
		// only add new equations
		if ((eq != null) && (!allEquations.contains(eq))) {
			allEquations.add(eq);
		}
	}

	/**
	 * Method that adds a variable to the list if not already contained.
	 * 
	 * @param var
	 *          the variable to add
	 */
	public void addVariable(Variable var) {
		// Only add new variables to the list
		if (!allVariables.contains(var)) {
			allVariables.add(var);
		}
	}

	/**
	 * Method that adds a data type to the list if not already contained.
	 * 
	 * @param dt
	 *          the data type to add
	 */
	public void addDataType(DataType dt) {
		// Only add new data types to the list
		if (!allDataTypes.contains(dt)) {
			allDataTypes.add(dt);
		}
	}
	
	/**
	 * Method that adds an action to the list.
	 * @param action
	 * 			the action
	 */
	public void addAction(Action action) {
		switch (action.getAction()) {
		case RECEIVE:
			// fall through
		case SPOTCHECK:
			// fall through
		case CRECEIVE:
			// fall through
		case PRECEIVE:
			interCompActions.add(action);
			break;
		default:
			// all other actions
			action.getComponent().addAction(action);
			break;
		}
		allActions.add(action);
	}

	// Getter and setter methods
	public List<Component> getCompList() {
		return compList;
	}

	public void setCompList(List<Component> compList) {
		this.compList = compList;
	}

	public void addComponent(Component comp) {
		compList.add(comp);
	}

	public List<Action> getInterComp_Actions() {
		return interCompActions;
	}

	public void setInterComp_Actions(List<Action> interCompActions) {
		this.interCompActions = interCompActions;
	}

	public void addInterComp_Action(Action action) {
		interCompActions.add(action);
	}

	public List<Action> getAllActions() {
		return allActions;
	}

	public void setAllActions(List<Action> allActions) {
		this.allActions = allActions;
	}

	public List<Trust> getTrusts() {
		return trusts;
	}

	public void setTrusts(List<Trust> trusts) {
		this.trusts = trusts;
	}

	public List<Composition> getCompositions() {
		return compositions;
	}

	public List<Variable> getVariables() {
		return allVariables;
	}

	public List<Equation> getAllEquations() {
		return allEquations;
	}

	public void setAllEquations(List<Equation> allEquations) {
		this.allEquations = allEquations;
	}

	public List<Property> getAllProperties() {
		return allProperties;
	}

	public void setAllProperties(List<Property> allProperties) {
		this.allProperties = allProperties;
	}

	public List<Statement> getAllStatements() {
		return allStatements;
	}
	
	public PurposeHierarchy getPurposeHierarchy() {
		return purpHier;
	}
	
	public List<DataType> getDataTypes() {
		return allDataTypes;
	}
}

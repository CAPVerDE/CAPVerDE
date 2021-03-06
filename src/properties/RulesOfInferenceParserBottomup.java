package properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import architecture.Action;
import architecture.Architecture;
import architecture.Attest;
import architecture.Component;
import architecture.Composition;
import architecture.DataType;
import architecture.Deduction;
import architecture.Dep;
import architecture.Equation;
import architecture.P;
import architecture.Variable;
import properties.Property.PropertyType;
import architecture.Action.ActionType;
import utils.TraceBuffer;
import utils.TraceBuffer.LogType;

/**
 * Parser that implements rules of inference to gather all Has, K and B
 * properties from an Architecture_Class object.
 */
public class RulesOfInferenceParserBottomup extends Parser implements Serializable {

	/**
	 * @serial Serial ID for storing architecture objects in files.
	 */
	private static final long serialVersionUID = 6066854130700797593L;

	// class fields
	private Map<Property, Boolean> resultHistory;
	private List<Property> callHistory;

	/**
	 * The constructor for this class. This already verifies the architecture for consistency.
	 * 
	 * @param arch
	 *          the architecture to parse
	 */
	public RulesOfInferenceParserBottomup(Architecture arch) {
		//TODO
		// test this!
		super(arch);
		resultHistory = new HashMap<Property, Boolean>();
		callHistory = new ArrayList<Property>();
	}

	/**
	 * Method that verifies if the given statement is consistent with the
	 * architecture.
	 * 
	 * @param statement
	 *          the statement to verify
	 * @param recurseDepth
	 *          the depth of the recursion
	 * @return true, if the statement is satisfiable with the architecture
	 */
	@Override
	public boolean verifyStatement(Property statement, int recurseDepth) {
		String spacing = String.join("", Collections.nCopies(recurseDepth, "  "));
		System.out.println(spacing + "Current property to prove: " + statement);
		TraceBuffer.logMessage(
				statement, "Current property to prove: " + statement, recurseDepth, LogType.START);
		// recursion optimization: do not check the same statement twice
		Boolean result = resultHistory.put(statement, null);
		if (result != null) {
			// return the cached value
			resultHistory.put(statement, result);
			String msg = "Current statement already checked: ";
			msg += result ? "successfully verified" : "not successfully verified";
			System.out.println(spacing + msg);
			TraceBuffer.logMessage(statement, msg, recurseDepth, LogType.END);
			return result;
		}
		if (!callHistory.contains(statement)) {
			callHistory.add(statement);
		} else { // break condition if in endless loop via substitution/transitivity
			// this statement was already input but did not properly terminate (yet)
			// thus it should not be evaluated again
			System.out.println(spacing + "Stopping recursive endless loop");
			TraceBuffer.logMessage(
					statement, "Stopping recursive endless loop", recurseDepth, LogType.END);
			return false;
		}
		// do the actual work and apply the rules of inference
		switch (statement.getType()) {
		case CONJUNCTION:
			// Rule I^
			System.out.println(spacing + "Rule I^ applied for statement: " + statement);
			System.out.println(spacing + "Therefore trying to verify new statements:");
			TraceBuffer.logMessage(
					statement, "Rule I^ applied for statement: ", recurseDepth, LogType.INFO);
			TraceBuffer.logMessage(
					statement, "Therefore trying to verify new statements:", recurseDepth, LogType.INFO);
			if (verifyStatement(statement.getSt1(), recurseDepth + 1)
					&& verifyStatement(statement.getSt2(), recurseDepth + 1)) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule I^ applied for statement: " + statement);
				TraceBuffer.logMessage(statement, "Rule I^ applied for statement: "
						+ statement, recurseDepth, LogType.END);
				return true;
			} else {
				resultHistory.put(statement, false);
				System.out.println(spacing + "Rule I^ not applied for statement: " + statement);
				TraceBuffer.logMessage(statement, "Rule I^ not applied for statement: "
						+ statement, recurseDepth, LogType.END);
				return false;
			}
		case NEGATION:
			// Rule I_neg
			System.out.println(spacing + "Rule I_neg applied for statement: " + statement);
			System.out.println(spacing + "Therefore trying to verify new statement:");
			TraceBuffer.logMessage(
					statement, "Rule I_neg applied for statement: ", recurseDepth, LogType.INFO);
			TraceBuffer.logMessage(
					statement, "Therefore trying to verify new statement:", recurseDepth, LogType.INFO);
			if (!verifyStatement(statement.getSt1(), recurseDepth + 1)) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule I_neg applied for statement: " + statement);
				TraceBuffer.logMessage(statement, "Rule I_neg  applied for statement: "
						+ statement, recurseDepth, LogType.END);
				return true;
			} else {
				resultHistory.put(statement, false);
				System.out.println(spacing + "Rule I_neg not applied for statement: " + statement);
				TraceBuffer.logMessage(statement, "Rule I_neg not applied for statement: "
						+ statement, recurseDepth, LogType.END);
				return false;
			}
		case HAS:
			// Rule H1
			System.out.println(spacing + "Trying Rule H1...");
			TraceBuffer.logMessage(statement, "Trying Rule H1...", recurseDepth, LogType.INFO);
			if (arch.getAllActions().contains(new Action(
					ActionType.HAS, statement.getOwner(), statement.getVar()))) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule H1 applied for statement: " + statement);
				TraceBuffer.logMessage(statement, "Rule H1 applied for statement: "
						+ statement, recurseDepth, LogType.END);
				return true;
			} else {
				System.out.println(spacing + "Rule H1 not applicable");
				TraceBuffer.logMessage(
						statement, "Rule H1 not applicable", recurseDepth, LogType.INFO);
			}
			// Rule H2
			System.out.println(spacing + "Trying Rule H2...");
			TraceBuffer.logMessage(statement, "Trying Rule H2...", recurseDepth, LogType.INFO);
			if (isContainedReceive(statement.getOwner(), statement.getVar())) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule H2 applied for statement: " + statement);
				TraceBuffer.logMessage(statement, "Rule H2 applied for statement: "
						+ statement, recurseDepth, LogType.END);
				return true;
			} else {
				System.out.println(spacing + "Rule H2 not applicable");
				TraceBuffer.logMessage(
						statement, "Rule H2 not applicable", recurseDepth, LogType.INFO);
			}
			// Rule H3
			System.out.println(spacing + "Trying Rule H3...");
			TraceBuffer.logMessage(statement, "Trying Rule H3...", recurseDepth, LogType.INFO);
			if (isContainedCompute(statement.getOwner(), statement.getVar())) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule H3 applied for statement: " + statement);
				TraceBuffer.logMessage(statement, "Rule H3 applied for statement: " 
						+ statement, recurseDepth, LogType.END);
				return true;
			} else {
				System.out.println(spacing + "Rule H3 not applicable");
				TraceBuffer.logMessage(
						statement, "Rule H3 not applicable", recurseDepth, LogType.INFO);
			}
			// Rule H4
			//TODO
			System.out.println(spacing + "Trying Rule H4...");
			TraceBuffer.logMessage(statement, "Trying Rule H4...", recurseDepth, LogType.INFO);
			if (isContainedDep(statement.getOwner(), statement.getVar(), statement.getProb(), recurseDepth)) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule H4 applied for statement: " + statement);
				TraceBuffer.logMessage(statement, "Rule H4 applied for statement: "
						+ statement, recurseDepth, LogType.END);
				return true;
			} else {
				System.out.println(spacing + "Rule H4 not applicable");
				TraceBuffer.logMessage(
						statement, "Rule H4 not applicable", recurseDepth, LogType.INFO);
			}
			// Rule H5
			//TODO
			System.out.println(spacing + "Trying Rule H5...");
			TraceBuffer.logMessage(statement, "Trying Rule H5...", recurseDepth, LogType.INFO);
			TraceBuffer.logMessage(statement, "Therefore trying to verify new statement(s):", recurseDepth, LogType.INFO);
			if (isContainedCompos(statement.getOwner(), statement.getVar(), statement.getProb(), recurseDepth)) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule H5 applied for statement: " + statement);
				TraceBuffer.logMessage(statement, "Rule H5 applied for statement: "
						+ statement, recurseDepth, LogType.END);
				return true;
			} else {
				System.out.println(spacing + "Rule H5 not applicable");
				TraceBuffer.logMessage(
						statement, "Rule H5 not applicable", recurseDepth, LogType.INFO);
			}
			break;
		case KNOWS:
			// Rule K1
			System.out.println(spacing + "Trying Rule K1...");
			TraceBuffer.logMessage(statement, "Trying Rule K1...", recurseDepth, LogType.INFO);
			if (arch.getAllActions().contains(new Action(
					ActionType.COMPUTE, statement.getOwner(), statement.getEq()))) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule K1 applied for statement: " + statement);
				TraceBuffer.logMessage(statement, "Rule K1 applied for statement: "
						+ statement, recurseDepth, LogType.END);
				return true;
			} else {
				System.out.println(spacing + "Rule K1 not applicable");
				TraceBuffer.logMessage(statement, "Rule K1 not applicable", recurseDepth, LogType.INFO);
			}
			// Rule K2
			System.out.println(spacing + "Trying Rule K2...");
			TraceBuffer.logMessage(statement, "Trying Rule K2...", recurseDepth, LogType.INFO);
			if (isContainedCheck(statement.getOwner(), statement.getEq())) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule K2 applied for statement: " + statement);
				TraceBuffer.logMessage(statement, "Rule K2 applied for statement: "
						+ statement, recurseDepth, LogType.END);
				return true;
			} else {
				System.out.println(spacing + "Rule K2 not applicable");
				TraceBuffer.logMessage(statement, "Rule K2 not applicable", recurseDepth, LogType.INFO);
			}
			// Rule K3
			System.out.println(spacing + "Trying Rule K3...");
			TraceBuffer.logMessage(statement, "Trying Rule K3...", recurseDepth, LogType.INFO);
			if (isContainedProof(statement.getOwner(), statement.getEq())) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule K3 applied for statement: " + statement);
				TraceBuffer.logMessage(
						statement, "Rule K3 applied for statement: " + statement, recurseDepth, LogType.END);
				return true;
			} else {
				System.out.println(spacing + "Rule K3 not applicable");
				TraceBuffer.logMessage(statement, "Rule K3 not applicable", recurseDepth, LogType.INFO);
			}
			// Rule K4
			System.out.println(spacing + "Trying Rule K4...");
			TraceBuffer.logMessage(statement, "Trying Rule K4...", recurseDepth, LogType.INFO);
			if (isContainedProAtt(statement.getOwner(), statement.getEq())) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule K4 applied for statement: " + statement);
				TraceBuffer.logMessage(
						statement, "Rule K4 applied for statement: " + statement, recurseDepth, LogType.END);
				return true;
			} else {
				System.out.println(spacing + "Rule K4 not applicable");
				TraceBuffer.logMessage(statement, "Rule K4 not applicable", recurseDepth, LogType.INFO);
			}
			// Rule K5
			System.out.println(spacing + "Trying Rule K5...");
			TraceBuffer.logMessage(statement, "Trying Rule K5...", recurseDepth, LogType.INFO);
			if (isContainedAttest(statement.getOwner(), statement.getEq())) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule K5 applied for statement: " + statement);
				TraceBuffer.logMessage(
						statement, "Rule K5 applied for statement: " + statement, recurseDepth, LogType.END);
				return true;
			} else {
				System.out.println(spacing + "Rule K5 not applicable.");
				TraceBuffer.logMessage(statement, "Rule K5 not applicable.", recurseDepth, LogType.INFO);
			}
			// Rule Kded
			//TODO
			System.out.println(spacing + "Trying Rule K deduc...");
			System.out.println(spacing + "Therefore trying to verify new statements:");
			TraceBuffer.logMessage(statement, "Trying Rule K deduc...", recurseDepth, LogType.INFO);
			TraceBuffer.logMessage(
					statement, "Therefore trying to verify new statements:", recurseDepth, LogType.INFO);
			if (isContainedDed(statement.getOwner(), statement.getEq(), statement.getProb(), recurseDepth)) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule K deduc applied for statement: " + statement);
				TraceBuffer.logMessage(statement, "Rule K deduc applied for statement: "
						+ statement, recurseDepth, LogType.END);
				return true;
			} else {
				System.out.println(spacing + "Rule K deduc not applicable");
				TraceBuffer.logMessage(
						statement, "Rule K deduc not applicable", recurseDepth, LogType.INFO);
			}
			break;
		case NOTSHARED:
			// Rule SH1
			System.out.println(spacing + "Trying Rule SH1...");
			TraceBuffer.logMessage(statement, "Trying Rule SH1...", recurseDepth, LogType.INFO);
			if (isContainedCompute(statement.getOwner(), statement.getVar())
					|| isContainedHas(statement.getOwner(), statement.getVar())) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule SH1 applied for statement: " + statement);
				TraceBuffer.logMessage(
						statement, "Rule SH1 applied for statement: " + statement, recurseDepth, LogType.END);
				return true;
			} else {
				System.out.println(spacing + "Rule SH1 not applicable");
				TraceBuffer.logMessage(statement, "Rule SH1 not applicable", recurseDepth, LogType.INFO);
			}
			// Rule SH2
			System.out.println(spacing + "Trying Rule SH2...");
			TraceBuffer.logMessage(statement, "Trying Rule SH2...", recurseDepth, LogType.INFO);
			if (!isContainedReceive2(statement.getOwner(), statement.getVar())) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule SH2 applied for statement: " + statement);
				TraceBuffer.logMessage(
						statement, "Rule SH2 applied for statement: " + statement, recurseDepth, LogType.END);
				return true;
			} else {
				System.out.println(spacing + "Rule SH2 not applicable");
				TraceBuffer.logMessage(statement, "Rule SH2 not applicable", recurseDepth, LogType.INFO);
			}
			break;
		case NOTSTORED:
			// Rule ST1
			System.out.println(spacing + "Trying Rule ST1...");
			TraceBuffer.logMessage(statement, "Trying Rule ST1...", recurseDepth, LogType.INFO);
			if (!isContainedReceive(statement.getOwner(), statement.getVar())) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule ST1 applied for statement: " + statement);
				TraceBuffer.logMessage(
						statement, "Rule ST1 applied for statement: " + statement, recurseDepth, LogType.END);
				return true;
			} else {
				System.out.println(spacing + "Rule ST1 not applicable");
				TraceBuffer.logMessage(statement, "Rule ST1 not applicable", recurseDepth, LogType.INFO);
			}
			// Rule ST2
			System.out.println(spacing + "Trying Rule ST2...");
			TraceBuffer.logMessage(statement, "Trying Rule ST2...", recurseDepth, LogType.INFO);
			if (counter(statement.getOwner(), statement.getVar()) <= statement.getBound()) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule ST2 applied for statement: " + statement);
				TraceBuffer.logMessage(
						statement, "Rule ST2 applied for statement: " + statement, recurseDepth, LogType.END);
				return true;
			} else {
				System.out.println(spacing + "Rule ST2 not applicable");
				TraceBuffer.logMessage(statement, "Rule ST2 not applicable", recurseDepth, LogType.INFO);
			}
			break;
		case NOTPURP:
			//TODO
			// Rule P1
			System.out.println(spacing + "Trying Rule P1...");
			TraceBuffer.logMessage(statement, "Trying Rule P1...", recurseDepth, LogType.INFO);
			if (isContainedIllegalPReceive(statement.getOwner())) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule P1 applied for statement: " + statement);
				TraceBuffer.logMessage(
						statement, "Rule P1 applied for statement: " + statement, recurseDepth, LogType.END);
				return true;
			} else {
				System.out.println(spacing + "Rule P1 not applicable");
				TraceBuffer.logMessage(statement, "Rule P1 not applicable", recurseDepth, LogType.INFO);
			}
			// Rule P2
			System.out.println(spacing + "Trying Rule P2...");
			TraceBuffer.logMessage(statement, "Trying Rule P2...", recurseDepth, LogType.INFO);
			if (isContainedIncompatiblePurpose(statement.getOwner())) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule P2 applied for statement: " + statement);
				TraceBuffer.logMessage(
						statement, "Rule P2 applied for statement: " + statement, recurseDepth, LogType.END);
				return true;
			} else {
				System.out.println(spacing + "Rule P2 not applicable");
				TraceBuffer.logMessage(statement, "Rule P2 not applicable", recurseDepth, LogType.INFO);
			}
			break;
		case CONSENTVIOLATED:
			//TODO
			// Rule C
			System.out.println(spacing + "Trying Rule C...");
			TraceBuffer.logMessage(statement, "Trying Rule C...", recurseDepth, LogType.INFO);
			if (isContainedIllegalReceive(statement.getOwner(), statement.getDt())) {
				resultHistory.put(statement, true);
				System.out.println(spacing + "Rule C applied for statement: " + statement);
				TraceBuffer.logMessage(
						statement, "Rule C applied for statement: " + statement, recurseDepth, LogType.END);
				return true;
			} else {
				System.out.println(spacing + "Rule C not applicable");
				TraceBuffer.logMessage(statement, "Rule C not applicable", recurseDepth, LogType.INFO);
			}
			break;
		default:
			break;
		}
		// no rule applied
		resultHistory.put(statement, false);
		System.out.println(spacing + "No Rule applicable for statement: " + statement);
		TraceBuffer.logMessage(
				statement, "No Rule applicable for statement: " + statement, recurseDepth, LogType.END);
		return false;
	}

	/**
	 * Helper method to check whether a component sends at least one variable to a component,
	 * which passes it on to another one with an incompatible purpose.
	 * It is checked whether a variable in the second PReceive event has a purpose higher in the purp hierarchy.
	 * @param owner
	 * @return
	 */
	private boolean isContainedIncompatiblePurpose(Component owner) {
		// TODO not only same variable
		for (Action a : arch.getInterComp_Actions()) {
			if (a.getAction() != ActionType.PRECEIVE || !a.getComponent().equals(owner)) {
				continue;
			}
			for (Action a2 : arch.getInterComp_Actions()) {
				if (a2.getAction() != ActionType.PRECEIVE || !a2.getComPartner().equals(owner)) {
					continue;
				}
				for (Variable var : a.getVarSet()) {
					for (Variable var2 : a2.getVarSet()) {
						if (isContainedDep2(owner, var, var2, 0)) {
							// a purpose-bound variable is passed on to a third party
							if (!arch.getPurposeHierarchy().compare(a2.getPurpose(), a.getPurpose())) {
								// the purposes of the two PReceives are not compatible
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Helper method to check whether a component sends at least one variable with an unsuitable purpose.
	 * It is checked whether the purpose of a PReceive action covers all transmitted variables.
	 * @param owner
	 * 			the component to be checked
	 * @return
	 * 			true/false
	 */
	private boolean isContainedIllegalPReceive(Component owner) {
		// TODO test
		for (Action a : arch.getInterComp_Actions()) {
			if (a.getAction() != ActionType.PRECEIVE || !a.getComponent().equals(owner)) {
				continue;
			}
			for (Variable var : a.getVarSet()) {
				if (!a.getPurpose().getVars().contains(var)) {
					// variable is not part of the purpose-bound consent
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Helper method to check whether a components shares at least one variable without an according consent.
	 * @param owner
	 * 			the component
	 * @param dt
	 * 			the data type that contains the variables
	 * @return
	 */
	private boolean isContainedIllegalReceive(Component owner, DataType dt) {
		// find a receive action that fits the component and data type
		for (Action a : arch.getInterComp_Actions()) {
			if (a.getAction() != ActionType.RECEIVE || !a.getComPartner().equals(owner)) {
				continue;
			}
			for (Variable v : dt.getVars()) {
				if (a.getVarSet().contains(v)) {
					// check if there is a revoke or no permission
					if (isContainedRevoke(owner, dt) || !isContainedPermission(owner, dt)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Helper method to check whether a component has a suiting revoke action.
	 * @param comp
	 * 			the component
	 * @param dt
	 * 			the data type
	 * @return
	 */
	private boolean isContainedRevoke(Component comp, DataType dt) {
		//TODO test
		for (Action a : arch.getAllActions()) {
			if (a.getAction() != ActionType.REVOKE) {
				continue;
			}
			if (a.getComPartner().equals(comp) && a.getDt().equals(dt)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Helper method to check whether a component has a suiting permission action.
	 * @param comp
	 * 			the component
	 * @param dt
	 * 			the data type
	 * @return
	 */
	private boolean isContainedPermission(Component comp, DataType dt) {
		//TODO test
		for (Action a : arch.getAllActions()) {
			if (a.getAction() != ActionType.PERMISSION) {
				continue;
			}
			if (a.getComPartner().equals(comp) && a.getDt().equals(dt)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method that returns the maximum number of events that a component accesses a variable before deleting it.
	 * @param owner
	 * 			the component
	 * @param var
	 * 			the variable
	 * @return the number of events before delete
	 */
	private int counter(Component owner, Variable var) {
		// TODO test this!
		int counter = 0;
		int maxCounter = 0;
		for (Action action : arch.getAllActions()) {
			// only count all actions of component with variable
			if (action.getComponent().equals(owner)) {
				switch (action.getAction()) {
				case CHECK:
					// fall through
				case COMPUTE:
					if (action.getEq().getAtoms().contains(var)) {
						// the var is used
						counter++;
						if (counter > maxCounter) {
							maxCounter = counter;
						}
					}
					break;
				case DELETE:
					if (action.getVar().equals(var)) {
						// the var gets deleted
						counter--;
					}
					break;
				default:
					break;
				}
			} else if (action.getComPartner() != null && action.getComPartner().equals(owner)) {
				if (action.getAction() == ActionType.RECEIVE && action.getVarSet().contains(var)) {
					// the component sends the variable to another comp
					counter++;
					if (counter > maxCounter) {
						maxCounter = counter;
					}
				}
			}

		}
		return maxCounter;
	}


	/**
	 * Helper method that returns a probability for which the has property holds.
	 * @param comp
	 * 			the component
	 * @param var
	 * 			the variable
	 * @param recurseDepth
	 * 			the depth of the recursion
	 * @return a probability for which the property holds, 0 if it does not
	 */
	private double verifyHasProb(Component comp, Variable var, int recurseDepth) {
		//TODO better approach
		double prob = 1;
		while (prob >= 0.000001) {
			if (verifyStatement(new Property(PropertyType.HAS, comp, prob, var), recurseDepth + 1)) {
				// test with what probability the property applies
				return prob;
			}
			prob = prob / 10;
		}
		return 0;
	}

	/**
	 * Helper method that returns a probability for which the knows property holds.
	 * @param comp
	 * 			the component
	 * @param eq
	 * 			the equation
	 * @param recurseDepth
	 * 			the depth of the recursion
	 * @return a probability for which the property holds, 0 if it does not
	 */
	private double verifyKnowsProb(Component comp, Equation eq, int recurseDepth) {
		//TODO better approach
		double prob = 1;
		while (prob >= 0.000001) {
			if (verifyStatement(new Property(PropertyType.KNOWS, comp, prob, eq), recurseDepth + 1)) {
				// test with what probability the property applies
				return prob;
			}
			prob = prob / 10;
		}
		return 0;
	}

	/**
	 * Helper method to check if there is a fitting receive in the action of the
	 * architecture.
	 * 
	 * @param comp
	 *          the receiving component
	 * @param var
	 *          the variable to look for
	 * @return true, if there is a receive that fits
	 */
	private boolean isContainedReceive(Component comp, Variable var) {
		for (Action action : arch.getAllActions()) {
			if (action.getAction() == ActionType.RECEIVE) {
				// check if the right acting component and if the variable is contained
				if (action.getComponent().equals(comp) && action.getVarSet().contains(var)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Helper method to check if there is a fitting receive in the action of the
	 * architecture.
	 * 
	 * @param comp
	 *          the sending component
	 * @param var
	 *          the variable to look for
	 * @return true, if there is a receive that fits
	 */
	private boolean isContainedReceive2(Component comp, Variable var) {
		for (Action action : arch.getAllActions()) {
			if (action.getAction() == ActionType.RECEIVE) {
				// check if the right acting component and of the variable is contained
				if (action.getComPartner().equals(comp) && action.getVarSet().contains(var)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Helper method to check if there is a fitting compute in the action of the
	 * architecture.
	 * 
	 * @param comp
	 *          the acting component
	 * @param var
	 *          the variable to look for
	 * @return true, if there is a compute that fits
	 */
	private boolean isContainedCompute(Component comp, Variable var) {
		for (Action action : arch.getAllActions()) {
			if (action.getAction() == ActionType.COMPUTE) {
				// check if the right acting component and of the variable is contained
				if (action.getComponent().equals(comp) && action.getEq().getLefthandSide().equals(var)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Helper method to check if there is a fitting has in the action of the
	 * architecture.
	 * 
	 * @param comp
	 *          the acting component
	 * @param var
	 *          the variable to look for
	 * @return true, if there is a has that fits
	 */
	private boolean isContainedHas(Component comp, Variable var) {
		for (Action action : arch.getAllActions()) {
			if (action.getAction() == ActionType.HAS) {
				// check if the right acting component and of the variable is contained
				if (action.getComponent().equals(comp) && action.getVar().equals(var)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Helper method to check if there is a fitting dep for the component.
	 * 
	 * @param comp
	 *          the acting component
	 * @param var
	 *          the variable to look for
	 * @param prob
	 *          the probability of the dep
	 * @param recurseDepth
	 *          the depth of recursion
	 * @return true, if there is a dep that fits
	 */
	private boolean isContainedDep(Component comp, Variable var, double prob, int recurseDepth) {
		// consider probabilities
		for (Dep dep : comp.getDepSet()) {
			if (dep.getVar().equals(var)) {
				// check if all required variables are possessed
				double allProbs = dep.getProb();
				for (Variable mustHave : dep.getVarSet()) {
					// multiply along the path
					allProbs *= verifyHasProb(comp, mustHave, recurseDepth + 1);
				}
				if (allProbs >= prob) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Helper method to check whether a component can obtain a variable from another one.
	 * @param comp
	 * 			the component
	 * @param start
	 * 			the variable the component has
	 * @param end
	 * 			the variable the component wants to obtain
	 * @param recurseDepth
	 * 			the depth of recursion
	 * @return
	 * 			success
	 */
	private boolean isContainedDep2(Component comp, Variable start, Variable end, int recurseDepth) {
		//TODO test!
		if (start.equals(end)) {
			// reflexive
			return true;
		}
		for (Dep dep : comp.getDepSet()) {
			if (dep.getVar().equals(end) && dep.getVarSet().contains(start)) {
				// the component can obtain end from start
				return true;
			}
			if (dep.getVar().equals(end)) {
				// also check for possible transitive deps
				for (Variable var : dep.getVarSet()) {
					if (isContainedDep2(comp, start, var, recurseDepth+1)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isContainedCompos(Component owner, Variable var, Double prob, int recurseDepth) {
		// TODO test
		for (Composition compos : arch.getCompositions()) {
			if (compos.getComponent().equals(owner) && verifyHasProb(compos.getContainer(), var, recurseDepth +1) >= prob) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Helper method to check if there is a fitting deduction for the component.
	 * 
	 * @param comp
	 *          the acting component
	 * @param eq
	 *          the equation to look for
	 * @param prob
	 *          the probability of the ded
	 * @param recurseDepth
	 *          the depth of recursion
	 * @return true, if there is a dep that fits
	 */
	private boolean isContainedDed(Component comp, Equation eq, double prob, int recurseDepth) {
		// consider probabilities
		for (Deduction ded : comp.getDeductionCapability()) {
			if (ded.getConclusion().equals(eq)) {
				// check if all required variables are possessed
				double allProbs = ded.getProb();
				for (Equation mustHave : ded.getPremises()) {
					// multiply along the path
					allProbs *= verifyKnowsProb(comp, mustHave, recurseDepth + 1);
				}
				if (allProbs > prob) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Helper method to check if there is a fitting check in the action of the
	 * architecture.
	 * 
	 * @param comp
	 *          the acting component
	 * @param eq
	 *          the equation to look for
	 * @return true, if there is a check that fits
	 */
	private boolean isContainedCheck(Component comp, Equation eq) {
		for (Action action : arch.getAllActions()) {
			if (action.getAction() == ActionType.CHECK) {
				// check if the right acting component and if the equation is contained
				if (action.getComponent().equals(comp) && action.getEqSet().contains(eq)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Helper method to check if there is a fitting verifP in the action of the
	 * architecture.
	 * 
	 * @param comp
	 *          the acting component
	 * @param eq
	 *          the equation to look for
	 * @return true, if there is a verifP that fits
	 */
	private boolean isContainedProof(Component comp, Equation eq) {
		for (Action action : arch.getAllActions()) {
			if (action.getAction() == ActionType.VERIF_P) {
				// check if the right acting component and if the equation is contained
				if (action.getComponent().equals(comp) && action.getPro().getpSet().contains(eq)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Helper method to check if there is a fitting verifP with an attest in the
	 * action of the architecture.
	 * 
	 * @param comp
	 *          the acting component
	 * @param eq
	 *          the equation to look for
	 * @return true, if there is a verifP containing a valid attest that fits
	 */
	private boolean isContainedProAtt(Component comp, Equation eq) {
		// different approach
		for (Action action : arch.getAllActions()) {
			if (action.getAction() == ActionType.VERIF_P) {
				// check if there is an attest in the proof that fits
				for (P p : action.getPro().getpSet()) {
					if (p instanceof Attest) {
						// only if the verifying component trusts the attesting one
						if (arch.trust(action.getComponent(), ((Attest) p).getComponent())) {
							// check of the equation is in the attest
							if (((Attest) p).getEqSet().contains(eq)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Helper method to check if there is a fitting verifA in the actions of the
	 * architecture.
	 * 
	 * @param comp
	 *          the acting component
	 * @param eq
	 *          the equation to look for
	 * @return true, if there is a verifA containing a valid attest that fits
	 */
	private boolean isContainedAttest(Component comp, Equation eq) {
		for (Action action : arch.getAllActions()) {
			if (action.getAction() == ActionType.VERIF_A) {
				// check if the attesting component is "trustworthy"
				if (arch.trust(action.getComponent(), action.getAtt().getComponent())) {
					// check if the equation is contained
					if (action.getAtt().getEqSet().contains(eq)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// Setter and getter methods
	public Architecture getArch() {
		return arch;
	}

	public void setArch(Architecture arch) {
		this.arch = arch;
	}
}

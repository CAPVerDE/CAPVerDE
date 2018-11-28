package properties;

import java.util.List;
import java.util.Set;

import architecture.Action;
import architecture.Architecture;
import solver.SmtHandler;

/**
 * A bottom-up parser based on SMT solver.
 */
public class SmtParser extends Parser {
	
	/**
	 * @serial Serial ID for storing architecture objects in files.
	 */
	private static final long serialVersionUID = 4962127290155017777L;
	
	
	// class fields
	private Set<Property> properties;
	private SmtHandler smt;
	private List<Action> actionLog;
	
	public SmtParser(Architecture arch) {
		super(arch);
		setSmt(new SmtHandler(arch));
	}

	@Override
	public boolean verifyStatement(Property statement, int recurseDepth) {
		// TODO
		return false;
	}
	
	// getter and setter methods
	public Set<Property> getProperties() {
		return properties;
	}

	public void setProperties(Set<Property> properties) {
		this.properties = properties;
	}

	public SmtHandler getSmt() {
		return smt;
	}

	public void setSmt(SmtHandler smt) {
		this.smt = smt;
	}

	public List<Action> getActionLog() {
		return actionLog;
	}

	public void setActionLog(List<Action> actionLog) {
		this.actionLog = actionLog;
	}

}

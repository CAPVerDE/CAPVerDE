package architecture;

import java.util.Set;

public class DataType {

	// class fields
	private String name;
	private Set<Variable> vars;

	public DataType(String name, Set<Variable> vars) {
		this.name = name;
		this.vars = vars;
	}

	@Override
	public String toString() {
		return name;
	}

	// getter and setter methods
	public Set<Variable> getVars() {
		return vars;
	}
}

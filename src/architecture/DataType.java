package architecture;

import java.util.Set;

public class DataType {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((vars == null) ? 0 : vars.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataType other = (DataType) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (vars == null) {
			if (other.vars != null)
				return false;
		} else if (!vars.equals(other.vars))
			return false;
		return true;
	}

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

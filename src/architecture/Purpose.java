package architecture;

import java.util.Collections;
import java.util.Set;

/**
 * Purpose Role objects.
 * A purpose is connected to a purpose hierarchy and a Labeling that connects Purposes and Variables.
 */
public class Purpose {
	
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
		Purpose other = (Purpose) obj;
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

	@Override
	public String toString() {
		return name;
	}
	
	// class fields
	private String name;
	private Set<Variable> vars;
	
	/**
	 * Constructor for purposes.
	 * @param name	the name of the purpose
	 * @param vars	the connected variables
	 */
	public Purpose(String name, Set<Variable> vars) {
		this.name = name;
		this.vars = vars;
	}
	
	/**
	 * Empty constructor without variables.
	 * @param name	the name of the purpose
	 */
	public Purpose(String name) {
		this(name, Collections.emptySet());
	}

	// getter and setter methods
	public void setVars(Set<Variable> vars) {
		this.vars = vars;
	}
	public Set<Variable> getVars() {
		return vars;
	}
}

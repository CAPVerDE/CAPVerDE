package architecture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PurposeHierarchy {
	
	private static final int SIZE = 10; //TODO for test purposes
	
	// class fields
	private List<Purpose> purposes;
	private boolean [][] adjacencyMatrix;
	private Purpose top;
	private Purpose bot;
	
	/**
	 * Empty Constructor.
	 * Initializes the variables.
	 */
	public PurposeHierarchy() {
		purposes = new ArrayList<Purpose>();
		top = new Purpose("Top", new LinkedHashSet<Variable>()); //TODO set that includes all vars
		bot = new Purpose("Bot", Collections.emptySet());
		purposes.add(top);
		purposes.add(bot);
		// initialize ridiculously large array...
		adjacencyMatrix = new boolean [SIZE][SIZE];
	}
	
	/**
	 * Function that compares two purposes in the hierarchy.
	 * Returns true if the first is lower, 0 else.
	 * @param p1	the first purpose
	 * @param p2	the second purpose
	 * @return		1,0,null
	 */
	public boolean compare(Purpose p1, Purpose p2) {
		if (p1.equals(p2)) {
			return true;
		}
		return isChild(p1, p2);
	}
	
	/**
	 * Method to check whether a purpose is a (transitive) child of another.
	 * @param p1	the child purpose
	 * @param p2	the other purpose
	 * @return		true if p1 is a child of p2
	 */
	private boolean isChild(Purpose p1, Purpose p2) {
		if (!(purposes.contains(p1) && purposes.contains(p2))) {
			// one of the purposes is not contained in hierarchy
			return false;
		}
		if (adjacencyMatrix[purposes.indexOf(p2)][purposes.indexOf(p1)]) {
			// direct child
			return true;
		}
		int counter = 0;
		int i = 0;
		while (counter < trueCount(adjacencyMatrix[purposes.indexOf(p2)])) {
			if (adjacencyMatrix[purposes.indexOf(p2)][i]) {
				// child found -> check children of child recursively
				if (isChild(p1, purposes.get(i))) {
					return true;
				}
				++counter;
			}
			++i;
		}
		//TODO finish testing!
		return false;
	}
	
	/**
	 * Inefficient helper method to count number of true in row.
	 * @param children	the row in AM
	 * @return			number of true
	 */
	private int trueCount(boolean[] children) {
		int count = 0;
		for (int i = 0; i < children.length; i++) {
		    if (children[i]) {
		        count++;
		    }
		}
		return count;
	}
	
	/**
	 * Add a new purpose to the hierarchy.
	 * Updates the adjacency matrix if successful.
	 * @param p			the new purpose
	 * @param parents	all parent purposes
	 * @param children	all child purposes
	 * @return			success
	 */
	public boolean addPurpose(Purpose p, Set<Purpose> parents, Set<Purpose> children) {
		 if (!purposes.add(p)) {
			 return false;
		 } else {
			 updateAM(p, parents, children);
			 return true;
		 }
	}

	/**
	 * Updates the adjacency matrix with a new row+column for the purpose.
	 * Updates all edges.
	 * @param p			the new purpose
	 * @param parents	all parents
	 * @param children	all children
	 */
	private void updateAM(Purpose p, Set<Purpose> parents, Set<Purpose> children) {
		// TODO finish
		// update the parent nodes
		if (!parents.isEmpty()) {
			for (Purpose parent : parents) {
				adjacencyMatrix[purposes.indexOf(parent)][purposes.indexOf(p)] = true;
			}
		}
		// update child nodes
		if (!children.isEmpty()) {
			for (Purpose child : children) {
				adjacencyMatrix[purposes.indexOf(p)][purposes.indexOf(child)] = true;
			}
		}
		// delete all connections between parents and children of new purpose
		for (Purpose parent : parents) {
			for (Purpose child : children) {
				if (adjacencyMatrix[purposes.indexOf(parent)][purposes.indexOf(child)]) {
					adjacencyMatrix[purposes.indexOf(parent)][purposes.indexOf(child)] = false;
				}
			}
		}
	}
	
	// getter and setter methods
	public List<Purpose> getPurposes() {
		return purposes;
	}
	
	public boolean [][] getAM() {
		return adjacencyMatrix;
	}

}

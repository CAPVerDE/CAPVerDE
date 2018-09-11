package architecture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PurposeHierarchy {
	
	private static final int SIZE = 100;
	
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
	 * Returns 1 if the first is lower, 0 if it is higher, null else.
	 * @param p1	the first purpose
	 * @param p2	the second purpose
	 * @return		1,0,null
	 */
	public Boolean compare(Purpose p1, Purpose p2) {
		return isChild(p2, p1);
	}
	
	private Boolean isChild(Purpose p1, Purpose p2) {
		if (!(purposes.contains(p1) && purposes.contains(p2))) {
			// one of the purposes is not contained in hierarchy
			return null;
		} else {
			return false;
		}
			//TODO finish
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
		adjacencyMatrix[0][2] = true;
		System.out.println("test!");
	}
	
	// getter and setter methods
	public List<Purpose> getPurposes() {
		return purposes;
	}
	
	public boolean [][] getAM() {
		return adjacencyMatrix;
	}

}

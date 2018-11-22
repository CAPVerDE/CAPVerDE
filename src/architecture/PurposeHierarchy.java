package architecture;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PurposeHierarchy implements Serializable {

	/**
	 * @serial Serial ID for storing architecture objects in files.
	 */
	private static final long serialVersionUID = -4167533256291006038L;

	private static final int SIZE = 10; //TODO for test purposes

	// class fields
	private List<Purpose> purposes;
	private boolean [][] adjacencyMatrix;
	private Purpose top;
	private Purpose bot;

	/**
	 * Full constructor with a list of purposes and a valid adjacency matrix.
	 * @param purposes
	 * 			a list of existing purposes
	 * @param adjacencyMatrix
	 * 			the hierarchy of the given purposes
	 */
	public PurposeHierarchy(List<Purpose> purposes, boolean [][] adjacencyMatrix) {
		this.purposes = new ArrayList<Purpose>(purposes);
		this.adjacencyMatrix = new boolean [SIZE][SIZE];
		//TODO test this!
		copy2DArray(adjacencyMatrix, this.adjacencyMatrix, 2);
		// assume top and bot do not exist
		top = new Purpose("Top", new LinkedHashSet<Variable>()); //TODO set that includes all vars
		bot = new Purpose("Bot", Collections.emptySet());
		// add top and bot
		this.purposes.add(0, top);
		this.purposes.add(1, bot);
		// include the top and bot into the am
		updateTopBot(adjacencyMatrix);
	}

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

	private void updateTopBot(boolean[][] adjacencyMatrix2) {
		// TODO Auto-generated method stub
		for (int i=0; i<adjacencyMatrix2.length; i++) {
			boolean children = false;
			boolean parent = false;
			for (int j=0; j<adjacencyMatrix2[i].length; j++) {
				children = children || adjacencyMatrix2[i][j];
				parent = parent || adjacencyMatrix2[j][i];
			}
			if (!children) {
				// no children -> set bot as child
				adjacencyMatrix[i + 2][1] = true;
			}
			if (!parent) {
				// no parent -> set as child of top
				adjacencyMatrix[0][i + 2] = true;
			}
		}
		System.out.println("Debug");
	}

	private void copy2DArray(boolean[][] src, boolean[][] dest, int offset) {
		// TODO test
		if (src.length + offset > dest.length) {
			// error
			System.out.println("Destination is not big enough for the source and offset!");
			return;
		}
		for (int i=0; i<src.length; i++) {
			for (int j=0; j<src[i].length; j++) {
				dest[i+offset][j+offset] = src[i][j];
			}
		}
	}

	/**
	 * Function that compares two purposes in the hierarchy.
	 * Returns true if the first is lower, false else.
	 * @param p1	the first purpose
	 * @param p2	the second purpose
	 * @return		true, false
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
		if (parents.isEmpty()) {
			parents = Collections.singleton(top);
		}
		for (Purpose parent : parents) {
			adjacencyMatrix[purposes.indexOf(parent)][purposes.indexOf(p)] = true;
		}
		// update child nodes
		if (children.isEmpty()) {
			children = Collections.singleton(bot);
		}
		for (Purpose child : children) {
			adjacencyMatrix[purposes.indexOf(p)][purposes.indexOf(child)] = true;
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

	public Purpose getTop() {
		return top;
	}

	public Purpose getBot() {
		return bot;
	}

}

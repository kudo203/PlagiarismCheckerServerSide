package cloneDetectionTechniques.ASTDiff;

import cloneDetectionTechniques.ASTDiff.ast.JavaAST;
import cloneDetectionTechniques.ASTDiff.ast.WrapperNode;
import cloneDetectionTechniques.CloneTechnique;
import cloneDetectionTechniques.TechniqueVisitor;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import engine.Clone;
import engine.CodeStub;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;
import java.util.*;

// represents a clone detection technique that compares AST Subtrees by hashing them first
// and comparing the subtrees in the same hash bucket by calculating the min edit distance
// of their string representation
public class AstDiff implements CloneTechnique {

	private String type = "ASTEditDistance"; // name of the clone detection
												// technique
	private List<Clone> clones; // list of the clones of this type found between
								// f1 and f2

	private File f1; // file 1 to be compared
	private File f2; // file 2 to be compared

	private JavaAST f1AST; // AST info of file 1
	private JavaAST f2AST; // AST info of file 2

	private final static double SIMILARITY_THR = 0.8; // subtree similarity threshold
	private final static double MIN_CODE_SPAN = 1; // minimum code span of a node

	// constructor
	public AstDiff(File f1, File f2)  {
		clones = new ArrayList<>();
		this.f1 = f1;
		this.f2 = f2;
	}

    /**
     *finds the clones between f1 and f2 and populates clones list by creating
     * two hash tables for the two files and calling findSameHashClones on them
     */
	public void findClones() throws FileSystemNotFoundException, IOException, ParseProblemException {

		f1AST = new JavaAST(f1);
		f2AST = new JavaAST(f2);

		// find clones with the same hash value
		findHashtableClones(f2AST.getRoot());
	}

    /**
     * Finds clones in f1, for each node of f2, which
     * is visited in a depth first search
     * fashion.
     */
	private void findHashtableClones(Node node) {
		// ignore very small subtrees
		// this can be tuned according to user expectation
		int beginLine = node.getRange().get().begin.line;
		int endLine = node.getRange().get().end.line;
		if (endLine - beginLine <= MIN_CODE_SPAN)
			return;

		// ignore the nodes that are not present in f2AST
		Integer nodeHash = f2AST.getNodeHash(node);

		// finding exact and near miss hashcodes
		Integer exact = f1AST.getHashTable().getBucket(nodeHash);
		Integer leftNear = f1AST.getHashTable().getLeftNearBucket(nodeHash);
		Integer rightNear = f1AST.getHashTable().getRightNearBucket(nodeHash);

		Integer[] buckets = new Integer[]{exact, leftNear, rightNear};
		boolean found = false;
		for (Integer bucket : buckets) {
			if (findPossibleClones(node, bucket)) {
				found = true;
				break;
			}
		}
		if (!found) {
			for (Node childNode : node.getChildNodes())
				findHashtableClones(childNode);
		}

	}

    /**
     * Finds if the given node has a clone in the hashbucket
     */
	private boolean findPossibleClones(Node node, Integer bucketNum) {
		Clone clone;
		if (bucketNum != null) {
			clone = checkBucket(f1AST.getHashTable().getBucketHashCode(bucketNum), node);
			if (clone != null) {
				addClone(clone);
				return true;
			}
		}
		return false;

	}

    /**
     * Adds a potential clone in the list
     * of the clones
     */
	private void addClone(Clone clone) {
		// avoid overlapping clones :
		for (int i = 0; i < clones.size(); i++) {
			Clone c = clones.get(i);
			if (c.overLaps(clone)) {
				if (c.isBetterMatch(clone)) {
					return;
				} else {
					clones.remove(c);
					i--;
				}
			}
		}
        clones.add(clone);
	}

    /**
     * Finds possible clones for the given node
     * in the hashbucket defined by f1Code
     * @param f1Code - hashvalue of the bucket
     * @param node - node in file 2
     */
	private Clone checkBucket(int f1Code, Node node) {
		double minSimilarity = Double.MAX_VALUE;
		Node closestNode = null;

		List<WrapperNode> f1Possible = f1AST.getAllNodesWithHashcode(f1Code);
		List<WrapperNode> sortedList = getSortList(f1Possible, node);
		for (WrapperNode wNodes : sortedList) {
			double sim = similarityOfSubtrees(wNodes.getNode(), node);
			if (sim >= SIMILARITY_THR) {
				minSimilarity = sim;
				closestNode = wNodes.getNode();
			}
			
		}
		if (closestNode == null)
			return null;
		
		return makeClone(node, closestNode, minSimilarity);

	}

    /**
     * Prepares the Clone object defined by
     * the two input nodes in the arguments
     * @param node - from file 2
     * @param closestNode - from file 1
     * @param minSimilarity - clone %
     */
	private Clone makeClone(Node node, Node closestNode, double minSimilarity) {
		Range rangeF1 = closestNode.getRange().get();
		Range rangeF2 = node.getRange().get();

		CodeStub stubF1 = new CodeStub(rangeF1.begin.line, rangeF1.end.line);
		CodeStub stubF2 = new CodeStub(rangeF2.begin.line, rangeF2.end.line);

		return new Clone(stubF1, stubF2, minSimilarity);
	}

    /**
     * Finds clone % between two nodes
     */
	private double similarityOfSubtrees(Node f1, Node f2) {

	    //Mapping between node type to their frequency in
        //their respective clones
		Map<String, Integer> NodeTypeCountF1 = new HashMap<>();
		Map<String, Integer> NodeTypeCountF2 = new HashMap<>();

		//generates the above mapping
		getSubtreeTypeCount(f1, NodeTypeCountF1);
		getSubtreeTypeCount(f2, NodeTypeCountF2);

		Map<String, Integer> NodeTypeCountBoth = new HashMap<>();

		Set<String> Allkeys = new HashSet(NodeTypeCountF1.keySet());
		Allkeys.addAll(NodeTypeCountF2.keySet());

		//Count of Similar Type Nodes
		int S = 0;
        //Count of Different Type of Nodes in File2
		int L = 0;
        //Count of Different Type of Nodes in File1
		int R = 0;

		for (String k : Allkeys) {
			if (NodeTypeCountF1.containsKey(k) && NodeTypeCountF2.containsKey(k)) {
				int commonCount = Math.min(NodeTypeCountF1.get(k), NodeTypeCountF2.get(k));
				S += commonCount;
				NodeTypeCountBoth.put(k, commonCount);
			}
		}
		for (Map.Entry<String, Integer> entryF2 : NodeTypeCountF2.entrySet()) {
			L += entryF2.getValue();
		}
		for (Map.Entry<String, Integer> entryF1 : NodeTypeCountF1.entrySet()) {
			R += entryF1.getValue();
		}
		L -= S;
		R -= S;

		//Clone percentage
		return (2 * S + 0.0) / (2 * S + L + R + 0.0);
	}

    /**
     * Generates the mapping between the node type
     * and their frequencies
     */
	private void getSubtreeTypeCount(Node f1, Map<String, Integer> NodeTypeCount) {
		String className = f1.getClass().getName();
		if (NodeTypeCount.containsKey(className)) {
			Integer count = NodeTypeCount.get(className) + 1;
			NodeTypeCount.put(className, count);
		} else {
			NodeTypeCount.put(className, 1);
		}
		if (f1.getChildNodes().size() != 0) {
			for (Node child : f1.getChildNodes()) {
				getSubtreeTypeCount(child, NodeTypeCount);
			}
		}
	}

	/*
	* alligns the priority of the nodes in the hashbucket in F1, with the first
	* node having similar node type with the node in F2. The later nodes in the
	* sequence are in descending order of content length enclosed by the node
	* in F2
    */
	private List<WrapperNode> getSortList(List<WrapperNode> f1Possible, Node node) {

		List<WrapperNode> newList = new ArrayList<>();
		newList.addAll(f1Possible);

		List<WrapperNode> sorted = new ArrayList<>();
		int index = 0;
		for (WrapperNode n : newList) {
			if (n.getNode().getClass() == node.getClass()) {
				sorted.add(n);
				newList.remove(index);
				break;
			}
			index++;
		}

		Collections.sort(newList);

		sorted.addAll(newList);

		return sorted;
	}

	/*
	*accepts the similairty visitor
	*/
	public void accept(TechniqueVisitor v) {
		try {
			v.visit(this);
		} catch (FileSystemNotFoundException | ParseProblemException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * getter for list of clones
	 */
	public List<Clone> getClones() {
		return clones;
	}

	/**
	 * getter for the type
	 */
	public String getType() {
		return type;
	}

    /**
     * switches the pointer of the files
     */
	@Override
	public void switchFiles() {
		File t = f1;
		f1 = f2;
		f2 = t;
		for (Clone c : clones) {
			c.switchFiles();
		}
		
	}
	
	
}

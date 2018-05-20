package cloneDetectionTechniques.ASTDiff.ast;

import com.github.javaparser.ast.Node;

import java.util.*;

// represents a hash table of nodes in an AST and their hashcodes
public class HashTable {
	// the AST this table represents
	private JavaAST ast;
    // hashcode buckets for ast
	private Map<Integer, List<WrapperNode>> hashBuckets;
    // Mapping of nodes with their hashcodes, for ast
	private Map<Node, Integer> nodeHashcodeMap;
    // all hashcodes of AST sorted in descending order
	private int[] hashSorted;

	//constructor
	public HashTable(JavaAST ast) {
		this.ast = ast;
		this.hashBuckets = new TreeMap<>();
		this.nodeHashcodeMap = new HashMap<>();

	}

    /**
     * adds a <hashVal, node> entry in the table
     * @param hashVal
     * @param node
     */
	public void addEntry(int hashVal, Node node) {
	    try {
			node.getRange().get();
		}
		catch (NoSuchElementException e){
			return;
		}
		// add this node to hash table buckets
		if (hashBuckets.containsKey(hashVal)) {
			List<WrapperNode> getSet = hashBuckets.get(hashVal);
			
			getSet.add(new WrapperNode(node));
			hashBuckets.put(hashVal, getSet);
		} else {
			List<WrapperNode> newSetNode = new ArrayList<>();
			newSetNode.add(new WrapperNode(node));
			hashBuckets.put(hashVal, newSetNode);
		}
		// adds the Node to the mapping of nodes to the hashcodes
		nodeHashcodeMap.put(node, hashVal);
	}

    /**
     * generates a sorted integer array from all hashcodes in the table
     */
	public void updateKeySet() {
		int[] hashSortedF = new int[hashBuckets.size()];
		Set<Integer> keySet = hashBuckets.keySet();
		int i = 0;
		for (int key : keySet) {
			hashSortedF[i] = key;
			i++;
		}
		hashSorted = hashSortedF;
	}

    /**
     * return the hashcode of given node
     */
	public int getNodeHash(Node node) {
		return this.nodeHashcodeMap.get(node);
	}

    /**
     *
     * return the index of the left nearest bucket of given hashcode
     * returns null if there is no such bucket
     */
	public Integer getLeftNearBucket(int hash) {
		// finding exact and near miss hashcodes
        int binarySearch = Arrays.binarySearch(this.hashSorted, hash);
        if (binarySearch > 0 ) {
            if (binarySearch + 1 < hashSorted.length)
                return binarySearch - 1;
            else
                return null;
        } else if (binarySearch == 0) {
            if (this.hashSorted.length == 1) {
                return binarySearch + 1;
            }
        } else {
            int val = -1 * (binarySearch + 1);
            if (val == 0) {
                return null;
            } else{
                return val - 1;
            }
        }
        return null;
	}

    /**
     * return the index of the right nearest bucket of given hashcode
     * returns null if there is no such bucket
     */
	public Integer getRightNearBucket(int hash) {
		int binarySearch = Arrays.binarySearch(this.hashSorted, hash);
		if (binarySearch >= 0 ) {
			if (binarySearch + 1 < hashSorted.length)
				return binarySearch + 1;
			else
				return null;
		} else {
			int val = -1 * (binarySearch + 1);
			if (val == 0) {
				return val;
			} else if (val != this.hashSorted.length) {
				return val;
			}
		}
		return null;
	}

    /**
     *
     * return the index of the bucket of given hashcode
     * returns null if there is no such bucket
     */
	public Integer getBucket(int hash) {
		int binarySearch = Arrays.binarySearch(this.hashSorted, hash);
		if (binarySearch < 0) {
			return null;
		}
		return binarySearch;

	}

    /**
     * given a hash bucket index, returns the hashValue of that bucket
     */
	public int getBucketHashCode(int index) {
		
		return hashSorted[index];
	}

    /**
     * given a hashcode, returns all nodes that have that hash value in ast
     */
	public List<WrapperNode> getAllNodesWithHashCode(int hash) {
		return hashBuckets.get(hash);
	}

    /**
     * Setter for the integer array for testing
     */
    public void setHashSorted(int[] hashSorted) {
        this.hashSorted = hashSorted;
    }
}

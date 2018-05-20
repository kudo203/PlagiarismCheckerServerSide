package cloneDetectionTechniques.ASTDiff.ast;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.SimpleName;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

// represents the AST of a Java file and its hash table
public class JavaAST {
	// a java file
	private File file;
    // root node of file's AST
	private Node root;
    // hashtable of all nodes in this AST
	private HashTable hashTable;

	public JavaAST(File f) throws FileNotFoundException {
		this.file = f;
		hashTable = new HashTable(this);

		// create AST of file using JavaParser lib
		root = JavaParser.parse(file);

		removeCommentNodes();
        // updates the hashtable
		computeASTHashCode(root);
        // moves the sorted hashcodes to
        // an integer array
		hashTable.updateKeySet();
	}

    /**
     * removes all nodes that are of type "comment"
     */
	private void removeCommentNodes() {
		List<Comment> comments = root.getAllContainedComments();
		List<Comment> removedComments = comments.stream().filter(
				p -> !p.getCommentedNode().isPresent() || p instanceof LineComment || p instanceof JavadocComment)
				.collect(Collectors.toList());
		removedComments.forEach(Node::remove);
	}

    /**
     * given an AST cu, adds the
     * hashvalue of the root + hashvalue of its children ( recursively)
     * and populates the hash table
     */
	private int computeASTHashCode(Node root) {
		int hashVal = 0;

		if (root.getChildNodes().size() == 0) {
			// hash value is the hash of the source code of AST
			hashVal = root.hashCode();
			// hashVal =
			// cu.getTokenRange().get().getBegin().getText().hashCode();
		} else {
			for (Node node : root.getChildNodes()) {
				// skip identifier type nodes
				if (!(node instanceof SimpleName)) {
					// add the hash value of children to hashval
					hashVal += computeASTHashCode(node);
				}
			}
		}
		hashTable.addEntry(hashVal, root);
		return hashVal;
	}

    /**
     * returns the root node of AST
     */
	public Node getRoot() {
		return root;
	}

    /**
     * given a node in this AST
     * returns the hashcode of given node
     */
    public int getNodeHash(Node node) {
		return hashTable.getNodeHash(node);
	}

    /**
     * returns the hash table
     */
	public HashTable getHashTable() {
		return hashTable;
	}

    /**
     * returns all nodes with the given hashcode
     */
	public List<WrapperNode> getAllNodesWithHashcode(int hash) {
		return hashTable.getAllNodesWithHashCode(hash);
	}

}

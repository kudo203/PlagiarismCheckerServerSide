package cloneDetectionTechniques.ASTDiff.ast;

import com.github.javaparser.Position;
import com.github.javaparser.ast.Node;

// Wrapper Class around a node in an AST including the content length
public class WrapperNode implements Comparable {
	// AST node
	private Node node;
	// Euclidean distance span of the code
	// represented by the node
	private Double codeSpan;

	// constructor
	public WrapperNode(Node node) {
		this.node = node;

		// gets the beginning line no. and column
		Position begin = node.getRange().get().begin;
		int beginLine = begin.line;
		int beginColumn = begin.column;

		// gets the ending line no. and column
		Position end = node.getRange().get().end;
		int endLine = end.line;
		int endColumn = end.column;

		// finds the euclidean distance
		this.codeSpan = Math
				.sqrt(Math.pow((beginLine - endLine + 0.0), 2) + Math.pow((beginColumn - endColumn + 0.0), 2));

	}

	/*
	 * Overriding function to generate a comparator for ordering the objects of
	 * this class in descending order
	 */
	@Override
	public int compareTo(Object nodeNew) {
		return (((WrapperNode) nodeNew).codeSpan).compareTo(this.codeSpan);
	}

    public Double getCodeSpan() {
        return codeSpan;
    }

    /**
     * Getter for codeSpan for testing
     * @return
     */



	// returns the node
	public Node getNode() {
		return node;
	}
}

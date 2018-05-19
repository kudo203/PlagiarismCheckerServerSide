package engine;


// represents a continuous part of a source file
public class CodeStub {
    //first line number of the code stub
    private int startLine;
    //last line number of the code stub
    private int endLine;

    //constructor
    public CodeStub(int startLine,int endLine){
        this.startLine = startLine;
        this.endLine = endLine;
    }

    /**
     * getter for startLine
     * @return
     */
    public int getStartLine() {
        return startLine;
    }

    /**
     * getter for endLine
     * @return
     */
    public int getEndLine() {
        return endLine;
    }

    /**
     * checks if the stub is equal to this object
     * @param stub - Different code stub which is to
     *             be compared
     */
    public boolean isEqual(CodeStub stub){
        if(stub.startLine==this.startLine && stub.endLine==this.endLine)
            return true;
        return false;
    }

    /**
     * Checks if the given CodeStub object's lines
     * overlap with this object
     * @param cs - CodeStub to be compared
     */
	public boolean overLaps(CodeStub cs) {
		if (startLine >= cs.getStartLine() && endLine <= cs.getEndLine())
			return true;
		else if (startLine < cs.getStartLine() && endLine > cs.getEndLine())
			return true;
		return false;
	}

    /**
     * Gets the code span of this stub
     */
	public int getSpan() {
		return endLine - startLine + 1;
	}

    /**
     * Overriding method for toString
     */
	@Override
	public String toString() {
		return " [" + startLine + " - " + endLine+ "]";
	}
}

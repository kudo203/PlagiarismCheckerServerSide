package engine;

// represents a clone between two source files
public class Clone {
	// Code Stub in file1
	private CodeStub file1Stub;
	// Code Stub in file2
	private CodeStub file2Stub;
    //Clone percentage between the two stubs
	private double similarity;

	// constructor
	public Clone(CodeStub file1Stub, CodeStub file2Stub, double similiarity) {
		this.file1Stub = file1Stub;
		this.file2Stub = file2Stub;
		this.similarity = similiarity;
	}

    /**
     * getter for file1Stub
     */
	public CodeStub getFile1Stub() {
		return file1Stub;
	}

    /**
     * getter for file2Stub
     */
	public CodeStub getFile2Stub() {
		return file2Stub;
	}

    /**
     * getter for similarity fraction
     */
	public double getSimilarity() {
		return similarity;
	}

    /**
     * Checks if the codestubs, in a clone, overlap each other
     */
	public boolean overLaps(Clone clone) {
		return (file1Stub.overLaps(clone.getFile1Stub()) || file2Stub.overLaps(clone.getFile2Stub()));
	}

    /**
     * Checks if this object is a better match than
     * given clone
     */
	public boolean isBetterMatch(Clone clone) {
		return (similarity > clone.getSimilarity());
	}
	
	@Override
	public String toString() {
		return "Clone : f1 " + file1Stub + " f2 : " + file2Stub + " sim: " + similarity;
	}

    /**
     * Switches file pointers
     */
	public void switchFiles() {
		CodeStub t = file1Stub;
		file1Stub = file2Stub;
		file2Stub = t;
		
	}
}
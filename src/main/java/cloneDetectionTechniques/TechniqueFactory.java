package cloneDetectionTechniques;

import java.io.File;

import cloneDetectionTechniques.ASTDiff.AstDiff;

// a singleton factory class for CloneTechnique
public class TechniqueFactory {

	// reusable instance
	private static TechniqueFactory instance = null;

	// constructor
	private TechniqueFactory() {
	}

    /**
     * returns the singleton instance of class
     */
	public static TechniqueFactory getInstance() {
		if (instance == null) {
			instance = new TechniqueFactory();
		}
		return instance;
	}

    /**
     * factory method that creates a AstDiff clone detection technique
     * @param f1 - file object of file 1
     * @param f2 - file object of file 2
     */
	public CloneTechnique makeAstDiff(File f1, File f2) {
		return new AstDiff(f1, f2);
	}
}

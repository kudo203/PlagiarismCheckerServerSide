package cloneDetectionTechniques;

import cloneDetectionTechniques.ASTDiff.AstDiff;
import com.github.javaparser.ParseProblemException;

import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;

// an interface for a visitor that visits different clone detection techniques
public class TechniqueVisitor {

	/**
	 * visits ASTDiff detection techinque and finds all clones using ASTDiff
	 * @param cloneFinder - One of the clone finding techiniques
	 */
	public void visit(AstDiff cloneFinder) throws FileSystemNotFoundException, ParseProblemException, IOException{
		cloneFinder.findClones();
	}
	// ... for other types will be added
}

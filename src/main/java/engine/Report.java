package engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cloneDetectionTechniques.TechniqueFactory;

//Represents Report class which consists of all the matches between any pair 
// of files of two projects
public class Report {

	// List of matches for all permutations of files in the projects
	private List<Match> matches;

	// constructor
	public Report() {
		matches = new ArrayList<Match>();
	}

	/**
	 * finds matches and updates the 'matches' list
	 * @param f1:file 1
	 * @param f2:file 2
	 * @param tf: Factory for different clone techniques
	 */
	public void findMatches(File f1, File f2, TechniqueFactory tf) throws IOException {
		Match newMatches = new Match(f1, f2, tf);
		matches.add(newMatches);
	}

    /**
     * Getter method for list of total matches
     * @return - List of total matches of different
     * Techniques
     */
	public List<Match> getMatches() {
		matches.sort((m1 , m2) -> m2.getSimilarity().compareTo(m1.getSimilarity()));
		return matches;
	}
}
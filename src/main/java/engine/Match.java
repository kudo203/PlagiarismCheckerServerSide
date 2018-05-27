package engine;

import cloneDetectionTechniques.CloneTechnique;
import cloneDetectionTechniques.TechniqueFactory;
import cloneDetectionTechniques.TechniqueVisitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

//Represents a match found between two source
// files using different clone detection techniques
public class Match {
	// File object for f1
	private File f1;
	// File name along with directory
    private String f1_name;
	// File object for f2
	private File f2;
    // File name along with directory
    private String f2_name;
    // List of result of clones found using different detection techniques
	private List<CloneTechnique> cloneTechniques;
    //String representation of the f1 content
	private List<String> f1_content;
    //String representation of the f2 content
	private List<String> f2_content;
    //Fraction of the file classified as clones
	private double similarity;

	// constructor and generates similarities
	public Match(File f1, File f2, TechniqueFactory sf) throws IOException {
		this.f1 = f1;
		this.f1_name = Paths.get(f1.getParentFile().getName(), f1.getName()).toString();
        this.f2 = f2;
        this.f2_name = Paths.get(f2.getParentFile().getName(), f2.getName()).toString();
		this.f1_content = populateFileContent(this.f1);
		this.f2_content = populateFileContent(this.f2);
		cloneTechniques = new ArrayList<CloneTechnique>();
		findAllClones(sf);
	}

    /**
     *
     * populates the list with different similarities
     * @param sf - factory of techniques
     * @throws IOException
     */
	private void findAllClones(TechniqueFactory sf) throws IOException {
		TechniqueVisitor visitor = new TechniqueVisitor();

		//Finds clones by visiting nodes in f2 and comparing
        //buckets in f1
        CloneTechnique astDiffDetector = sf.makeAstDiff(f1, f2);
        astDiffDetector.accept(visitor);

        //Finds clones by visiting nodes in f1 and comparing
        //buckets in f2
		CloneTechnique astDiffDetector2 = sf.makeAstDiff(f2, f1);
		astDiffDetector2.accept(visitor);


		double sim1 = computeSimilarity(astDiffDetector, false);
		double sim2 = computeSimilarity(astDiffDetector2, true);
		if (sim1 > sim2)
			cloneTechniques.add(astDiffDetector);
		else {
			cloneTechniques.add(astDiffDetector2);
			astDiffDetector2.switchFiles();
		}
		similarity = Math.max(sim1, sim2);
	}

    /**
     * returns the match similarity fraction as Max( # lines matched / # lines in each file )
     * switches the files if sw = true
     * @param ct : a clone detection technique
     * @param sw : indicates file switch flag
     * @return
     */
	private double computeSimilarity(CloneTechnique ct, boolean sw) {
		double matcheLinesf1 = 0;
		double matcheLinesf2 = 0;

		List<Clone> cs = ct.getClones();
		for (Clone c2 : cs) {
			//increments the line span of the clone multiplied by
            //percentage of clone found in the stub.
		    matcheLinesf1 += c2.getFile1Stub().getSpan() * c2.getSimilarity();
			matcheLinesf2 += c2.getFile2Stub().getSpan() * c2.getSimilarity();
		}

        double f1lines = f1_content.size();
        double f2lines = f2_content.size();

        if (sw) {
            f2lines = f1_content.size();
            f1lines = f2_content.size();

        }
        double simf1 = matcheLinesf1 / f1lines;
        double simf2 = matcheLinesf2 / f2lines;

        return Math.max(simf1, simf2);

	}

    /**
     * Populates the f_content with the lines of code
     * in the given file
     * @param f - File Object
     * @return - List of lines present in the given file
     */
	private List<String> populateFileContent(File f) throws IOException {
		List<String> file_content = new ArrayList<String>();
		BufferedReader in = new BufferedReader(new FileReader(f));
		String str;
		while ((str = in.readLine()) != null) {
			file_content.add(str);
		}
		in.close();
		return file_content;
	}

    /**
     *Getter for list of similarities derived from
     * different techniques
     */
	public List<CloneTechnique> getClones() {
		return cloneTechniques;
	}
	/**
     * getter for File f1
     */
	//public File getF1() {
	//	return f1;
	//}
    public String getF1_name() {
        return this.f1_name;
    }

    /**
     * getter for File f2
     */
	//public File getF2() {
	//	return f2;
	//}

    public String getF2_name(){
        return this.f2_name;
    }

    /**
     * getter for File content of f2
     */
	public List<String> getF2_content() {
		return f2_content;
	}

    /**
     * getter for File content of f1
     */
    public List<String> getF1_content() {
		return f1_content;
	}

    /**
     * getter for similarity fraction
     */
	public Double getSimilarity() {
		return similarity;
	}
}
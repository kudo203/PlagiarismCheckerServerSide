package cloneDetectionTechniques;

import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;
import java.util.List;

import engine.Clone;

// interface for different clone detection techniques
public interface CloneTechnique {

    // finds all the clones using its detection technique
    void findClones() throws FileSystemNotFoundException,IOException;

    // accepts a visitor
    void accept(TechniqueVisitor v);

    // getter for the clones found
    public List<Clone> getClones();

    // getter for the type of technique
    public String getType();

    // switches the file order
	void switchFiles();
}

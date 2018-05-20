package engine;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

// represents a submission of multiple source files
public class Project {
    // absolute or relative path from plagPolice.jar
    private String srcDirPath;
    // valid suffixes for project files
    private static String[] suffixes = { ".java", ".jav", ".JAVA", ".JAV" };
    // file objects of project
    private List<File> files;

    //constructor
    public Project(String dir) throws Throwable {
        files = new ArrayList<File>();

        // check directory validation
        File f = new File(dir);
        if (!f.canRead())
            throw new Exception("\"" + dir + "\" is not a file/directory!");

        // f is a single file
        if (!f.isDirectory()) {
            addFile(f);
            srcDirPath = f.getAbsoluteFile().getParent();
        }

        // f is a directory
        else {
            lookupDir(f, "");
            srcDirPath = dir;
        }

    }

    /**
     * Getter method to get list of files
     * @return List of files
     */
    public List<File> getFiles() {
        return files;
    }

    /**
     * Checks the vaidity of a file and updates the
     * list of files
     * @param f - File object
     */
    private void addFile(File f) throws Exception {
        if (!isValidFile(f.getName()))
            throw new Exception("\"" + f.getName() + "\" is not a valid java file.");
        if (!isParseableFile(f, ""))
            throw new Exception("\"" + f.getName() + "\" is not a parseable java file.");
        files.add(new File(f.getName()));
    }

    /**
     * Checks if the file is valid java file finding
     * clones
     * @param name - File name
     */
    private boolean isValidFile(String name) {
		 for (int i = 0; i < suffixes.length; i++)
            if (name.endsWith(suffixes[i]))
                return true;
        return false;
    }

    /**
     * Checks if the file can be parsed by the
     * JavaParser
     * @param dir - File object
     * @param name - File Name
     */
    private boolean isParseableFile(File dir, String name) {
    	File f = new File(dir, name);
    	try {
			JavaParser.parse(f);
		} catch (ParseProblemException | FileNotFoundException e) {
			return false;
		}
    	return true;
    }

    /**
     * recursively read in all the files in dir/subdir
     * @param dir - File object
     * @param subDir - Subdirectory
     */
    private void lookupDir(File dir, String subDir) throws Throwable {
        File prtDir = new File(dir, subDir);
        if (!prtDir.isDirectory())
            return;

        // read subdirs
        String[] dirs = prtDir.list();
        if (subDir.equals(""))
            for (int i = 0; i < dirs.length; i++)
                lookupDir(dir, subDir + File.separator + dirs[i]);
        else
            for (int i = 0; i < dirs.length; i++)
                lookupDir(dir, dirs[i]);

        String[] newFiles = prtDir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return isValidFile(name) && isParseableFile(dir, name);
            }
        });

        for (String f : newFiles) {
            if (subDir.equals(""))
                files.add(new File(dir, subDir + File.separator + f));
            else
                files.add(new File(dir, f));
        }
    }


}
package engine;

import cloneDetectionTechniques.TechniqueFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

//Generates the report for two projects
public class PlagiarismChecker {

    //Projects
    private static Project p1;
    private static Project p2;
    //factory for similarity functions
    private TechniqueFactory tf;

    //Single Reusable instance of PlagiarismChecker
    private static PlagiarismChecker instance = null;

    /**
     * Method to reuse the singleton instance
     * @param path1 - Project1 path
     * @param path2 - Project2 path
     * @return - reused instance of the object
     * @throws Throwable
     */
    public static PlagiarismChecker getInstance(String path1, String path2) throws Throwable {
        if(instance==null)
            instance = new PlagiarismChecker();
        setProjects(path1,path2);
        return instance;
    }

    /**
     * sets the project objects according to the paths
     * @param path1 - Project1 path
     * @param path2 - Project2 path
     * @throws Throwable
     */
    private static void setProjects(String path1, String path2) throws Throwable {
        if(path1 == null || path2 == null){
            return;
        }
        try{
            p1 = new Project(path1);
            p2 = new Project(path2);
            
        }
        catch(Exception ex){
        	System.out.println(ex.getMessage());
        }
    }

    /**
     * Generates a Report by matching all the permutations of files in two projects.
     * @return - a Report which contains list of Matches
     * @throws IOException
     */
    public Report generateReport() throws IOException{
        if(p1==null || p2 == null)
            return null;
        

        List<File> p1Files = p1.getFiles();
        List<File> p2Files = p2.getFiles();

        Report finalReport = new Report();

        for(File f1: p1Files){
            for(File f2: p2Files){
                tf = TechniqueFactory.getInstance();
                finalReport.findMatches(f1,f2,tf);
            }
        }
        finalReport.sortMatches();
        return finalReport;
    }
}
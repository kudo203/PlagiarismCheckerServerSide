package webService.storage;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class StorageService {

    public final Path project1Location = Paths.get("src").resolve(Paths.get("main")).resolve("resources").resolve("project-1");
    public final Path project2Location = Paths.get("src").resolve(Paths.get("main")).resolve("resources").resolve("project-2");

    public void storeProject1(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), this.project1Location.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            throw new RuntimeException("FAIL!");
        }
    }

    public void storeProject2(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), this.project2Location.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            throw new RuntimeException("FAIL!");
        }
    }

    public void deleteAll() throws IOException{
        FileSystemUtils.deleteRecursively(project1Location.toFile());
        FileSystemUtils.deleteRecursively(project2Location.toFile());
    }

    public void deleteOneOne() throws IOException{
        deleteDirectory(new File(this.project1Location.toAbsolutePath().toString()));

        deleteDirectory(new File(this.project2Location.toAbsolutePath().toString()));

        init();
    }

    public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    boolean m = files[i].delete();
                    boolean s = true;
                }
            }
        }
        return (path.delete());
    }

    public void init() {
        try {
            Files.createDirectory(project1Location);
            Files.createDirectory(project2Location);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage!");
        }
    }

    public List<String> getProject1Files(){
        List<String> allFiles = new ArrayList<>();
        File[] files = new File(project1Location.toString()).listFiles();
        for (int i = 0; i < files.length; i++){
            allFiles.add(files[i].getName());
        }
        return allFiles;
    }

    public List<String> getProject2Files(){
        List<String> allFiles = new ArrayList<>();
        File[] files = new File(project2Location.toString()).listFiles();
        for (int i = 0; i < files.length; i++){
            allFiles.add(files[i].getName());
        }
        return allFiles;
    }
}

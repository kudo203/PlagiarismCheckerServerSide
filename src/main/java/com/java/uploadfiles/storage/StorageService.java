package com.java.uploadfiles.storage;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageService {

    private final Path project1Location = Paths.get("src").resolve(Paths.get("main")).resolve("resources").resolve("project-1");
    private final Path project2Location = Paths.get("src").resolve(Paths.get("main")).resolve("resources").resolve("project-2");

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

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(project1Location.toFile());
        FileSystemUtils.deleteRecursively(project2Location.toFile());
    }

    public void init() {
        try {
            Files.createDirectory(project1Location);
            Files.createDirectory(project2Location);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage!");
        }
    }
}

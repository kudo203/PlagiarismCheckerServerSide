package com.java.uploadfiles.controlller;

import com.java.uploadfiles.storage.StorageService;
import com.sun.org.apache.bcel.internal.ExceptionConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FileController {
    @Autowired
    StorageService storageService;

    List<String> files = new ArrayList<String>();

    @RequestMapping(method = RequestMethod.POST, value = "/project1")
    public ResponseEntity<String> handleFileUpload1(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            storageService.storeProject1(file);
            files.add(file.getOriginalFilename());

            message = "You successfully uploaded " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "FAIL to upload " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/project2")
    public ResponseEntity<String> handleFileUpload2(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            storageService.storeProject2(file);
            files.add(file.getOriginalFilename());

            message = "You successfully uploaded " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "FAIL to upload " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }

    @RequestMapping("/deleteAll")
    public ResponseEntity<String> deleteAllFiles(){
        try{
            storageService.deleteAll();
            storageService.init();
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch(Exception ex){
            return new ResponseEntity<String>(HttpStatus.EXPECTATION_FAILED);
        }
    }
}


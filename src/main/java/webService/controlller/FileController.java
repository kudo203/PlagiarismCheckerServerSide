package webService.controlller;

import com.fasterxml.jackson.databind.ObjectMapper;
import engine.PlagiarismChecker;
import engine.Report;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RestController;
import webService.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class FileController {
    @Autowired
    StorageService storageService;

    /**
     * get all matches between files
     * @return Json as string having all the matches
     * @throws Throwable
     */
    @RequestMapping(value = "/api/matches", method = RequestMethod.GET)
    public String getAllMatches() throws Throwable {
        PlagiarismChecker instance = PlagiarismChecker.getInstance( storageService.project1Location.toString(),
                storageService.project2Location.toString());
        Report r = instance.generateReport();
        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString(r);
        JSONObject json = new JSONObject(result);
        Object matches = json.get("matches");
        return matches.toString();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/project1")
    public ResponseEntity<String> handleFileUpload1(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            storageService.storeProject1(file);

            message = "You successfully uploaded " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "FAIL to upload " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/project2")
    public ResponseEntity<String> handleFileUpload2(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            storageService.storeProject2(file);

            message = "You successfully uploaded " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "FAIL to upload " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }

    @RequestMapping("/api/deleteAll")
    public ResponseEntity<String> deleteAllFiles(){
        try{
            storageService.deleteOneOne();
            //storageService.init();
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch(Exception ex){
            return new ResponseEntity<String>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping("/api/getProject1Files")
    public List<String> getProject1Files(){
        return storageService.getProject1Files();
    }

    @RequestMapping("/api/getProject2Files")
    public List<String> getProject2Files(){
        return storageService.getProject2Files();
    }
}


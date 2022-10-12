package com.futech.entertainment.packages.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {
     
    public static void saveFile(String uploadDir, String fileName,MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
         
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {        
            throw new IOException("Could not save image file: " + fileName, ioe);
        }      
    }
    
    public static void deleteFile(String filename) throws IOException {
        Path fileToDeletePath = Paths.get("src/main/resources/static/imgs/"+filename);
        if (Files.exists(fileToDeletePath)) {
             Files.delete(fileToDeletePath);
        }
    }
}

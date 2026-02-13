package com.project.web.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileStorageService {
    String storeFile(MultipartFile multipartFile) throws IOException;
    void deleteFile(String filename);
}
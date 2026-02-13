package com.project.web.util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/*
 * FileStore 클래스
 * 업로드된 파일을 저장해주는 유틸리티 클래스
 * 
 * */

@Component // 스프링이 관리하는 '일꾼(Bean)'으로 등록
public class LocalFileStore implements FileStorageService {
	// 파일을 저장할 경로입니다. 설정 파일(yml)에 없으면 기본값으로 C드라이브 경로를 사용
    //@Value("${file.dir:C:/web-project/images/}")
    
	@Value("${file.dir:/app/images/}")
	private String fileDir;

    // 파일을 받아서 저장하고, '저장된 파일명'을 돌려주는 메인 함수
    public String storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename(); // 예: cat.png
        String storeFileName = createStoreFileName(originalFilename); // 예: uuid-cat.png
        
        // 폴더가 없으면 만듭니다.
        File directory = new File(fileDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 실제 파일 저장 (지정된 경로로 이동)
        multipartFile.transferTo(new File(fileDir + storeFileName));
        
        return storeFileName; // DB에는 이 이름만 저장합니다.
    }

    // 파일명 중복을 막기 위해 UUID(랜덤 문자열)를 붙여줍니다.
    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename); // 확장자(.png) 추출
        return UUID.randomUUID().toString() + "." + ext;
    }

    // 확장자만 떼어내는 보조 함수
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        if(pos == -1)
        	return "";
        return originalFilename.substring(pos + 1);
    }
    
 // 파일 삭제 메서드 (상품 수정 시 기존 이미지 삭제용)
    public void deleteFile(String filename) {
        if (filename == null || filename.isEmpty()) {
            return;
        }
        
        //  경로 생성
        File file = new File(fileDir + filename);
        
        if (file.exists()) {
            if (file.delete()) {
                // 로그: 파일 삭제 성공
                // log.info("File deleted: {}", filename); 
            } else {
                // 로그: 파일 삭제 실패 (권한 문제 등)
                // log.warn("Failed to delete file: {}", filename);
            }
        }
    }
}

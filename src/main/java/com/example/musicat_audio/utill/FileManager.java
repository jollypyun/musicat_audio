package com.example.musicat_audio.utill;

import com.example.musicat_audio.domain.MetaFile;
import com.example.musicat_audio.exception.customException.UploadFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.UUID;

@Slf4j
@Component
public class FileManager {

    //https://hello-bryan.tistory.com/343

    //@Value("${spring.servlet.multipart.location}")
    //@Value("${file.dir}")

    private String uploadPath = "/upload/";

    //만약 저장 경로 폴더가 없으면 생성
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch(IOException e) {
            throw new RuntimeException("Could not create upload folder!");
        }
    }
    // 파일 저장
    public MetaFile uploadFile(MultipartFile multipartFile) {
        if(multipartFile.isEmpty()) {
            throw new UploadFileException("Error : file is empty");
        }

        Path root = Paths.get(uploadPath);

        if(!Files.exists(root))
            init();

        String originalFileName = multipartFile.getOriginalFilename();
        String ext = extract(originalFileName); // 원본 파일 확장자 추출
        String systemFileName = createSystemFileName(originalFileName, ext);
        Long fileSize = multipartFile.getSize();


        try{
            multipartFile.transferTo(new File("/upload/" + systemFileName));
        } catch (Exception e) {
            log.info("fileManager multipartFile transferTo error {}", e);
            throw new UploadFileException("Error : file transfer fail");
        }

        return new MetaFile(originalFileName, systemFileName, ext, fileSize);
    }

    // 서버에서 관리할 파일 이름 생성
    private String createSystemFileName(String originalFileName, String ext) {
        String uuid = UUID.randomUUID().toString(); // 랜덤 UUID 생성
        return uuid + "." + ext;

        // 테스트하고 교체
        //return new String(
        // UUID.randomUUID().toString()
        // + originalFileName.substring(originalFileName.lastIndexOf("." + 1)));
    }

    // 원본 파일 확장자 추출
    private String extract(String originalFileName) {
        int pos = originalFileName.lastIndexOf(".");
        return originalFileName.substring(pos + 1);
    }
}
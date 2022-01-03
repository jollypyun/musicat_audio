package com.example.musicat_audio.controller;

import com.example.musicat_audio.domain.MetaFile;
import com.example.musicat_audio.utill.FileManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import reactor.core.publisher.Mono;

import javax.persistence.EntityManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@Slf4j
public class TestController {


    private EntityManager em;

    @Autowired
    public TestController(EntityManager em) {
        this.em = em;
    }

    @PostMapping(value = "uploadFileTemp")
    public ResponseEntity<String> uploadFileTemp(MultipartFile file) throws IOException {

        System.out.println("upload file");
        if (!file.isEmpty()) {
            //log.debug("file org name = {}", file.getOriginalFilename());
            System.out.println("file org name = {} " + file.getOriginalFilename());
            //log.debug("file content type = {} ", file.getContentType());
            System.out.println("file content type = {} " + file.getContentType());
            file.transferTo(new File(file.getOriginalFilename()));
        } else {
            //log.debug("file is empty");
            System.out.println("file is empty");

        }
        return new ResponseEntity<>("", HttpStatus.OK);
    }


//    @Transactional
//    @PostMapping(value = "uploadFile")
//    public ResponseEntity<String> upload(MultipartFile file) {
//        FileManager temp = new FileManager();
//        try {
//            MetaFile metafile = temp.uploadFile(file);
//            em.persist(metafile);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return new ResponseEntity<>("", HttpStatus.OK);
//    }

    @GetMapping(value = "/data")
    public ResponseEntity<StreamingResponseBody> streamData() {
        StreamingResponseBody responseBody = response -> {
            for (int i = 1; i <= 1000; i++) {
                try {
                    Thread.sleep(10);
                    response.write(("data stream line = " + i + "\n").getBytes());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseBody);
    }

    /*
    public static final String VIDEO_PATH = "/static/videos";
    public static final String AUDIO_PATH = "d:\\temp\\spring_uploaded_files";
    public static final int BYTE_RANGE = 128; // increase the byterange from here

    @GetMapping("/videos/{fileName}")
    public Mono<ResponseEntity<byte[]>> streamVideo(@RequestHeader(value = "Range", required = false) String httpRangeList,
                                                    @PathVariable("fileName") String fileName) {
        return Mono.just(getContent(VIDEO_PATH, fileName, httpRangeList, "video"));
    }

    @GetMapping("/audios/{fileName}")
    public Mono<ResponseEntity<byte[]>> streamAudio(@RequestHeader(value = "Range", required = false) String httpRangeList,
                                                    @PathVariable("fileName") String fileName) {
        return Mono.just(getContent(AUDIO_PATH, fileName, httpRangeList, "audio"));
    }

    private ResponseEntity<byte[]> getContent(String location, String fileName, String range, String contentTypePrefix) {
        long rangeStart = 0;
        long rangeEnd;
        byte[] data;
        Long fileSize;
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        //String tempPath = new String("C:\\Users\\MZC\\IdeaProjects\\musicat_audio\\src\\main\\resources\\static\\upload\\audio");
        String tempPath = new String("d:\\temp\\spring_uploaded_files");
        try {
            fileSize = Optional.ofNullable(fileName)
                    //.map(file -> Paths.get(getFilePath(location), file))
                    .map(file -> Paths.get(tempPath, file))
                    .map(this::sizeFromFile)
                    .orElse(0L);
            if (range == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .header("Content-Type", contentTypePrefix + "/" + fileType)
                        .header("Content-Length", String.valueOf(fileSize))
                        .body(readByteRange(tempPath, fileName, rangeStart, fileSize - 1));
            }
            String[] ranges = range.split("-");
            rangeStart = Long.parseLong(ranges[0].substring(6));
            if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]);
            } else {
                rangeEnd = fileSize - 1;
            }
            if (fileSize < rangeEnd) {
                rangeEnd = fileSize - 1;
            }
            data = readByteRange(tempPath, fileName, rangeStart, rangeEnd);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header("Content-Type", contentTypePrefix +
                        "/" + fileType)
                .header("Accept-Ranges", "bytes")
                .header("Content-Length", contentLength)
                .header("Content-Range", "bytes" + " " + rangeStart + "-" + rangeEnd + "/" + fileSize)
                .body(data);
    }

    public byte[] readByteRange(String location, String filename, long start, long end) throws IOException {
        //Path path = Paths.get(getFilePath(location), filename);
        //Path path = Paths.get("C:\\Users\\MZC\\IdeaProjects\\musicat_audio\\src\\main\\resources\\static\\upload\\audio", filename);
        Path path = Paths.get("d:\\temp\\spring_uploaded_files", filename);
        try (InputStream inputStream = (Files.newInputStream(path));
             ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream()) {
            byte[] data = new byte[BYTE_RANGE];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                bufferedOutputStream.write(data, 0, nRead);
            }
            bufferedOutputStream.flush();
            byte[] result = new byte[(int) (end - start) + 1];
            System.arraycopy(bufferedOutputStream.toByteArray(), (int) start, result, 0, result.length);
            return result;
        }
    }

    private String getFilePath(String location) {
        URL url = this.getClass().getResource(location);
        System.out.println("location : " + location);
        System.out.println("url : " + url);
        return new File(url.getFile()).getAbsolutePath();
    }

    private Long sizeFromFile(Path path) {
        try {
            return Files.size(path);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0L;
    }
    */

}

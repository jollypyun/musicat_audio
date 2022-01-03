package com.example.musicat_audio.controller;

import com.example.musicat_audio.domain.MetaFile;
import com.example.musicat_audio.domain.Music;
import com.example.musicat_audio.service.MusicService;
import com.example.musicat_audio.utill.FileManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/api/music/")
public class AudioController {

    public static final String AUDIO_PATH = "d:\\temp\\spring_uploaded_files";
    public static final int BYTE_RANGE = 128; // increase the byterange from here

    private MusicService musicService;
    public AudioController(MusicService musicService) {
        this.musicService = musicService;
    }

    @GetMapping("/hello")
    public String index() {
        return "hello";
    }

    @PostMapping(value = "uploadFile")
    public ResponseEntity<String> upload(@RequestParam("audio") MultipartFile file, @RequestParam("image") MultipartFile imagefile, @RequestParam("title") String title,
                                         @RequestParam("memberNo") int memberNo, @RequestParam("articleNo") int aritlceNo) {
        FileManager temp = new FileManager();
        String fileName = "there is no file.";
        try {
            MetaFile metafile_music = null;
            MetaFile metafile_image = null;
            if(file != null)
                metafile_music = temp.uploadFile(file);
            if(imagefile != null)
                metafile_image = temp.uploadFile(imagefile);
            
            fileName = musicService.saveMusic(metafile_music, metafile_image, title, memberNo, aritlceNo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(fileName + "title : " + title, HttpStatus.OK);
    }

    @DeleteMapping("delete/{articleNo}")
    public ResponseEntity<String> deleteMusic(@PathVariable("articleNo") int articleNo) {

        musicService.deleteMusic(articleNo);

        return new ResponseEntity<>("delete success", HttpStatus.OK);
    }
    @GetMapping("get/{id}")
    public String findMusic(@PathVariable("id") Long musicId) {

        Music music = musicService.findMusic(musicId);
        return music.getFile().getSystemFileName();
    }
    @GetMapping("streaming/{fileName}")
    public Mono<ResponseEntity<byte[]>> streamAudio(@RequestHeader(value = "Range", required = false) String httpRangeList,
                                                    @PathVariable("fileName") String fileName) {
        return Mono.just(getContent(AUDIO_PATH, fileName, httpRangeList, "audio"));
    }

    // 나중에 FileManager 로 뺄 것
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
}

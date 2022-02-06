package com.example.musicat_audio.controller;

import com.example.musicat_audio.domain.MetaFile;
import com.example.musicat_audio.domain.Music;
import com.example.musicat_audio.domain.Playlist;
import com.example.musicat_audio.exception.customException.MusicNotFoundException;
import com.example.musicat_audio.service.MusicService;
import com.example.musicat_audio.service.PlaylistService;
import com.example.musicat_audio.utill.FileManager;

import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.swing.text.html.parser.Entity;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/api/")
@Validated
public class AudioController {

    //public static final String AUDIO_PATH = "d:\\temp\\spring_uploaded_files";

    public static final String AUDIO_PATH = "/upload/";

    public static final int BYTE_RANGE = 128; // increase the byterange from here

    private MusicService musicService;
    private PlaylistService playlistService;

    public AudioController(MusicService musicService, PlaylistService playlistService) {
        this.musicService = musicService;
        this.playlistService = playlistService;
    }

    // 연결 확인용
    @GetMapping("hello")
    public String index() {
        return "hello";
    }

    @PostMapping(value = "music")
    public EntityModel<Music> upload(@RequestParam("audio") MultipartFile file, @RequestParam("image") MultipartFile imagefile, @RequestParam("title") String title,
                                     @RequestParam("memberNo") /*@Min(6)*/ int memberNo) {

        System.out.println("upload");
        FileManager temp = new FileManager();
        String fileName = "there is no file.";
        Music music = null;

        MetaFile metafile_music = null;
        MetaFile metafile_image = null;
        if (file != null)
            metafile_music = temp.uploadFile(file);
        if (imagefile != null)
            metafile_image = temp.uploadFile(imagefile);

        music = musicService.saveMusic(metafile_music, metafile_image, title, memberNo);


        EntityModel<Music> entityModel = EntityModel.of(music);
        entityModel.add(linkTo(methodOn(this.getClass()).streamAudio(null, music.getFile().getSystemFileName())).withRel("musicResourceURL"));
        entityModel.add(linkTo(methodOn(this.getClass()).streamAudio(null, music.getThumbnail().getFile().getSystemFileName())).withRel("imageResourceURL"));

        return entityModel;
    }

    @PutMapping("music/{musicId}/{articleNo}")
    public ResponseEntity<String> connectToArticle(@PathVariable("musicId") Long musicId, @PathVariable("articleNo") int articleNo) {

        musicService.connectToArticle(musicId, articleNo);

        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @GetMapping("musics/article/{articleNo}")
    public List<EntityModel<Music>> findMusics(@PathVariable int articleNo) {

        List<Music> musics = musicService.findMusics(articleNo);

        return musics.stream().map(music -> {
            // Java Stream을 이용하여 각 Music 객체의 엔티티 모델 생성.
            EntityModel<Music> entityModel = EntityModel.of(music);
            // 각 엔티티 모델마다 링크 추가.
            log.info("music : " + music.getFile().getSystemFileName());
            entityModel.add(linkTo(methodOn(this.getClass()).streamAudio(null, music.getFile().getSystemFileName())).withRel("musicResourceURL"));
            entityModel.add(linkTo(methodOn(this.getClass()).streamAudio(null, music.getThumbnail().getFile().getSystemFileName())).withRel("imageResourceURL"));
//        );
            return entityModel;
            // 컬렉션으로 반환.
        }).collect(Collectors.toList());
    }

    @DeleteMapping("musics/article/{articleNo}")
    public ResponseEntity<String> deleteMusicByArticleNo(@PathVariable("articleNo") int articleNo) {

        musicService.deleteMusicByArticleNo(articleNo);

        return new ResponseEntity<>("delete success", HttpStatus.OK);
    }

    @GetMapping("musics/file/{fileName}")
    public Mono<ResponseEntity<byte[]>> streamAudio( @RequestHeader(value = "Range", required = false) String httpRangeList,
                                                     @PathVariable("fileName") String fileName) {
        return Mono.just(getContent(AUDIO_PATH, fileName, httpRangeList, "audio"));
        //return Mono.just(getContent(AUDIO_PATH, fileName, null, "audio"));

    }

    @GetMapping("musics/id/{id}")
    public EntityModel<Music> findMusic(@PathVariable("id") Long musicId) {

        Music music = musicService.findMusic(musicId);

        if (music == null)
            throw new MusicNotFoundException("there is no music");

        EntityModel<Music> entityModel = EntityModel.of(music);
        entityModel.add(Link.of("/whatever", "list"));
        return entityModel;
        //return "http://localhost:20000/api/musics/find/" + music.getFile().getSystemFileName();
    }

    @DeleteMapping("musics/id/{musicId}")
    public ResponseEntity<String> deleteMusicByMusicId(@PathVariable("musicId") Long musicId) {

        musicService.deleteMusicByMusicId(musicId);
        return new ResponseEntity<>("delete success", HttpStatus.OK);
    }

    // 플레이리스트 상세 불러오기
    @GetMapping("detailPlaylists/{playlistKey}")
    public List<EntityModel<Music>> findDetailPlaylist(@PathVariable String playlistKey) {
        log.info("playlistNo : " + playlistKey);
        List<Music> musics = playlistService.showDetailPlaylist(playlistKey);
        for (Music m : musics) {
            log.info("what the heck : " + m.getTitle());
        }
        return musics.stream().map(music -> {
            // Java Stream을 이용하여 각 Music 객체의 엔티티 모델 생성.
            EntityModel<Music> entityModel = EntityModel.of(music);
            // 각 엔티티 모델마다 링크 추가.
            log.info("music : " + music.getFile().getSystemFileName());
            entityModel.add(linkTo(methodOn(this.getClass()).streamAudio(null, music.getFile().getSystemFileName())).withRel("musicResourceURL"));
            entityModel.add(linkTo(methodOn(this.getClass()).streamAudio(null, music.getThumbnail().getFile().getSystemFileName())).withRel("imageResourceURL"));
//        );
            return entityModel;
            // 컬렉션으로 반환.
        }).collect(Collectors.toList());
    }

    // 플레이리스트안에 곡 넣기
    @PostMapping("playlists/musics")
    public List<EntityModel<Music>> pushMusicTo(@RequestBody Map<String, Object> map) {
        log.info("map : " + map);
        Playlist playlist = playlistService.addMusicsToPlaylist(map);
        List<Music> musics = playlistService.showDetailPlaylist(playlist.getId());
        log.info("len : " + musics.size());
        return musics.stream().map(music -> {
            // Java Stream을 이용하여 각 Music 객체의 엔티티 모델 생성.
            EntityModel<Music> entityModel = EntityModel.of(music);
            // 각 엔티티 모델마다 링크 추가.
            log.info("music : " + music.getFile().getSystemFileName());
            entityModel.add(linkTo(methodOn(this.getClass()).streamAudio(null, music.getFile().getSystemFileName())).withRel("musicResourceURL"));
            entityModel.add(linkTo(methodOn(this.getClass()).streamAudio(null, music.getThumbnail().getFile().getSystemFileName())).withRel("imageResourceURL"));
//        );
            return entityModel;
            // 컬렉션으로 반환.
        }).collect(Collectors.toList());
    }

    // 나중에 FileManager 로 뺄 것
    private ResponseEntity<byte[]> getContent(String location, String fileName, String range, String contentTypePrefix) {
        long rangeStart = 0;
        long rangeEnd;
        byte[] data;
        Long fileSize;
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);

        String tempPath = new String("/upload/");
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
        Path path = Paths.get("/upload/", filename);
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

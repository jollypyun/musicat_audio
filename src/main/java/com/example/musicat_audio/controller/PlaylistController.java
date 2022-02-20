package com.example.musicat_audio.controller;

import com.example.musicat_audio.domain.MetaFile;
import com.example.musicat_audio.domain.Playlist;
import com.example.musicat_audio.service.PlaylistService;
import com.example.musicat_audio.utill.FileManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

@RestController
@RequestMapping("/api/")
@Slf4j
public class PlaylistController {
    public static final String IMAGE_PATH = "d:\\temp\\spring_uploaded_files";
    public static final int BYTE_RANGE = 128; // increase the byterange from here

    private PlaylistService playlistService;
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping("/playlists/images/{systemfilename}")
    public Mono<ResponseEntity<byte[]>> streamImage(@PathVariable("systemfilename") String systemfilename) {
        return Mono.just(getContent(IMAGE_PATH, systemfilename, null, "image"));
    }


    // 플레이리스트 생성
    @PostMapping("playlists/create")
    public ResponseEntity<Playlist> createPli(@RequestParam("playlistName") String title, @RequestParam("image") MultipartFile file, @RequestParam("memberNo") int memberNo) {
        log.info("what name : " + file.getOriginalFilename());
        FileManager temp = new FileManager();
        MetaFile meta = new MetaFile();
        List<Playlist> list = playlistService.showPlaylist(memberNo);
        String[] args = list.get(list.size() - 1).getId().split("pl");
        for(String a:args) {
            log.info("s : " + a);
        }
        String playlistKey = memberNo + "pl" + (Integer.valueOf(args[1]) + 1);
        //log.info("playlistKey : " + playlistKey);
        Playlist playlist = new Playlist(playlistKey, title, memberNo);
        try {
            meta = temp.uploadFile(file);
            log.info("meta : ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        playlistService.insertPlaylist(playlist, meta);
        return new ResponseEntity<>(playlist, HttpStatus.OK);
    }

    // 플레이리스트 삭제
    @DeleteMapping("playlists/delete/{memberNo}/{playlistKey}")
    public ResponseEntity<String> deletePli(@PathVariable(name = "memberNo") int memberNo, @PathVariable(name = "playlistKey") String playlistKey) {
        log.info("memberNo : " + memberNo);
        log.info("playlistKey : " + playlistKey);
        playlistService.delPlaylist(playlistKey);
        return new ResponseEntity<>("Playlist is deleted successfully", HttpStatus.OK);
    }

    // 플레이리스트 목록 불러오기
    @GetMapping("playlists/{memberNo}")
    public List<EntityModel<Playlist>> findPlaylist(@PathVariable int memberNo) {
        log.info("memberNo : " + memberNo);
        List<Playlist> lst = playlistService.showPlaylist(memberNo);
        return lst.stream().map(playlist -> {
            EntityModel<Playlist> entityModel = EntityModel.of(playlist);
            entityModel.add(linkTo(methodOn(this.getClass()).streamImage(playlist.getPlaylistImage().getFileNo().getSystemFileName())).withRel("imageResourcURL"));
            return entityModel;
        }).collect(Collectors.toList());
        //return lst;
    }

    // 특정 플레이리스트 안의 곡 빼기
    @DeleteMapping("playlists/pull/{playlistKey}/{musicNos}")
    public ResponseEntity<String> pullMusic(@PathVariable(name = "playlistKey") String playlistKey, @PathVariable(name = "musicNos") String list) {
        log.info("playlistKey : " + playlistKey);
        String[] lst = list.substring(1,list.length()-1).split(",");
        List<Long> deleteList = new ArrayList<Long>();
        for(String l : lst) {
            deleteList.add(Long.valueOf(l.trim()));
        }
        log.info("deleteList : " + deleteList);
        playlistService.removeMusicFromPlaylist(playlistKey, deleteList);
        return null;
    }

    // 플레이리스트 수정
    @PostMapping("playlists/update")
    public Integer changePlaylistName(@RequestParam(value = "image", required = false)MultipartFile img, @RequestParam("playlistKey")String playlistKey, @RequestParam("title") String title) {
        FileManager temp = new FileManager();
        String fileName = "there is no file.";
        Playlist playlist = null;
        MetaFile metaFile_image = null;

        if (img != null) {
            try {
                metaFile_image = temp.uploadFile(img);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int memberNo = playlistService.modifyPlaylist(metaFile_image, title, playlistKey);
        log.info("playlistKey : " + playlistKey);
        log.info("playlistName : " + title);
        log.info("image : " + img.getOriginalFilename());
        log.info("memberNo : " + memberNo);
        return memberNo;
    }

    @GetMapping("onePlaylists/{playlistKey}")
    public Playlist getOnePlaylist(@PathVariable String playlistKey) {
        log.info("playlistKey : " + playlistKey);
        return playlistService.showOnePlaylist(playlistKey);
    }

    // 현재 재생목록에 플레이리스트 자체 넣기
    @PostMapping("playlists/pushNow")
    public Playlist pushPlaylistToNow(@RequestParam("memberNo") int memberNo, @RequestParam("playlistKey") String playlistKey){
        log.info("memberNo : " + memberNo);
        String nowPlaying = memberNo + "pl1";
        log.info("now : " + nowPlaying);
        log.info("playlistKey : " + playlistKey);
        Playlist now = playlistService.addPlaylistToNow(nowPlaying, playlistKey);
        return now;
    }

    // 현재 재생목록 생성
    @PostMapping("playlists/makeNow/{memberNo}")
    public void makeNowPlay(@PathVariable("memberNo") Integer memberNo) {
        log.info("JOIN member : " + memberNo);
        String playlistKey = memberNo + "pl1";
        String playlistName = "현재 재생목록";
        Playlist nowPlaying = new Playlist(playlistKey, playlistName, memberNo);
        this.playlistService.insertNow(nowPlaying);
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

    private Long sizeFromFile(Path path) {
        try {
            return Files.size(path);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0L;
    }
}

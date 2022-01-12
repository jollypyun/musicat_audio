package com.example.musicat_audio.controller;

import com.example.musicat_audio.domain.MetaFile;
import com.example.musicat_audio.domain.Music;
import com.example.musicat_audio.domain.Playlist;
import com.example.musicat_audio.domain.PlaylistImage;
import com.example.musicat_audio.service.PlaylistService;
import com.example.musicat_audio.utill.FileManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/")
@Slf4j
public class PlaylistController {
    public static final String AUDIO_PATH = "d:\\temp\\spring_uploaded_files";
    public static final int BYTE_RANGE = 128; // increase the byterange from here

    private PlaylistService playlistService;
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    // 플레이리스트 생성
    @PostMapping("playlists/create")
    public ResponseEntity<Playlist> createPli(@RequestParam("playlist") Playlist playlist, @RequestParam("image") MultipartFile file) {
        log.info("" + playlist.getPlaylistName());
        playlistService.insertPlaylist(playlist);
        return new ResponseEntity<>(playlist, HttpStatus.OK);
    }

    // 플레이리스트 삭제
    @DeleteMapping("playlists/delete/{memberNo}/{playlistNo}")
    public ResponseEntity<String> deletePli(@PathVariable(name = "memberNo") int memberNo, @PathVariable(name = "playlistNo") String playlistNo) {
        log.info("memberNo : " + memberNo);
        log.info("playlistNo : " + playlistNo);
        playlistService.delPlaylist(playlistNo);
        return new ResponseEntity<>("Playlist is deleted successfully", HttpStatus.OK);
    }

    // 플레이리스트안에 곡 넣기
    @PostMapping("playlists/push")
    public ResponseEntity<String> pushMusicTo(@RequestBody Map<String, Object> map) {
        log.info("map : " + map);
        playlistService.addMusicsToPlaylist(map);
        return null;
    }


    // 특정 플레이리스트 안의 곡 빼기
    @DeleteMapping("playlists/pull/{playlistNo}/{musicNos}")
    public ResponseEntity<String> pullMusic(@PathVariable(name = "playlistNo") String playlistNo, @PathVariable(name = "musicNos") String list) {
        log.info("playlistNo : " + playlistNo);
        String[] lst = list.substring(1,list.length()-1).split(",");
        List<Long> deleteList = new ArrayList<Long>();
        for(String l : lst) {
            deleteList.add(Long.valueOf(l.trim()));
        }
        log.info("deleteList : " + deleteList);
        playlistService.removeMusicFromPlaylist(playlistNo, deleteList);
        return null;
    }

    // 플레이리스트 수정
    @PostMapping("playlists/update")
    public void changePlaylistName(@RequestParam("image")MultipartFile img, @RequestParam("playlistNo")String playlistNo, @RequestParam("title") String title) {
        FileManager temp = new FileManager();
        String fileName = "there is no file.";
        Playlist playlist = null;

        try{
            MetaFile metaFile_image = null;
            if(img != null) {
                metaFile_image = temp.uploadFile(img);
            }
            playlistService.modifyPlaylistName(metaFile_image, title, playlistNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("playlistNo : " + playlistNo);
        log.info("playlistNo : " + title);
        log.info("image : " + img.getOriginalFilename());
    }

    // 플레이리스트 목록 불러오기
    @GetMapping("playlists/{memberNo}")
    public List<Playlist> findPlaylist(@PathVariable int memberNo) {
        log.info("memberNo : " + memberNo);
        List<Playlist> lst = playlistService.showPlaylist(memberNo);
        return lst;
    }


    @GetMapping("onePlaylists/{playlistNo}")
    public Playlist getOnePlaylist(@PathVariable String playlistNo) {
        log.info("playlistNo : " + playlistNo);
        return playlistService.showOnePlaylist(playlistNo);
    }

}

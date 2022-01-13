package com.example.musicat_audio.service;

import com.example.musicat_audio.domain.MetaFile;
import com.example.musicat_audio.domain.Music;
import com.example.musicat_audio.domain.Playlist;
import com.example.musicat_audio.domain.PlaylistImage;
import com.example.musicat_audio.repository.MusicRepository;
import com.example.musicat_audio.repository.PlaylistRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final MusicRepository musicRepository;

    @Autowired
    public PlaylistService(PlaylistRepository playlistRepository, MusicRepository musicRepository) {
        this.playlistRepository = playlistRepository;
        this.musicRepository = musicRepository;
    }

    // 플레이리스트 생성
    @Transactional
    public void insertPlaylist(Playlist playlist) {
        playlistRepository.savePlaylist(playlist);
    }

    // 플레이리스트 삭제
    @Transactional
    public void delPlaylist(String playlistKey) {
        playlistRepository.deletePlaylist(playlistKey);
    }

    // 특정 플레이리스트 안에 곡 넣기
    @Transactional
    public void addMusicsToPlaylist(Map<String, Object> map) {
        List<Integer> list = new ArrayList<Integer>();
        list = (ArrayList)map.get("musicNos");
        String playlistKey = (String) map.get("playlistKey");
        log.info("musicNos : " + list);
        log.info("playlistKey : " + playlistKey);
        playlistRepository.insertPlaylistNode(playlistKey, list);
    }

    // 특정 플레이리스트 안의 곡 빼기
    @Transactional
    public void removeMusicFromPlaylist(String playlistKey, List<Long> deleteList) {
        playlistRepository.deletePlaylistNode(playlistKey, deleteList);
    }

    // 플레이리스트의 수정
    @Transactional
    public void modifyPlaylistName(MetaFile file, String title, String playlistKey) {
        // 플레이리스트에 맞는 이미지 넘버 찾기
        PlaylistImage pi = playlistRepository.selectAndSavePlaylistImage(playlistKey, file);
        log.info("pi : " + pi.getFileNo());
        playlistRepository.updatePlaylist(pi, title, playlistKey);
    }

    // 플레이리스트 목록 불러오기
    public List<Playlist> showPlaylist(int memberNo) {
        Integer memNo = Integer.valueOf(memberNo);
        return playlistRepository.selectPlaylist(memNo);
    }

    // 플레이리스트 상세 불러오기
    public List<Music> showDetailPlaylist(String playlistKey) {
        List<Music> playlists = playlistRepository.selectDetailPlaylist(playlistKey);
        return playlists;
    }

    // 플레이리스트 하나의 정보 불러오기
    public Playlist showOnePlaylist(String playlistKey) {
        Playlist playlist = playlistRepository.selectOnePlaylist(playlistKey);
        return playlist;
    }
}
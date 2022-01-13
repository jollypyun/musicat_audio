package com.example.musicat_audio.repository;

import com.example.musicat_audio.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class PlaylistRepository {
    private EntityManager em;

    @Autowired
    public PlaylistRepository(EntityManager em) {
        this.em = em;
    }

    // 플레이리스트 생성
    public void savePlaylist(Playlist playlist) {
        em.persist(playlist);
    }

    // 플레이리스트 삭제
    public void deletePlaylist(String playlistKey) {
        log.info("info : " + playlistKey);
        em.createQuery("DELETE FROM Playlist P WHERE P.id = :playlistKey").setParameter("playlistKey", playlistKey).executeUpdate();
    }

    // 특정 플레이리스트 안에 곡 넣기
    public void insertPlaylistNode(String playlistKey, List<Integer> list) {
        Playlist playlist = em.find(Playlist.class, playlistKey);
        for(long num : list) {
            Music music = em.find(Music.class, num);
            PlaylistNode playlistNode = new PlaylistNode();
            playlistNode.setPlaylistKey(playlist);
            playlistNode.setMusicNo(music);
            em.persist(playlistNode);
        }
    }

    // 특정 플레이리스트 안의 곡 빼기
    public void deletePlaylistNode(String playlistKey, List<Long> deleteList) {
        Playlist playlist = em.find(Playlist.class, playlistKey);
        for(long num : deleteList) {
            Music music = em.find(Music.class, num);
            PlaylistNode playlistNode = new PlaylistNode();
            playlistNode.setPlaylistKey(playlist);
            playlistNode.setMusicNo(music);
            log.info("playlistnode : " + playlistNode.getPlaylistKey().getId());
            log.info("playlistnode : " + playlistNode.getMusicNo().getId());
            em.createQuery("DELETE FROM PlaylistNode PN WHERE PN.playlistKey = :playlistKey AND PN.musicNo = :musicNo")
                    .setParameter("playlistKey", playlistKey)
                    .setParameter("musicNo", music)
                    .executeUpdate();
        }
    }

    // 플레이리스트 수정
    public void updatePlaylist(PlaylistImage playlistImage, String playlistName, String playlistKey) {
        Playlist playlist = em.find(Playlist.class, playlistKey);
        em.createQuery("UPDATE Playlist P SET P.playlistName = :playlistName, P.playlistImage = :playlistImage WHERE P.id = :playlistKey")
                .setParameter("playlistName", playlistName)
                .setParameter("playlistImage", playlistImage)
                .setParameter("playlistKey", playlistKey)
                .executeUpdate();
    }

    // 플레이리스트 목록 가져오기
    public List<Playlist> selectPlaylist(Integer memberNo) {
        List<Playlist> playlists = em.createQuery("SELECT P FROM Playlist P WHERE P.memberNo = :memberNo")
                .setParameter("memberNo", memberNo)
                .getResultList();
        for(Playlist p : playlists) {
            log.info("log : " + p.getId());
        }
        return playlists;
    }

    // 플레이리스트 상세정보 가져오기
    public List<Music> selectDetailPlaylist(String playlistKey) {
        log.info("key : " + playlistKey);
        List<Music> playlists = em.createQuery("SELECT PN.musicNo FROM PlaylistNode PN JOIN Playlist P ON P.id = PN.playlistKey.id WHERE P.id = :playlistKey")
                .setParameter("playlistKey", playlistKey)
                .getResultList();
        for(Music p : playlists) {
            System.out.println("p : " + p);
        }
        return playlists;
    }

    // 플레이리스트 이미지 정보 가져오고 metafile 업데이트
    public PlaylistImage selectAndSavePlaylistImage(String playlistKey, MetaFile meta) {
        MetaFile file = (MetaFile) em.createQuery("SELECT PI.fileNo FROM PlaylistImage PI WHERE PI.playlistKey = :playlistKey")
                .setParameter("playlistKey", playlistKey)
                .getSingleResult();
        Long fileNo = file.getId();
        em.createQuery("UPDATE MetaFile M SET M.fileSize = :fileSize, M.originalFileName = :originalFileName, M.systemFileName = :systemFileName, M.fileType = :fileType")
                .setParameter("fileSize", meta.getFileSize())
                .setParameter("originalFileName", meta.getOriginalFileName())
                .setParameter("systemFileName", meta.getSystemFileName())
                .setParameter("fileType", meta.getFileType())
                .executeUpdate();
        return em.find(PlaylistImage.class, fileNo);
    }

    // 플레이리스트 하나의 정보 불러오기
    public Playlist selectOnePlaylist(String playlistKey) {
        Playlist playlist = em.find(Playlist.class, playlistKey);
        return playlist;
    }
}

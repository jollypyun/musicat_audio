package com.example.musicat_audio.repository;

import com.example.musicat_audio.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@Slf4j
public class PlaylistRepository {
    private EntityManager em;

    @Autowired
    public PlaylistRepository(EntityManager em) {
        this.em = em;
    }

    // 플레이리스트 생성
    public Playlist savePlaylist(Playlist playlist) {
        em.persist(playlist);
        Playlist info = (Playlist) em.createQuery("SELECT P FROM Playlist P WHERE P.playlistName = :name")
                .setParameter("name", playlist.getPlaylistName())
                .getSingleResult();
        return info;
    }

    public void saveNewCurrentPlaylist(Playlist playlist){
        em.persist(playlist);
    }

    public MetaFile saveMetafileReturnFileNo(MetaFile meta) {
        String sql = "INSERT INTO metafile(originalfilename, systemfilename, filesize, filetype) VALUES(?,?,?,?)";
        em.createNativeQuery(sql)
                .setParameter(1, meta.getOriginalFileName())
                .setParameter(2, meta.getSystemFileName())
                .setParameter(3, meta.getFileSize())
                .setParameter(4, meta.getFileType())
                .executeUpdate();
        log.info("persist 완료");
        MetaFile info = (MetaFile) em.createQuery("SELECT M FROM MetaFile M WHERE M.systemFileName = :sys")
                        .setParameter("sys", meta.getSystemFileName())
                        .getSingleResult();
        log.info("meta : " + info);
        return info;
    }

    public void savePlaylistImage(PlaylistImage playlistImage) {
        log.info("pi_file_no : " + playlistImage.getFileNo());
        log.info("pi_key" + playlistImage.getPlaylistKey());
        log.info("pi_node" + playlistImage.getId());
        em.persist(playlistImage);
    }

    // 플레이리스트 삭제
    public void deletePlaylist(String playlistKey) {
        log.info("info : " + playlistKey);
        em.createQuery("DELETE FROM Playlist P WHERE P.id = :playlistKey").setParameter("playlistKey", playlistKey).executeUpdate();
    }

    // 특정 플레이리스트 안에 곡 넣기

    public Playlist insertPlaylistNode(String playlistKey, List<Integer> list) {
        Playlist playlist = em.find(Playlist.class, playlistKey);
        for(long num : list) {
            Music music = em.find(Music.class, num);
            PlaylistNode playlistNode = new PlaylistNode();
            playlistNode.setPlaylistKey(playlist);
            playlistNode.setMusic(music);
            em.persist(playlistNode);
        }
        Playlist newPl = em.find(Playlist.class, playlistKey);
        return newPl;
    }

    // 특정 플레이리스트 안의 곡 빼기
    public void deletePlaylistNode(String playlistKey, List<Long> deleteList) {
        Playlist playlist = em.find(Playlist.class, playlistKey);
        for(long num : deleteList) {
            Music music = em.find(Music.class, num);
            PlaylistNode playlistNode = new PlaylistNode();
            playlistNode.setPlaylistKey(playlist);
            playlistNode.setMusic(music);
            log.info("playlistnode : " + playlistNode.getPlaylistKey().getId());
            log.info("playlistnode : " + playlistNode.getMusic().getId());
            em.createQuery("DELETE FROM PlaylistNode PN WHERE PN.playlistKey = :playlistKey AND PN.music = :musicNo")
                    .setParameter("playlistKey", playlist)
                    .setParameter("musicNo", music)
                    .executeUpdate();
        }
    }

    // 플레이리스트 수정 with Image
    public Playlist updatePlaylistWithImage(PlaylistImage playlistImage, String playlistName, String playlistKey) {
        log.info("with playlistImage : " + playlistImage);
        log.info("with playlistName : " + playlistName);
        log.info("with playlistKey : " + playlistKey);
        em.createQuery("UPDATE Playlist P SET P.playlistName = :playlistName, P.playlistImage = :playlistImage WHERE P.id = :playlistKey")
                .setParameter("playlistName", playlistName)
                .setParameter("playlistImage", playlistImage)
                .setParameter("playlistKey", playlistKey)
                .executeUpdate();
        Playlist playlist = em.find(Playlist.class, playlistKey);
        return playlist;
    }

    // 플레이리스트 수정
    public Playlist updatePlaylist(String playlistName, String playlistKey) {
        em.createQuery("UPDATE Playlist P SET P.playlistName = :playlistName WHERE P.id = :playlistKey")
                .setParameter("playlistName", playlistName)
                .setParameter("playlistKey", playlistKey)
                .executeUpdate();
        Playlist playlist = em.find(Playlist.class, playlistKey);
        return playlist;
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
        List<Music> playlists = em.createQuery("SELECT PN.music FROM PlaylistNode PN JOIN Playlist P ON P.id = PN.playlistKey.id WHERE P.id = :playlistKey")
                .setParameter("playlistKey", playlistKey)
                .getResultList();
        for(Music p : playlists) {
            System.out.println("p : " + p);
        }
        return playlists;
    }

    // 플레이리스트 이미지 정보 가져오고 metafile 업데이트
    public PlaylistImage selectAndSavePlaylistImage(String playlistKey, MetaFile meta) {
        Playlist playlist = (Playlist) em.createQuery("SELECT P FROM Playlist P WHERE P.id = :playlistKey")
                .setParameter("playlistKey", playlistKey)
                .getSingleResult();

        MetaFile file = (MetaFile) em.createQuery("SELECT PI.fileNo FROM PlaylistImage PI WHERE PI.playlistKey = :playlistKey")
                .setParameter("playlistKey", playlist)
                .getSingleResult();
        em.createQuery("UPDATE MetaFile M SET M.fileSize = :fileSize, M.originalFileName = :originalFileName, M.systemFileName = :systemFileName, M.fileType = :fileType")
                .setParameter("fileSize", meta.getFileSize())
                .setParameter("originalFileName", meta.getOriginalFileName())
                .setParameter("systemFileName", meta.getSystemFileName())
                .setParameter("fileType", meta.getFileType())
                .executeUpdate();
        PlaylistImage playlistImage = (PlaylistImage) em.createQuery("SELECT P FROM PlaylistImage P WHERE P.fileNo = :fileNo")
                .setParameter("fileNo", file)
                .getSingleResult();
        return playlistImage;
    }

    // 플레이리스트 하나의 정보 불러오기
    public Playlist selectOnePlaylist(String playlistKey) {
        Playlist playlist = em.find(Playlist.class, playlistKey);
        return playlist;
    }
}

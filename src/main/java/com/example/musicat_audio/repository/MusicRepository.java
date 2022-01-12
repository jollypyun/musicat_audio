package com.example.musicat_audio.repository;

import com.example.musicat_audio.domain.MetaFile;
import com.example.musicat_audio.domain.Music;
import com.example.musicat_audio.domain.Playlist;
import com.example.musicat_audio.domain.Thumbnail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Repository
public class MusicRepository {

    private EntityManager em;

    @Autowired
    public MusicRepository(EntityManager em) {
        this.em = em;
    }

    // 썸네일 저장
    public void saveThumbnail(Thumbnail thumbnail) {
        em.persist(thumbnail);
    }
    // 파일 저장
    public String saveFile(MetaFile file) {
        em.persist(file);
        return file.getSystemFileName();
    }
    // music 저장
    public void saveMusic(Music music) {
        em.persist(music);
    }

    // music 삭제
    public void deleteMusic(Music music) {
        em.remove(music);
    }

    // articleNo 관련된 music 전부 삭제
    public void deleteMusics(int articleNo) {
//        em.createQuery("select m from Member m where m.name = :name", Member.class)
//                .setParameter("name", name)
        em.createQuery("DELETE FROM Music m WHERE m.articleNo = :articleno")
                .setParameter("articleno", articleNo)
                .executeUpdate();
    }
    // music 찾기
    public Music findOne_Music(Long musicId) {
        return em.find(Music.class, musicId);
    }

    public List<Music> findMusics(int articleNo){
        return em.createQuery("SELECT m FROM Music m WHERE m.articleNo = :articleno")
                .setParameter("articleno", articleNo)
                .getResultList();
    }
}

package com.example.musicat_audio.service;

import com.example.musicat_audio.domain.MetaFile;
import com.example.musicat_audio.domain.Music;
import com.example.musicat_audio.domain.Playlist;
import com.example.musicat_audio.domain.Thumbnail;
import com.example.musicat_audio.repository.MusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MusicService {

    private final MusicRepository musicRepository;

    @Autowired
    public MusicService(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
    }

    @Transactional
    public Music saveMusic(MetaFile file, MetaFile imageFile, String title, int memberNo) {
        // 파일 테이블에 저장
        String systemfileName = musicRepository.saveFile(file); // 음악파일 저장
        musicRepository.saveFile(imageFile); // 썸네일 파일 저장

        // 썸네일 테이블에 저장
        Thumbnail thumbnail = new Thumbnail(imageFile);
        musicRepository.saveThumbnail(thumbnail);

        // music 테이블에 저장
        Music music = new Music(file, thumbnail, title, memberNo);
        musicRepository.saveMusic(music);
        //return music.getFile().getSystemFileName();
        return music;
    }

    @Transactional
    public void deleteMusicByArticleNo(int articleNo) {
        musicRepository.deleteMusics(articleNo);
    }

    @Transactional
    public void deleteMusicByMusicId(Long musicId) {
        Music music = musicRepository.findOne_Music(musicId);
        musicRepository.deleteMusic(music);
    }

    public Music findMusic(Long musicId) {
        return musicRepository.findOne_Music(musicId);
    }

    public List<Music> findMusics(int articleNo) {
        return musicRepository.findMusics(articleNo);
    }

    @Transactional
    public void connectToArticle(Long musicId, int articleNo){
        Music music = findMusic(musicId);
        music.connectToArticle(articleNo);
        musicRepository.saveMusic(music);
    }
}

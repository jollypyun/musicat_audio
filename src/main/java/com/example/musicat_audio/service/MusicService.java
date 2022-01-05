package com.example.musicat_audio.service;

import com.example.musicat_audio.domain.MetaFile;
import com.example.musicat_audio.domain.Music;
import com.example.musicat_audio.domain.Thumbnail;
import com.example.musicat_audio.repository.MusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MusicService {

    private final MusicRepository musicRepository;

    @Autowired
    public MusicService(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
    }

    @Transactional
    public Music saveMusic(MetaFile file, MetaFile imageFile, String title, int memberNo, int articleNo) {
        // 파일 테이블에 저장
        String systemfileName = musicRepository.saveFile(file); // 음악파일 저장
        musicRepository.saveFile(imageFile); // 썸네일 파일 저장

        // 썸네일 테이블에 저장
        Thumbnail thumbnail = new Thumbnail(imageFile);
        musicRepository.saveThumbnail(thumbnail);

        // music 테이블에 저장
        Music music = new Music(file, thumbnail, title, memberNo, articleNo);
        musicRepository.saveMusic(music);
        //return music.getFile().getSystemFileName();
        return music;
    }

    @Transactional
    public void deleteMusic(int articleNo) {
        musicRepository.deleteMusics(articleNo);
    }

    public Music findMusic(Long musicId) {
        return musicRepository.findOne_Music(musicId);
    }
}

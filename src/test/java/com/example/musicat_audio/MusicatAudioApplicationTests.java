package com.example.musicat_audio;

import com.example.musicat_audio.domain.MetaFile;
import com.example.musicat_audio.repository.MusicRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class MusicatAudioApplicationTests {

	@Autowired
	MusicRepository musicRepository;

	@Test
	@Transactional
	@Rollback(false)
	void contextLoads() {
		//MetaFile file = new MetaFile();
		//musicRepository.save(file);
	}

}

package com.example.musicat_audio.domain;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@NoArgsConstructor
public class File {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_no")
    private Long id;

    @Column(name="originalfilename", length = 100)
    private String originalFileName;

    @Column(name="systemfilename", length = 100)
    private String systemFileName;

    @Column(name="filesize")
    private Long fileSize;

    @Column(name = "filetype")
    private String fileType;

    @Column(name = "writedate")
    private Instant wirteDate;

    public File(String originalFileName, String systemFileName, Long fileSize) {
        this.originalFileName = originalFileName;
        this.systemFileName = systemFileName;
        this.fileSize = fileSize;
    }
}

package com.example.musicat_audio.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Entity
@Table(name="metafile")
@NoArgsConstructor
public class MetaFile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_no", nullable = false)
    private Long id;

    @Column(name="originalfilename", nullable = false, length = 100)
    private String originalFileName;

    @Column(name="systemfilename", nullable = false, length = 100)
    private String systemFileName;

    @Column(name="filesize", nullable = false)
    private Long fileSize;

    @Column(name = "filetype", nullable = false, length = 20)
    private String fileType;

    @Column(name = "writedate", nullable = false)
    private Instant wirteDate;

    public MetaFile(String originalFileName, String systemFileName, Long fileSize) {
        this.originalFileName = originalFileName;
        this.systemFileName = systemFileName;
        this.fileSize = fileSize;
    }
}

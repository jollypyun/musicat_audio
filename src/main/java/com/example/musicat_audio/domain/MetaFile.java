package com.example.musicat_audio.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Table(name="metafile")
@NoArgsConstructor
public class MetaFile {
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

    public MetaFile(String originalFileName, String systemFileName, String fileType, Long fileSize) {
        this.originalFileName = originalFileName;
        this.systemFileName = systemFileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }
}

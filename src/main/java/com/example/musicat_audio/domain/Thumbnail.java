package com.example.musicat_audio.domain;

import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class Thumbnail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="thumbnail_no")
    private Long id;

    @OneToOne
    @JoinColumn(name = "file_no")
    private MetaFile file;

    public Thumbnail(MetaFile file) {
        this.file = file;
    }
}

package com.example.musicat_audio.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Thumbnail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="thumbnail_no", nullable = false)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "file_no", nullable = false)
    private MetaFile file;
  
    public Thumbnail(MetaFile file) {
        this.file = file;
    }
}

package com.example.musicat_audio.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Music {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_no", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "file_no", nullable = false)
    private MetaFile file;

    @ManyToOne(optional = false)
    @JoinColumn(name = "thumbnail_no", nullable = false)
    private Thumbnail thumbnail;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name="member_no", nullable = false)
    private int memberNo;
}

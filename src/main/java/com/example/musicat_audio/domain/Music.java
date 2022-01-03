package com.example.musicat_audio.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Music {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_no")
    private Long id;

    @OneToOne
    @JoinColumn(name = "file_no")
    private MetaFile file;

    @OneToOne
    @JoinColumn(name = "thumbnail_no")
    private Thumbnail thumbnail;

    private String title;

    @Column(name="member_no")
    private int memberNo;

    @Column(name="article_no")
    private int articleNo;

    public Music(MetaFile file, Thumbnail thumbnail, String title, int memberNo, int articleNo) {
        this.file = file;
        this.thumbnail = thumbnail;
        this.title = title;
        this.memberNo = memberNo;
        this.articleNo = articleNo;
    }
}

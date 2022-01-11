package com.example.musicat_audio.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "playlist_image")
@NoArgsConstructor
public class PlaylistImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_no", nullable = false)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "playlist_no", nullable = false)
    private Playlist playlistNo;

    @OneToOne(optional = false)
    @JoinColumn(name = "file_no", nullable = false)
    private MetaFile fileNo;

    public PlaylistImage(Playlist playlistNo, MetaFile fileNo) {
        this.playlistNo = playlistNo;
        this.fileNo = fileNo;
    }
}
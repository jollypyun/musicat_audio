package com.example.musicat_audio.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @JsonBackReference
    @OneToOne(optional = false)
    @JoinColumn(name = "playlist_key", nullable = false)
    private Playlist playlistKey;

    @OneToOne(optional = false)
    @JoinColumn(name = "file_no", nullable = false)
    private MetaFile fileNo;

    public PlaylistImage(Playlist playlistKey, MetaFile fileNo) {
        this.playlistKey = playlistKey;
        this.fileNo = fileNo;
    }
}
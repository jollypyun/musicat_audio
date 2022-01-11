package com.example.musicat_audio.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "playlist_node")
public class PlaylistNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "node_no", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "playlist_no", nullable = false)
    private Playlist playlistNo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "music_no", nullable = false)
    private Music musicNo;
}
package com.example.musicat_audio.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @JsonBackReference
    @ManyToOne(optional = false)
    @JoinColumn(name = "playlist_key", nullable = false)
    private Playlist playlistKey;

    //@JsonBackReference
    @JsonManagedReference
    @ManyToOne(optional = false)
    @JoinColumn(name = "music_no", nullable = false)
    private Music music;
}
package com.example.musicat_audio.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Entity
@Table(name = "playlist")
@NoArgsConstructor
@AllArgsConstructor
public class Playlist {
    @Id
    @Column(name = "playlist_key", nullable = false)
    private String id;

    @Column(name = "member_no", nullable = false)
    private Integer memberNo;

    @Column(name = "playlist_name", nullable = false, length = 50)
    private String playlistName;

    @JsonManagedReference
    @OneToMany(mappedBy = "playlistKey", cascade = CascadeType.REMOVE)
    private List<PlaylistNode> playlistNodes = new ArrayList<>();

    @JsonManagedReference
    @OneToOne(mappedBy = "playlistKey", cascade = CascadeType.REMOVE)
    private PlaylistImage playlistImage;

    public Playlist(String id, int memberNo) {
        this.id = id;
        this.memberNo = memberNo;
    }

    public Playlist(int memberNo, String playlistName) {
        this.memberNo = memberNo;
        this.playlistName = playlistName;
    }

    public Playlist(String id, String playlistName) {
        this.id = id;
        this.playlistName = playlistName;
    }

    public Playlist(String id, String playlistName, int memberNo) {
        this.id = id;
        this.playlistName = playlistName;
        this.memberNo = memberNo;
    }
}
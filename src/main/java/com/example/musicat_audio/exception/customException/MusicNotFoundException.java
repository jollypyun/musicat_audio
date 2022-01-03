package com.example.musicat_audio.exception.customException;

import com.example.musicat_audio.exception.ErrorCode;

public class MusicNotFoundException extends RuntimeException {
    public MusicNotFoundException(String message) { super(message); }
    public ErrorCode getErrorCode() {
        return ErrorCode.INVALID_MUSIC_ID;
    }
}

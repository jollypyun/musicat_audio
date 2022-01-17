package com.example.musicat_audio.exception.customException;

import com.example.musicat_audio.exception.ErrorCode;

public class UploadFileException extends RuntimeException {
    public UploadFileException(String message) { super(message); }
    public ErrorCode getErrorCode() {
        return ErrorCode.FILE_UPLOAD_FAIL;
    }

}

package com.example.musicat_audio.exception;

import lombok.Getter;
import lombok.Setter;

/*
   열거형 : 에러코드 (상태코드, 메시지)
 */


@Getter
public enum ErrorCode {

    INVALID_INPUT_VALUE(400," Invalid Input Value"),
    METHOD_NOT_ALLOWED(405,  "Method Not Allowed"),
    HANDLE_ACCESS_DENIED(403, "Access is Denied"),
    EMAIL_DUPLICATION(400, "Email is Duplication"),
    LOGIN_INPUT_INVALID(400, "Login input is invalid"),
    INTERNAL_SERVER_ERROR(500, "Internal Server error"),
    VALIDATION_CHECK_FAIL(400, "Invlaid Input Value"),
    FILE_UPLOAD_FAIL(500, "File Upload Fail"),
    INVALID_MUSIC_ID(400, "Invalid Music Id");

    private int statusCode;
    private final String message;


    ErrorCode(final int statusCode, final String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

}
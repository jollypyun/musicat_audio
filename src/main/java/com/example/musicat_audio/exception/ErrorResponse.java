package com.example.musicat_audio.exception;


import lombok.*;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
/*
==========================================================
 Error Response JSON
==========================================================

{
  "message": " Invalid Input Value",
  "status": 400,
  // "errors":[], 비어있을 경우 null 이 아닌 빈 배열을 응답한다.
  "errors": [
    {
      "field": "name.last",
      "reason": "must not be empty"
    },
    {
      "field": "name.first",
      "reason": "must not be empty"
    }
  ]
}
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ErrorResponse {
    private String message;
    private int statusCode;
    private List<FieldError> errors = new ArrayList<>();


    public ErrorResponse(ErrorCode code) {
        this.message = code.getMessage();
        this.statusCode = code.getStatusCode();
    }


    public ErrorResponse(ErrorCode errorCode, BindingResult bindingResult) {
        this.statusCode = errorCode.getStatusCode();

        var errors = bindingResult.getFieldErrors();
        for(var error : errors) {
            String field = error.getField();
            String reason = error.getDefaultMessage();
            this.errors.add(new FieldError(field, reason));
        }
    }


    public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult) {
        return new ErrorResponse(errorCode, bindingResult);
    }


    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FieldError {
        private String field;
        private String reason;
    }
}
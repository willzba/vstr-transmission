package com.vstr.video_chat.exception;

public class FileTypeNotAllowedException extends RuntimeException{
    public FileTypeNotAllowedException(String message) {
        super(message);
    }
}

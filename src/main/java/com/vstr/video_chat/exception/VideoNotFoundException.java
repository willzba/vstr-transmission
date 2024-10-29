package com.vstr.video_chat.exception;

public class VideoNotFoundException extends RuntimeException{

    public VideoNotFoundException(String message) {
        super(message);
    }
}

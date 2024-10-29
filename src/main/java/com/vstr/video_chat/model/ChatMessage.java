package com.vstr.video_chat.model;

public class ChatMessage {

    private String from;
    private String content;
    private String timestamp;

    public ChatMessage() {
    }

    public ChatMessage(String from, String content, String timestamp) {
        this.from = from;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ChatMessage(String from, String content) {
        this.from = from;
        this.content = content;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

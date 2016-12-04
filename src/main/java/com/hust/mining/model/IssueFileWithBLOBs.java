package com.hust.mining.model;

public class IssueFileWithBLOBs extends IssueFile {
    private byte[] content;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
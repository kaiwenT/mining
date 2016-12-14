package com.hust.mining.model;

import java.io.Serializable;

public class IssueFileWithBLOBs extends IssueFile implements Serializable{
    private byte[] content;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
package com.hust.mining.model;

import java.io.Serializable;

public class IssueFileKey implements Serializable{
    private String fileId;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId == null ? null : fileId.trim();
    }
}
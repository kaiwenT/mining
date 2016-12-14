package com.hust.mining.model;

import java.io.Serializable;

public class ResultWithBLOBs extends Result implements Serializable{
    private byte[] content;

    private byte[] origResult;

    private byte[] modifiedResult;

    private byte[] countResult;

    private byte[] modifiedCountResult;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] getOrigResult() {
        return origResult;
    }

    public void setOrigResult(byte[] origResult) {
        this.origResult = origResult;
    }

    public byte[] getModifiedResult() {
        return modifiedResult;
    }

    public void setModifiedResult(byte[] modifiedResult) {
        this.modifiedResult = modifiedResult;
    }

    public byte[] getCountResult() {
        return countResult;
    }

    public void setCountResult(byte[] countResult) {
        this.countResult = countResult;
    }

    public byte[] getModifiedCountResult() {
        return modifiedCountResult;
    }

    public void setModifiedCountResult(byte[] modifiedCountResult) {
        this.modifiedCountResult = modifiedCountResult;
    }
}
package com.hust.mining.model;

public class ResultWithBLOBs extends Result {
    private byte[] content;

    private byte[] origResult;

    private byte[] modifiedResult;

    private byte[] statResult;

    private byte[] modifiedStatResult;

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

    public byte[] getStatResult() {
        return statResult;
    }

    public void setStatResult(byte[] statResult) {
        this.statResult = statResult;
    }

    public byte[] getModifiedStatResult() {
        return modifiedStatResult;
    }

    public void setModifiedStatResult(byte[] modifiedStatResult) {
        this.modifiedStatResult = modifiedStatResult;
    }
}
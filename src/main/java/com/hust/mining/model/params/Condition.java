package com.hust.mining.model;

import org.springframework.web.multipart.MultipartFile;

public class Condition {

    private MultipartFile file;
    private int urlIndex;
    private int titleIndex;
    private int timeIndex;
    private String sourceType;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public int getUrlIndex() {
        return urlIndex;
    }

    public void setUrlIndex(int urlIndex) {
        this.urlIndex = urlIndex;
    }

    public int getTitleIndex() {
        return titleIndex;
    }

    public void setTitleIndex(int titleIndex) {
        this.titleIndex = titleIndex;
    }

    public int getTimeIndex() {
        return timeIndex;
    }

    public void setTimeIndex(int timeIndex) {
        this.timeIndex = timeIndex;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    @Override
    public String toString() {
        return "Condition [file=" + file + ", urlIndex=" + urlIndex + ", titleIndex=" + titleIndex + ", timeIndex="
                + timeIndex + ", sourceType=" + sourceType + "]";
    }

}

package com.hust.mining.model;

import java.util.List;

public class ResultWithContent {

    private Result result;
    private List<String[]> content;
    private List<String[]> origCluster;
    private List<String[]> origCount;
    private List<String[]> modiCluster;
    private List<String[]> modiCount;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<String[]> getContent() {
        return content;
    }

    public void setContent(List<String[]> content) {
        this.content = content;
    }

    public List<String[]> getOrigCluster() {
        return origCluster;
    }

    public void setOrigCluster(List<String[]> origCluster) {
        this.origCluster = origCluster;
    }

    public List<String[]> getOrigCount() {
        return origCount;
    }

    public void setOrigCount(List<String[]> origCount) {
        this.origCount = origCount;
    }

    public List<String[]> getModiCluster() {
        return modiCluster;
    }

    public void setModiCluster(List<String[]> modiCluster) {
        this.modiCluster = modiCluster;
    }

    public List<String[]> getModiCount() {
        return modiCount;
    }

    public void setModiCount(List<String[]> modiCount) {
        this.modiCount = modiCount;
    }

}

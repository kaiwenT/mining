package com.hust.mining.model;

import java.io.Serializable;

public class IssueKey implements Serializable{
    private String issueId;

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId == null ? null : issueId.trim();
    }
}
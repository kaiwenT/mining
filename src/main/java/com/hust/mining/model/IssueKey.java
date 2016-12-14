package com.hust.mining.model;

public class IssueKey {
    private String issueId;

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId == null ? null : issueId.trim();
    }
}
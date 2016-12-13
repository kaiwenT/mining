package com.hust.mining.model;

public class ResultKey {
    private String rid;

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid == null ? null : rid.trim();
    }
}
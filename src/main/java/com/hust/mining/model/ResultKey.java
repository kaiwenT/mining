package com.hust.mining.model;

import java.io.Serializable;

public class ResultKey implements Serializable{
    private String rid;

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid == null ? null : rid.trim();
    }
}
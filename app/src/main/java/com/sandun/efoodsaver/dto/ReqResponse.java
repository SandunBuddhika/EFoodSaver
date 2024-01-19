package com.sandun.efoodsaver.dto;

import java.io.Serializable;

public class ReqResponse implements Serializable {
    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}

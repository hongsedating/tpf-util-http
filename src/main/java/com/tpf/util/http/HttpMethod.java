package com.tpf.util.http;

import org.apache.http.client.methods.*;

public enum HttpMethod {
    GET(new HttpGet()), POST(new HttpPost()), DELETE(new HttpDelete()), PUT(new HttpPut());

    private HttpRequestBase requestBase;
    private HttpMethod(HttpRequestBase requestBase){
        this.requestBase = requestBase;
    }

    public HttpRequestBase getRequestBase() {
        return requestBase;
    }
}

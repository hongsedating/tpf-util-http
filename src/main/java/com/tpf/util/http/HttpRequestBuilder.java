package com.tpf.util.http;

import com.tpf.util.jdk.StringUtils;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRequestBuilder {
    private static Logger log = LoggerFactory.getLogger(HttpRequestBuilder.class);
    private StringBuilder url;
    private HttpMethod method;
    private Map<String, String> headers;
    private HttpEntity httpEntity;
    private CloseableHttpClient httpClient;

    public HttpRequestBuilder url(String url){
        this.url = new StringBuilder(url);
        return this;
    }

    /***/
    public HttpRequestBuilder method(HttpMethod method){
        this.method = method;
        return this;
    }

    public HttpRequestBuilder headers(Map<String, String> headers){
        this.headers = headers;
        return this;
    }

    /**url 拼接的参数*/
    public HttpRequestBuilder urlParam(Map<String, Object> param) {
        if(param != null && !param.isEmpty()){
            boolean isFirst = false;
            for (String k: param.keySet()) {
                if(StringUtils.isEmpty(k)){
                    continue;
                }
                if(isFirst){
                    url.append("&");
                }else {
                    url.append("?");
                    isFirst = true;
                }
                url.append(k).append("=").append(param.get(k));
            }
        }
        return this;
    }

    /**form 表单提交的参数*/
    public HttpRequestBuilder formEntity(Map<String, Object> param) throws UnsupportedEncodingException {
        log.info("param={}", param);
        List<NameValuePair> nvps = new ArrayList<>();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            nvps.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
        }
        this.httpEntity = new UrlEncodedFormEntity(nvps);
        return this;
    }

    /**String 字符串提交的参数*/
    public HttpRequestBuilder stringEntity(String body) throws UnsupportedEncodingException {
        this.httpEntity = new StringEntity(body);
        return this;
    }

    /**String 字符串提交的参数
     * @param charset see constant defined in {@link Charsets}*/
    public HttpRequestBuilder stringEntity(String body, Charset charset) throws UnsupportedEncodingException {
        this.httpEntity = new StringEntity(body, charset);
        return this;
    }

    public HttpRequestBuilder httpEntity(HttpEntity httpEntity){
        this.httpEntity = httpEntity;
        return this;
    }

    public HttpRequestBuilder httpClient(CloseableHttpClient httpClient){
        this.httpClient = httpClient;
        return this;
    }

    public HttpRequestBase build() throws NoSuchAlgorithmException, KeyManagementException, IOException {
        if(method == null){
            throw new NullPointerException("method couldn't be null");
        }
        HttpRequestBase requestBase = method.getRequestBase();
        //url
        requestBase.setURI(URI.create(url.toString()));
        //请求头
        if(headers != null){
            headers.forEach((k, v)->{
                requestBase.addHeader(k, v);
            });
        }
        //请求体
        if(this.httpEntity != null){
            ((HttpEntityEnclosingRequestBase)requestBase).setEntity(httpEntity);
        }
        return requestBase;
    }
}

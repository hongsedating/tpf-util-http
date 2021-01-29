package com.tpf.util.http;

import com.tpf.util.jdk.StringUtils;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tpf.util.http.HttpUtilConstant.DEFAULT_CHARSET;

public class HttpRequestBuilder {
    private String protocol;
    private StringBuilder url;
    private HttpMethod method;
    private Map<String, String> headers;
    private HttpEntity httpEntity;

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
    public HttpRequestBuilder formEntity(Map<String, Object> param) {
        return formEntity(param, DEFAULT_CHARSET);
    }

    /**form 表单提交的参数*/
    public HttpRequestBuilder formEntity(Map<String, Object> param, String charsetName) {
        Charset charset = charsetName == null ? DEFAULT_CHARSET : Charset.forName(charsetName);
        return formEntity(param, charset);
    }

    /**form 表单提交的参数*/
    public HttpRequestBuilder formEntity(Map<String, Object> param, Charset charset) {
        if(charset == null){
            charset = DEFAULT_CHARSET;
        }
        List<NameValuePair> nvps = new ArrayList<>();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            nvps.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
        }
        this.httpEntity = new UrlEncodedFormEntity(nvps, charset);
        return this;
    }

    /**String 字符串提交的参数*/
    public HttpRequestBuilder stringEntity(String body) {
        return stringEntity(body, DEFAULT_CHARSET);
    }

    /**String 字符串提交的参数*/
    public HttpRequestBuilder stringEntity(String body, String charsetName) {
        Charset charset = charsetName == null ? DEFAULT_CHARSET : Charset.forName(charsetName);
        return stringEntity(body, charset);
    }

    /**String 字符串提交的参数
     * @param charset see constant defined in {@link Charsets}*/
    public HttpRequestBuilder stringEntity(String body, Charset charset) {
        this.httpEntity = new StringEntity(body, charset);
        return this;
    }

    public HttpRequestBuilder httpEntity(HttpEntity httpEntity){
        this.httpEntity = httpEntity;
        return this;
    }

    public HttpRequestBuilder sslProtocol(String protocol){
        this.protocol = protocol;
        return this;
    }

    public HttpRequestBase build() {
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

package com.tpf.util.http;


import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.function.Consumer;

import static com.tpf.util.http.HttpUtilConstant.DEFAULT_CHARSET;

public class HttpUtil {
    private HttpUtil(){}
    private static Logger log = LoggerFactory.getLogger(HttpUtil.class);
    /**
     * 绕过验证
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("TLS");
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                                           String paramString) throws CertificateException {
            }
            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                                           String paramString) throws CertificateException {
            }
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[] { trustManager }, null);
        return sc;
    }

    public static CloseableHttpClient getHttpClient() throws KeyManagementException, NoSuchAlgorithmException {
        // 采用绕过验证的方式处理https请求
        SSLContext sslcontext = createIgnoreVerifySSL();

        // 设置协议http和https对应的处理socket链接工厂的对象
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslcontext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)).build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpClients.custom().setConnectionManager(connManager);

        // 创建自定义的httpclient对象
        return HttpClients.custom().setConnectionManager(connManager).build();
    }

    /**
     * put 请求
     * @param options 配置, [0]表示请求字符集
     * */
    public static String put(String path, Map<String, String> headers, String body, Object... options) throws HttpException {
        log.info("path={}, headers={}, body={}", path, headers, body);
        String charsetName = options.length > 0 ? (String)options[0] : null;
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        requestBuilder.method(HttpMethod.PUT).url(path).headers(headers).stringEntity(body, charsetName);
        HttpRequestBase requestBase = requestBuilder.build();
        return send(requestBase, charsetName);
    }

    /**
     * get 请求
     * @param options 配置, [0]表示请求字符集
     * @return 响应内容
     * */
    public static String get(String path, Map<String, String> headers,  Map<String, Object> param, Object... options) throws HttpException {
        log.info("path={}, headers={}, param={}", path, headers, param);
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        requestBuilder.method(HttpMethod.GET).url(path).headers(headers).urlParam(param);
        String charsetName = options.length > 0 ? (String)options[0] : null;
        HttpRequestBase requestBase = requestBuilder.build();
        log.info("final url={}", requestBase.getURI());
        return send(requestBase, charsetName);
    }

    /**
     * delete 请求
     * @param options 配置, [0]表示请求字符集
     * @return 响应内容
     * */
    public static String delete(String path, Map<String, String> headers,  Map<String, Object> param, Object... options) throws HttpException {
        log.info("path={}, headers={}, param={}", path, headers, param);
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        requestBuilder.method(HttpMethod.DELETE).url(path).headers(headers).urlParam(param);
        String charsetName = options.length > 0 ? (String)options[0] : null;
        HttpRequestBase requestBase = requestBuilder.build();
        return send(requestBase, charsetName);
    }

    /**
     * delete 请求
     * @param options 配置, [0]表示请求字符集
     * @return 响应内容
     * */
    public static String postForm(String path, Map<String, String> headers,  Map<String, Object> param, Object... options) throws HttpException {
        log.info("path={}, headers={}, param={}", path, headers, param);
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        requestBuilder.method(HttpMethod.POST).url(path).headers(headers);
        String charsetName = options.length > 0 ? (String)options[0] : null;
        requestBuilder.formEntity(param, charsetName);
        HttpRequestBase requestBase = requestBuilder.build();
        return send(requestBase, charsetName);
    }

    /**
     * delete 请求
     * @param options 配置, [0]表示请求字符集
     * @return 响应内容
     * */
    public static String postRaw(String path, Map<String, String> headers, String param, Object... options) throws HttpException {
        log.info("path={}, headers={}, param={}", path, headers, param);
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        requestBuilder.method(HttpMethod.POST).url(path).headers(headers);
        String charsetName = options.length > 0 ? (String)options[0] : null;
        requestBuilder.stringEntity(param, charsetName);
        HttpRequestBase requestBase = requestBuilder.build();
        return send(requestBase, charsetName);
    }

    /**
     * 发送请求
     * @param requestBase 请求对象,可通过{@code HttpRequestBuilder}创建
     * @return 如果访问状态码是200,则返回响应内容, 否则抛出 HttpException 异常.
     * */
    public static String send(HttpRequestBase requestBase) throws HttpException {
        return send(requestBase, DEFAULT_CHARSET);
    }

    /**
     * 发送请求
     * @param requestBase 请求对象,可通过{@code HttpRequestBuilder}创建
     * @return 如果访问状态码是200,则返回响应内容, 否则抛出 HttpException 异常.
     * */
    public static String send(HttpRequestBase requestBase, String charsetName) throws HttpException {
        Charset charset = charsetName == null ? DEFAULT_CHARSET : Charset.forName(charsetName);
        return send(requestBase, charset);
    }

    /**
     * 发送请求
     * @param requestBase 请求对象,可通过{@code HttpRequestBuilder}创建
     * @return 如果访问状态码是200,则返回响应内容, 否则抛出 HttpException 异常.
     * */
    public static String send(HttpRequestBase requestBase, final Charset charset) throws HttpException {
        final StringBuilder result = new StringBuilder();
        send(requestBase, response->{
            try {
                result.append(EntityUtils.toString(response.getEntity(), charset));
            } catch (IOException e) {
                log.error("", e);
            }
        });
        return result.toString();
    }
    /**
     * 发送请求
     * @param requestBase 请求对象,可通过{@code HttpRequestBuilder}创建
     * @return 如果访问状态码是200,则返回响应内容, 否则抛出 HttpException 异常.
     * */
    public static void send(HttpRequestBase requestBase, Consumer<CloseableHttpResponse> callback) throws HttpException {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            client = getHttpClient();
            if(client == null){
                throw new NullPointerException("client couldn't be null");
            }
            response = client.execute(requestBase);
            if(response == null){
                throw new HttpException("response is null");
            }
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                callback.accept(response);
            }else{
                throw new HttpException("response status code is "+response.getStatusLine().getStatusCode());
            }

        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            log.error("", e);
        }finally {
            try {
                release(client, response);
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }
    /**
     * 发送请求
     * @param requestBase 请求对象,可通过{@code HttpRequestBuilder}创建
     * @return 如果访问状态码是200,则返回响应内容, 否则抛出 HttpException 异常.
     * */
    public static void send(CloseableHttpClient client, HttpRequestBase requestBase, Consumer<CloseableHttpResponse> callback) throws HttpException {
        CloseableHttpResponse response = null;
        try {
            client = getHttpClient();
            if(client == null){
                throw new NullPointerException("client couldn't be null");
            }
            response = client.execute(requestBase);
            if(response == null){
                throw new HttpException("response is null");
            }
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                callback.accept(response);
            }else{
                throw new HttpException("response status code is "+response.getStatusLine().getStatusCode());
            }

        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            log.error("", e);
        }finally {
            try {
                release(client, response);
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }

    public static String getResponseEntity(HttpResponse response, final String defaultCharset) throws IOException {
        HttpEntity resEntity = response.getEntity();
        return EntityUtils.toString(resEntity, defaultCharset);
    }

    /**释放资源*/
    public static void release(CloseableHttpClient httpClient, CloseableHttpResponse httpResponse) throws IOException {
        if (httpResponse != null) {
            httpResponse.close();
        }
        if (httpClient != null) {
            httpClient.close();
        }
    }

}

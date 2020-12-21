package com.tpf.util.http;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;

public class HttpUtil {
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
        CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
        return client;
    }

    /**
     * 发送请求
     * @param requestBase 请求对象,可通过{@code HttpRequestBuilder}创建
     * */
    public static String send(CloseableHttpClient client, HttpRequestBase requestBase) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        if(client == null){
            client = getHttpClient();
        }
        HttpResponse response = client.execute(requestBase);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String res = EntityUtils.toString(response.getEntity());
            return res;
        }
        return null;
    }

    /**
     * put 请求
     *
     * */
    public static String put(String path, Map<String, String> headers, String body) throws NoSuchAlgorithmException, IOException, KeyManagementException {
        log.info("path={}, headers={}, body={}", path, headers, body);
        HttpRequestBase requestBase = new HttpRequestBuilder().method(HttpMethod.PUT).url(path).headers(headers).stringEntity(body).build();
        return send(null, requestBase);
    }

    /**
     * get 请求
     * @return 响应内容
     * */
    public static String get(String path, Map<String, String> headers,  Map<String, Object> param) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        log.info("path={}, headers={}, param={}", path, headers, param);
        HttpRequestBase requestBase = new HttpRequestBuilder().method(HttpMethod.GET).url(path).headers(headers).urlParam(param).build();
        return send(null, requestBase);
    }

    /**
     * delete 请求
     * @return 响应内容
     * */
    public static String delete(String path, Map<String, String> headers,  Map<String, Object> param) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        log.info("path={}, headers={}, param={}", path, headers, param);
        HttpRequestBase requestBase = new HttpRequestBuilder().method(HttpMethod.DELETE).url(path).headers(headers).urlParam(param).build();
        return send(null, requestBase);
    }

    /**
     * delete 请求
     * @return 响应内容
     * */
    public static String postForm(String path, Map<String, String> headers,  Map<String, Object> param) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        log.info("path={}, headers={}, param={}", path, headers, param);
        HttpRequestBase requestBase = new HttpRequestBuilder().method(HttpMethod.POST).url(path).headers(headers).formEntity(param).build();
        return send(null, requestBase);
    }

}

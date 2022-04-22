package com.source.utils;

import com.source.constant.Constant;
import org.apache.http.*;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Source
 * @date 2022/04/05/17:54
 */
public class HttpUtils {

    /**
     * socket连接超时
     */
    private static final int DEFAULT_SOCKET_TIMEOUT = 15000;

    /**
     * 请求超时
     */
    private static final int DEFAULT_CONNECT_TIMEOUT = 30000;

    /**
     * 是否自动重定向
     */
    private static final boolean REDIRECT = false;

    /**
     * HttpClient对象
     */
    private static final CloseableHttpClient HTTP_CLIENT;

    /**
     * 是否使用代理
     */
    private static final boolean USE_PROXY = false;

    private static String baseUrl = "";

    private static final String USER_AGENT = "user-agent";
    private static final String USER_AGENT_VALUE = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)";
    private static final String CONNECTION = "connection";
    private static final String CONNECTION_VALUE = "Keep-Alive";
    private static final String ACCEPT = "accept";
    private static final String UTF8 = "utf-8";
    private static final String GBK = "GBK";

    // 静态代码块初始化配置
    static {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
                .setSocketTimeout(DEFAULT_SOCKET_TIMEOUT)
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .setRedirectsEnabled(REDIRECT)
                .build();
        HTTP_CLIENT = custom().setDefaultRequestConfig(config).build();
    }

    /**
     * 创建httpClientBuilder
     *
     * @return org.apache.http.impl.client.HttpClientBuilder
     * @author XanderYe
     * @date 2020/2/14
     */
    private static HttpClientBuilder custom() {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        // 忽略证书
        httpClientBuilder.setSSLSocketFactory(ignoreCertificates());
        if (USE_PROXY) {
            // 使用代理
            httpClientBuilder.setProxy(new HttpHost("127.0.0.1", 8888));
        }
        return httpClientBuilder;
    }

    /**
     * 忽略证数配置
     *
     * @param
     * @return org.apache.http.conn.ssl.SSLConnectionSocketFactory
     * @author XanderYe
     * @date 2020/2/14
     */
    private static SSLConnectionSocketFactory ignoreCertificates() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build();
            return new SSLConnectionSocketFactory(sslContext);
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 向指定 URL 发送GET方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) throws IOException {
        String urlNameString = url + "?" + param;
        URL realUrl = new URL(urlNameString);
        URLConnection connection = realUrl.openConnection();
        StringBuilder result = new StringBuilder();
        connection.setRequestProperty(Constant.USER_AGENT, Constant.USER_AGENT_VALUE);
        connection.setRequestProperty(Constant.CONNECTION, Constant.CONNECTION_VALUE);
        connection.setRequestProperty(Constant.ACCEPT, "*/*");
        connection.connect();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), Constant.GBK))) {
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
        }
        return result.toString();
    }

    /**
     * post请求基础方法
     *
     * @param url
     * @param headers
     * @param params
     * @return cn.xanderye.util.HttpUtil.ResEntity
     * @author XanderYe
     * @date 2020/2/4
     */
    public static HttpUtil.ResEntity doPost(String url, Map<String, Object> headers, String cookies, Map<String, Object> params) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        // 拼接参数
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> pairs = new ArrayList<>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String value = entry.getValue() == null ? null : (entry.getValue()).toString();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 添加headers
        addHeaders(httpPost, headers);
        // 添加cookies
        addCookies(httpPost, cookies);
        CloseableHttpResponse response = null;
        HttpEntity resultEntity = null;
        try {
            HttpClientContext httpClientContext = new HttpClientContext();
            response = HTTP_CLIENT.execute(httpPost, httpClientContext);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                resultEntity = response.getEntity();
                if (resultEntity != null) {
                    String res = EntityUtils.toString(resultEntity, "UTF-8");
                    String cookieString = getCookieString(response);
                    HttpUtil.ResEntity resEntity = new HttpUtil.ResEntity();
                    resEntity.setResponse(res);
                    resEntity.setCookies(formatCookies(cookieString));
                    return resEntity;
                }
            } else {
                throw new IOException(MessageFormat.format("Request error with error code {0}.", statusCode));
            }
        } finally {
            try {
                if (resultEntity != null) {
                    EntityUtils.consume(resultEntity);
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HttpUtil.ResEntity();
    }

    public static void setBaseUrl(String base) {
        baseUrl = base;
        if ('/' != base.charAt(base.length() - 1)) {
            baseUrl += "/";
        }
    }

    /**
     * 格式化cookie
     *
     * @param cookieString
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author XanderYe
     * @date 2020/4/1
     */
    public static Map<String, Object> formatCookies(String cookieString) {
        Map<String, Object> cookieMap = new HashMap<>(16);
        if (cookieString != null && !"".equals(cookieString)) {
            String[] cookies = cookieString.split(";");
            if (cookies.length > 0) {
                for (String parameter : cookies) {
                    int eqIndex = parameter.indexOf("=");
                    if (eqIndex > -1) {
                        String k = parameter.substring(0, eqIndex).trim();
                        String v = parameter.substring(eqIndex + 1).trim();
                        if (null != v && !"".equals(v)) {
                            cookieMap.put(k, v);
                        }
                    }
                }
            }
        }
        return cookieMap;
    }

    /**
     * 从请求头中获取cookie字符串
     *
     * @param response
     * @return java.lang.String
     * @author XanderYe
     * @date 2021/1/26
     */
    public static String getCookieString(CloseableHttpResponse response) {
        Header[] headers = response.getHeaders("Set-Cookie");
        return Arrays.stream(headers).map(Header::getValue).collect(Collectors.joining("; "));
    }

    /**
     * 添加cookie
     *
     * @param httpRequestBase
     * @return void
     * @author XanderYe
     * @date 2020-03-15
     */
    private static void addHeaders(HttpRequestBase httpRequestBase, Map<String, Object> headers) {
        // 设置默认UA
        httpRequestBase.setHeader("User-Agent", "PolyCity/4.4.2 (iPhone; iOS 15.1; Scale/3.00)");
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue() == null ? "" : String.valueOf(entry.getValue());
                httpRequestBase.setHeader(key, value);
            }
        }
    }

    /**
     * 添加cookie
     *
     * @param cookies
     * @author XanderYe
     * @date 2020-03-15
     */
    private static void addCookies(HttpRequestBase httpRequestBase, String cookies) {
        if (cookies != null && !cookies.isEmpty()) {
            httpRequestBase.addHeader("Cookie", cookies);
        }
    }

    public static String sendCookieGet(String url, String param, String cookie) throws IOException {
        String urlNameString = url + "?" + param;
        URL realUrl = new URL(urlNameString);
        URLConnection connection = realUrl.openConnection();
        StringBuilder result = new StringBuilder();
        connection.setRequestProperty(USER_AGENT, USER_AGENT_VALUE);
        connection.setRequestProperty(CONNECTION, CONNECTION_VALUE);
        connection.setRequestProperty("cookie", cookie);
        connection.setRequestProperty(ACCEPT, "*/*");
        connection.connect();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), GBK))) {
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
//            log.error("发送GET请求出现异常！", e);
        }
        return result.toString();
    }

    public static String sendTxCookieGet(String url, String cookie) throws IOException {
        URL realUrl = new URL(url);
        URLConnection connection = realUrl.openConnection();
        StringBuilder result = new StringBuilder();
        connection.setRequestProperty(USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.204 Safari/537.36");
        connection.setRequestProperty(CONNECTION, CONNECTION_VALUE);
        connection.setRequestProperty("Cookie", cookie);
        connection.setRequestProperty(ACCEPT, "*/*");
        connection.setRequestProperty("Referer", "https://v.qq.com");
        connection.connect();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), GBK))) {
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
//            log.error("发送GET请求出现异常！", e);
        }
        return result.toString();
    }

    public static String sendTxSignInGet(String url, String cookie) throws IOException {
        URL realUrl = new URL(url);
        URLConnection connection = realUrl.openConnection();
        StringBuilder result = new StringBuilder();
        connection.setRequestProperty(USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.204 Safari/537.36");
        connection.setRequestProperty(CONNECTION, CONNECTION_VALUE);
        connection.setRequestProperty("Cookie", cookie);
        connection.setRequestProperty(ACCEPT, "*/*");
        connection.setRequestProperty("Referer", "https://m.v.qq.com");
        connection.connect();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), GBK))) {
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
//            log.error("发送GET请求出现异常！", e);
        }
        return result.toString();
    }

    public static Map<String, List<String>> getHeaderTxCookieGet(String url, String cookie) throws IOException {
        URL realUrl = new URL(url);
        URLConnection connection = realUrl.openConnection();
        StringBuilder result = new StringBuilder();
        connection.setRequestProperty(USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.204 Safari/537.36");
        connection.setRequestProperty(CONNECTION, CONNECTION_VALUE);
        connection.setRequestProperty("Cookie", cookie);
        connection.setRequestProperty(ACCEPT, "*/*");
        connection.setRequestProperty("Referer", "https://v.qq.com");
        connection.connect();
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), GBK))) {
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
//            log.error("发送GET请求出现异常！", e);
        }
        return headerFields;
    }

    public static class ResEntity {

        private byte[] bytes;

        private String response;

        private Map<String, Object> cookies;

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }

        public Map<String, Object> getCookies() {
            return cookies;
        }

        public void setCookies(Map<String, Object> cookies) {
            this.cookies = cookies;
        }

        @Override
        public String toString() {
            return "ResEntity{" +
                    "response='" + response + '\'' +
                    ", cookies=" + cookies +
                    '}';
        }
    }

//    public static void main(String[] args) {
//        try {
//            String s = sendTxSignInGet("https://vip.video.qq.com/fcgi-bin/comm_cgi?name=hierarchical_task_system&cmd=2&_=", "RK=F+Lcy/OuOq; ptcz=b2be90880d09ae4fbd2b24558aac7e43279c75bed6b6d08e2a6021a24625bd66; pgv_pvid=4609753200; video_guid=f83e019259d5f41b; video_platform=2; tvfe_boss_uuid=aa014115cd4bc69b; pgv_info=ssid=s8297341560; _qpsvr_localtk=0.45595936371425805; ptui_loginuin=1134496699; main_login=qq; vqq_access_token=B894D6AF3FDE84706BAE34EE0E1E2803; vqq_appid=101483052; vqq_openid=580CD85156B6A2D82C971143B3DEE2F5; vqq_vuserid=495342549; vqq_vusession=LRq2tNCkfpeZ5GIgwUxmgA..");
//            System.out.println(s);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
}

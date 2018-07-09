package com.infinitus.husky;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class HttpUtils {

    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * @param url        请求地址
     * @param parameters 提交的请求参数
     */
    public static String get(String url, final NameValuePair... parameters) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("get请求地址不能为空");
        } else {
            String respTxt = StringUtils.EMPTY;
            HttpClient httpClient = createHttpClientInstance(false);
            try {
                if (null != parameters && parameters.length > 0) {
                    String encodedParams = encodeParameters(parameters);
                    if (-1 == url.indexOf("?")) {
                        url += "?" + encodedParams;
                    } else {
                        url += "&" + encodedParams;
                    }
                }

                HttpGet getMethod = new HttpGet(url);

                //设置请求和传输超时时间
                RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(4000).setConnectTimeout(2000).build();
                getMethod.setConfig(requestConfig);
                HttpResponse resp = httpClient.execute(getMethod);
                if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    respTxt = EntityUtils.toString(resp.getEntity(), ConstantUtil.DEFAULT_ENCODING);
                }
            } catch (Exception e) {
                logger.error("httpGet请求异常：{},错误详细是：{}", url, e.getMessage());
            } finally {
                HttpClientUtils.closeQuietly(httpClient);
            }

            return respTxt;
        }
    }
    public static String postJson(String postUrl, String json)throws Exception{
        return postJson(postUrl,json,false);
    }

    public static String postJson(String postUrl, String json,boolean ishttps)throws Exception {
        HttpClient client = createHttpClientInstance(ishttps);
        HttpPost post = new HttpPost(postUrl);
        String resp = null;
        try {
            StringEntity stringEntity = new StringEntity(json, ConstantUtil.DEFAULT_ENCODING);
            stringEntity.setContentEncoding(ConstantUtil.DEFAULT_ENCODING);
            stringEntity.setContentType(ConstantUtil.JSON_TYPE);
            post.setEntity(stringEntity);

            HttpResponse res = client.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                resp = EntityUtils.toString(entity, ConstantUtil.DEFAULT_ENCODING);
            }
        } catch (Exception ex) {
            logger.error("httpPost json异常：{},错误详细是：{}", postUrl, ex);
            throw ex;
        } finally {
            HttpClientUtils.closeQuietly(client);
        }
        return resp;
    }

    /**
     * 将NameValuePairs数组转变为字符串
     */
    private static String encodeParameters(final NameValuePair[] nameValues) {
        if (nameValues == null || nameValues.length == 0) {
            return StringUtils.EMPTY;
        }
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < nameValues.length; i++) {
            NameValuePair nameValue = nameValues[i];
            if (i == 0) {
                buffer.append(nameValue.getName() + "=" + nameValue.getValue());
            } else {
                buffer.append("&" + nameValue.getName() + "=" + nameValue.getValue());
            }
        }
        return buffer.toString();
    }

    /**
     * 依据浏览器将下载文件名编码，以防乱码
     *
     * @param filename
     * @param request
     * @return
     */
    public static String encodeFilename(String filename, HttpServletRequest request) {
        /**
         * 获取客户端浏览器和操作系统信息 在IE浏览器中得到的是：User-Agent=Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; Maxthon; Alexa
         * Toolbar) 在Firefox中得到的是：User-Agent=Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.7.10) Gecko/20050717
         * Firefox/1.0.6
         */
        String agent = request.getHeader("USER-AGENT");
        if (agent == null) {
            return filename;
        }
        try {
            if (-1 != agent.indexOf("MSIE")) {
                String newFileName = URLEncoder.encode(filename, ConstantUtil.DEFAULT_ENCODING);
                newFileName = StringUtils.replace(newFileName, "+", "%20");
                if (newFileName.length() > 150) {
                    newFileName = new String(filename.getBytes(ConstantUtil.DEFAULT_ENCODING), "ISO8859-1");
                    newFileName = StringUtils.replace(newFileName, " ", "%20");
                }
                return newFileName;
            }
            if (-1 != agent.indexOf("Mozilla")) {
                return "=?UTF-8?B?" + (new String(Base64.encodeBase64(filename.getBytes("UTF-8")))) + "?=";
            }

            return filename;
        } catch (Exception ex) {
            return filename;
        }
    }

    public static HttpClient createHttpClientInstance(boolean isHttps) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        RequestConfig.Builder reqConfigBuilder = RequestConfig.custom();
        reqConfigBuilder.setSocketTimeout(6000);
        reqConfigBuilder.setConnectTimeout(3000);
        reqConfigBuilder.setConnectionRequestTimeout(3000);

        httpClientBuilder.setMaxConnTotal(11000);
        httpClientBuilder.setMaxConnPerRoute(4000);

        RequestConfig reqConfig = reqConfigBuilder.build();

        try {
            if (isHttps) {
                javax.net.ssl.TrustManager[] trustAllCerts = {new MyTrustManager()};
                javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, null);
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sc, hv);
                httpClientBuilder.setSSLSocketFactory(sslsf);
            }
            httpClientBuilder.setDefaultRequestConfig(reqConfig);
        } catch (NoSuchAlgorithmException e) {
            logger.error("{}", e);
        } catch (KeyManagementException e) {
            logger.error("{}", e);
        }
        return httpClientBuilder.build();
    }

    static class MyTrustManager implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
            return;
        }
    }

    final static X509HostnameVerifier hv = new X509HostnameVerifier() {
        public boolean verify(String urlHostName, SSLSession session) {
            return true;
        }

        public void verify(String host, SSLSocket ssl) throws IOException {
        }

        public void verify(String host, X509Certificate cert) throws SSLException {
        }

        public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
        }
    };


    public static String getSap(String url, final Header[] headers) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("get请求地址不能为空");
        } else {
            String respTxt = StringUtils.EMPTY;
            HttpClient httpClient = createHttpClientInstance(false);
            try {
                HttpGet getMethod = new HttpGet(url);
                getMethod.setHeaders(headers);

                //设置请求和传输超时时间
                RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(4000).setConnectTimeout(2000).build();
                getMethod.setConfig(requestConfig);
                HttpResponse resp = httpClient.execute(getMethod);
                if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    respTxt = EntityUtils.toString(resp.getEntity(), ConstantUtil.DEFAULT_ENCODING);
                }
            } catch (Exception e) {
               logger.error("httpGet请求异常：{},错误详细是：{}", url, e.getMessage());
                throw new RuntimeException("httpGet请求异常",e.getCause());
            } finally {
                HttpClientUtils.closeQuietly(httpClient);
            }

            return respTxt;
        }
    }

    public static String post(String url,Map<String,String> params,boolean isHttps)throws Exception{
        HttpClient httpClient = createHttpClientInstance(isHttps);
        HttpPost post=new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        for (String key : params.keySet()) {
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }
        post.setEntity(EntityBuilder.create().setParameters(nvps).build());
        String resp=null;
        try {
            HttpResponse res=httpClient.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                resp = EntityUtils.toString(entity, ConstantUtil.DEFAULT_ENCODING);
                logger.info("{}请求成功，返回body{}",res.getStatusLine().getStatusCode(),resp);
            }else{
                HttpEntity entity = res.getEntity();
                logger.error("{}请求失败,返回body{}", res.getStatusLine().getStatusCode(), EntityUtils.toString(entity, ConstantUtil.DEFAULT_ENCODING));
            }
        } catch (IOException e) {
            logger.error("httpPost请求异常：{},错误详细是：{}", url, e.getMessage());
            throw new RuntimeException("httpPost请求异常");
        }finally {
            HttpClientUtils.closeQuietly(httpClient);
        }
        return resp;
    }

    public static String postBinary(String url,byte[] binary,boolean isHttps)throws Exception{
        HttpClient httpClient = createHttpClientInstance(isHttps);
        HttpPost post=new HttpPost(url);
        post.setEntity(EntityBuilder.create().setBinary(binary).build());
        String resp=null;
        try {
            HttpResponse res=httpClient.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                resp = EntityUtils.toString(entity, ConstantUtil.DEFAULT_ENCODING);
                logger.info("{}请求成功，返回body{}",res.getStatusLine().getStatusCode(),resp);
            }else{
                HttpEntity entity = res.getEntity();
                logger.error("{}请求失败,返回body{}", res.getStatusLine().getStatusCode(), EntityUtils.toString(entity, ConstantUtil.DEFAULT_ENCODING));
            }
        } catch (IOException e) {
            logger.error("httpPost请求异常：{},错误详细是：{}", url, e.getMessage());
            throw new RuntimeException("httpPost请求异常");
        }finally {
            HttpClientUtils.closeQuietly(httpClient);
        }
        return resp;
    }
}
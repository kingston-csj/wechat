package pers.kinson.wechat.util;

import jforgame.commons.JsonUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import pers.kinson.wechat.base.LifeCycle;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientManager implements LifeCycle {

    HttpClient httpClient;

    @Override
    public void init() {
        httpClient = HttpClients.createDefault();
    }

    public <T> T get(String url, Map<String, Object> params, Class<T> responseClazz) throws IOException {
        return get(url, null, params, responseClazz);
    }

    public <T> T get(String url, Map<String, String> headers, Map<String, Object> params, Class<T> responseClazz) throws IOException {
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            List<NameValuePair> urlParam = new ArrayList<>();
            if (params != null && !params.isEmpty()) {
                params.forEach((k, v) -> {
                    urlParam.add(new BasicNameValuePair(k, v.toString()));
                });
                uriBuilder.setParameters(urlParam);
            }
            // 构建 HttpGet 请求
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            if (headers != null && !headers.isEmpty()) {
                headers.forEach(httpGet::addHeader);
            }
            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
//                LoggerUtil.info(LoggerFunction.REQUEST, "module", "get_bad_status", "url", url, "params", JsonUtil.object2String(params));
            }
            HttpEntity entity = response.getEntity();
            try {
                return JsonUtil.string2Object(EntityUtils.toString(entity), responseClazz);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        return null;
    }

    public <T> T post(String url, Map<String, Object> params, Class<T> responseClazz) throws IOException {
        return post(url, null, params, responseClazz);
    }

    public <T> T post(String url, Map<String, String> headers, Map<String, Object> params, Class<T> responseClazz) throws IOException {
        //创建请求对象
        HttpPost httpPost = new HttpPost(url);
        if (params != null && !params.isEmpty()) {
            String json = JsonUtil.object2String(params);
            StringEntity entity = new StringEntity(json, "UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
        }
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(httpPost::addHeader);
        }
//        httpPost.addHeader("accept","application/json, text/plain, */*");
//        httpPost.addHeader("Content-Type","application/json; charset=UTF-8");
//        httpPost.addHeader("accept-language:","zh-CN,zh;q=0.9,en;q=0.8");
        //发送请求，接受响应结果
        HttpResponse response = httpClient.execute(httpPost);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200 && statusCode != 202) {
//            LoggerUtil.info(LoggerFunction.REQUEST, "module", "post_bad_status", "url", url, "statusCode", statusCode, "params", JsonUtil.object2String(params));
        }
        HttpEntity entity = response.getEntity();
        if (entity == null) {
//            LoggerUtil.info(LoggerFunction.REQUEST, "module", "post_bad_response", "url", url, "statusCode", statusCode, "params", JsonUtil.object2String(params));
            return null;
        }
        try {
            return JsonUtil.string2Object(EntityUtils.toString(entity), responseClazz);
        } catch (Exception e) {
            e.printStackTrace();
//            LoggerUtil.error(String.format("json %s 解析错误", JsonUtil.object2String(entity)), e);
        }
        return null;
    }
}

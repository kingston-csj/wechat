package pers.kinson.wechat.util;

import jforgame.commons.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.config.SystemConfig;
import pers.kinson.wechat.ui.controller.ProgressMonitor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpClientManager implements LifeCycle {

    HttpClient httpClient;

    @Override
    public void init() {
        httpClient = HttpClients.createDefault();
    }

    public <T> T get(String url, Object request, Class<T> responseClazz) throws IOException {
        String params = JsonUtil.object2String(request);
        return get(url, null, JsonUtil.string2Map(params), responseClazz);
    }

    public <T> T get(String url, Map<String, Object> params, Class<T> responseClazz) throws IOException {
        return get(url, null, params, responseClazz);
    }

    public <T> T get(String url, Map<String, String> headers, Map<String, Object> params, Class<T> responseClazz) throws IOException {
        try {
            url = SystemConfig.getInstance().getServer().getRemoteHttpUrl() + url;
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

    public <T> T post(String url, Object request, Class<T> responseClazz) throws IOException {
        String params = JsonUtil.object2String(request);
        return post(url, null, JsonUtil.string2Map(params), responseClazz);
    }

    public <T> T post(String url, Map<String, Object> params, Class<T> responseClazz) throws IOException {
        return post(url, null, params, responseClazz);
    }

    public <T> T post(String url, Map<String, String> headers, Map<String, Object> params, Class<T> responseClazz) throws IOException {
        url = SystemConfig.getInstance().getServer().getRemoteHttpUrl() + url;
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

    public void downloadFile(String fileUrl, String localFilePath, ProgressMonitor progressMonitor) throws IOException {
        if (StringUtils.isEmpty(fileUrl) || StringUtils.isEmpty(localFilePath)) {
            return;
        }
        // 创建自定义的响应拦截器实例
        HttpResponseInterceptor2 interceptor = new HttpResponseInterceptor2() {
            @Override
            public void process(HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    long contentLength = entity.getContentLength();
                    progressMonitor.setMaximum(contentLength);
                }
            }

            public void incrementBytesRead(long bytesRead) {
                progressMonitor.updateTransferred(bytesRead);
            }
        };

        // 创建HttpClient，并添加响应拦截器
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .addInterceptorFirst(interceptor)
                .build();

        HttpGet httpGet = new HttpGet(fileUrl);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // 创建本地文件对象
                File localFile = new File(localFilePath);
                // 创建输出流，用于将下载的数据写入本地文件
                try (OutputStream outputStream = Files.newOutputStream(localFile.toPath());
                     InputStream inputStream = entity.getContent()) {
                    // 缓冲区大小，可根据需要调整
                    byte[] buffer = new byte[1024];
                    int length;
                    // 从输入流读取数据并写入输出流，实现文件下载
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                        // 直接使用局部变量中的拦截器实例来更新已读取字节数和进度条
                        interceptor.incrementBytesRead(length);
                    }
                    System.out.println("文件已成功下载到本地！");
                } catch (IOException e) {
                    log.error("", e);
                    throw e;
                }
            } else {
                System.out.println("响应实体为空，无法下载文件。");
            }
        } catch (IOException e) {
            log.error("", e);
            throw e;
        } finally {
            httpClient.close();
        }
    }

    // 假设的进度条更新方法，实际中需要根据使用的终端或图形界面来实现
    private void updateProgressBar(int progress) {
        System.out.println("下载进度：" + progress + "%");
        // 这里如果是在图形界面应用中，比如使用Swing或JavaFX等，需要按照相应的方式更新进度条组件
    }

    // 自定义的响应拦截器接口
    private interface HttpResponseInterceptor2 extends HttpResponseInterceptor {
        void incrementBytesRead(long bytesRead);
    }
}

package com.code2020.joker.kit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpClientKit {

    private static Logger logger = LoggerFactory.getLogger(HttpClientKit.class);

    public static String getBody(String url) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        HttpGet httpGet = new HttpGet(url);

        response = client.execute(httpGet);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            HttpEntity entity = response.getEntity();
            String html = EntityUtils.toString(entity);
            return html;
        }else{
            logger.error("请求失败");
            return "error";
        }
    }

}

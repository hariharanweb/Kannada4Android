package com.ocrapp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class HttpService {
    private String url;
    private List<NameValuePair> httpParams;
    private String httpMethod;

    public HttpService(String url, List<NameValuePair> httpParams, String httpMethod) {
        this.url = url;
        this.httpParams = httpParams;
        this.httpMethod = httpMethod;
    }

    public String getContent() throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpResponse response = createResponse(client);
        HttpEntity responseEntity = response.getEntity();
        return createString(responseEntity);
    }

    private HttpResponse createResponse(DefaultHttpClient client) throws IOException {
        HttpResponse response;
        if (httpMethod.equals(HttpPost.METHOD_NAME)) {
            HttpEntityEnclosingRequestBase httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(httpParams));
            response = client.execute(httpPost);
        } else {
            url = url + "?";
            for (NameValuePair pair : httpParams) {
                url = url + pair.getName() + "=" + pair.getValue() + "&";
            }
            String completedUrl = url.substring(0, url.length() - 1);
            HttpGet httpGet = new HttpGet(completedUrl);
            response = client.execute(httpGet);
        }
        return response;
    }

    private String createString(HttpEntity responseEntity) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
        StringBuilder html = new StringBuilder();
        String val;
        while ((val = bufferedReader.readLine()) != null) {
            html.append(val);
        }
        return html.toString();
    }
}

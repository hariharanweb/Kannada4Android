package com.ocrapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpService {
    private String url;
    private List<NameValuePair> httpParams;
    private String httpMethod;
	private final DefaultHttpClient httpClient;

    public HttpService(String url, List<NameValuePair> httpParams, String httpMethod, DefaultHttpClient httpClient) {
        this.url = url;
        this.httpParams = httpParams;
        this.httpMethod = httpMethod;
		this.httpClient = httpClient;
    }

    public String getContent() throws IOException {
        HttpResponse response = createResponse();
        return createString(response.getEntity());
    }

    private HttpResponse createResponse() throws IOException {
        if (httpMethod.equals(HttpPost.METHOD_NAME)) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(httpParams));
            return httpClient.execute(httpPost);
        } else {
            url = url + "?";
            for (NameValuePair pair : httpParams) {
                url = url + pair.getName() + "=" + pair.getValue() + "&";
            }
            String completedUrl = url.substring(0, url.length() - 1);
            return httpClient.execute(new HttpGet(completedUrl));
        }
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

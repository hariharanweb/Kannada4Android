package com.ocrapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class KannadaMeaningFinder {
    private static final String kannadaFindUrl = "http://www.kannadakasturi.com/kasturiDictionary/Searchword.asp";
    private static final String englishMeaningUrl = "http://www.kannadakasturi.com/kasturiDictionary/ShowMeaning.asp";
    private String kannadaText;

    public KannadaMeaningFinder(String kannadaText) {
        this.kannadaText = kannadaText;
    }

    public String getKannadaMeaning() throws IOException{
		HttpService httpService = getKannadaPage();
		String kannadaWordId = getKannadaWordId(httpService.getContent());

        httpService = getEnglishPage(kannadaWordId);
        
        return parseEnglishWord(httpService.getContent());
    }

	private HttpService getEnglishPage(String kannadaWordId) {
		HttpService httpService;
		List<NameValuePair> httpParams = new ArrayList<NameValuePair>(1);
        httpParams.add(new BasicNameValuePair("kwid", kannadaWordId));
        
        httpService = new HttpService(englishMeaningUrl, httpParams, HttpGet.METHOD_NAME, new DefaultHttpClient());
		return httpService;
	}

	private HttpService getKannadaPage() {
		List<NameValuePair> httpParams = new ArrayList<NameValuePair>(3);
        httpParams.add(new BasicNameValuePair("SearchType", "0"));
        httpParams.add(new BasicNameValuePair("submit", "Find"));
        httpParams.add(new BasicNameValuePair("kaword", kannadaText));

        HttpService httpService = new HttpService(kannadaFindUrl, httpParams, HttpPost.METHOD_NAME, new DefaultHttpClient());
		return httpService;
	}

    private String parseEnglishWord(String content) {
        String contentLocation = "class=\"tdformatblack\">";
        int startIndex = content.indexOf(contentLocation) + contentLocation.length();
        return content.substring(startIndex, content.indexOf("</font>", startIndex));
    }

    private String getKannadaWordId(String httpContent) {
        String contentLocation = "javascript:getMeaning('";
        int startIndex = httpContent.indexOf(contentLocation) + contentLocation.length();
        return httpContent.substring(startIndex, httpContent.indexOf('\'', startIndex));
    }


}




package com.akshayboth.wikisearch.util;

import com.google.gson.JsonSyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by akshayboth on 30/01/18.
 */

public class HttpUtil {
    private String url;
    private String type;
    private HashMap<String, String> param;
    private String postrequest;

    public HttpUtil() {
    }

    private int socketTimeOut = 0, connectionTimeOut = 0;
    HttpClient httpclient = new DefaultHttpClient();


    public HttpUtil(String url, String type, HashMap<String, String> param, String postrequest) {
        this.url = url;
        this.type = type;
        this.param = param;
        this.postrequest = postrequest;
    }


    public String getStringResponse() {
        String jsonresponse = "";
        try {
            System.out.println("url.......... " + url);
            System.out.println("type........ " + type);

            HttpResponse httpResponse = getHttpResponse();
            if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                HttpEntity httpEntity = httpResponse.getEntity();
                jsonresponse = EntityUtils.toString(httpEntity);
                if (jsonresponse.equalsIgnoreCase("[]")) {
                    jsonresponse = "";
                }
                System.out.println("HttpUtil Response is .... " + jsonresponse);

            } else {
                return "null";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonresponse;
    }

    public void getVoidResponse() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashMap<String, String> getParam() {
        return param;
    }

    public void setParam(HashMap<String, String> param) {
        this.param = param;
    }

    private HttpResponse getHttpResponse() {
        HttpResponse httpResponse = null;
        /*HttpParams httpParameters = new BasicHttpParams();
// Set the timeout in milliseconds until a connection is established.
// The default value is zero, that means the timeout is not used.
        int timeoutConnection = 300000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
// Set the default socket timeout (SO_TIMEOUT)
// in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 500000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);*/
        if (socketTimeOut != 0 || connectionTimeOut != 0) {
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeOut);
            HttpConnectionParams.setSoTimeout(httpParameters, socketTimeOut);
            /*if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }*/

            httpclient = new DefaultHttpClient(httpParameters);
        }
        try {
            switch (type) {
                case "GET":
                    HttpGet httpGet = new HttpGet(url);
                    httpGet.setHeader("viksit-user-agent", getTocken());
                    httpResponse = httpclient.execute(httpGet);
                    break;
                case "POST":
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setHeader("viksit-user-agent", getTocken());

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    if (param != null) {
                        for (String key : param.keySet()) {
                            nameValuePairs.add(new BasicNameValuePair(key, param.get(key)));
                        }
                    }

                    if (postrequest != null) {
                        HttpEntity entity = new StringEntity(postrequest);
                        httpPost.setEntity(entity);
                        //httpPost.setHeader("Accept", "application/json");
                        httpPost.setHeader("Content-type", "application/json");
                        //httpPost.setHeader("Authorization","" );
                    }
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    httpResponse = httpclient.execute(httpPost);
                    break;
                case "PUT":
                    HttpPut httpPut = new HttpPut(url);
                    httpPut.setHeader("viksit-user-agent", getTocken());
                    if (postrequest != null) {
                        HttpEntity entity = new StringEntity(postrequest);
                        httpPut.setEntity(entity);
                        httpPut.setHeader("Accept", "application/json");
                        httpPut.setHeader("Content-type", "application/json");
                        httpPut.setHeader("Authorization", "");
                    }
                    if (param != null) {
                        List<NameValuePair> putValuePairs = new ArrayList<NameValuePair>();

                        for (String key : param.keySet()) {
                            putValuePairs.add(new BasicNameValuePair(key, param.get(key)));
                        }
                        httpPut.setEntity(new UrlEncodedFormEntity(putValuePairs));

                    }
                    httpResponse = httpclient.execute(httpPut);
                    break;
                default:
                    httpResponse = httpclient.execute(new HttpGet(url));
                    break;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JsonSyntaxException jse) {
            jse.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return httpResponse;
    }

    public String getPostrequest() {
        return postrequest;
    }

    public void setPostrequest(String postrequest) {
        this.postrequest = postrequest;
    }

    public int getSocketTimeOut() {
        return socketTimeOut;
    }

    public void setSocketTimeOut(int socketTimeOut) {
        this.socketTimeOut = socketTimeOut;
    }

    public int getConnectionTimeOut() {
        return connectionTimeOut;
    }

    public void setConnectionTimeOut(int connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    public String getTocken() {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date = new Date();
        String timeNow = dateFormat.format(date).toString();
        timeNow = timeNow.replaceAll("/", "").replaceAll(":", "").replaceAll(" ", "");
        System.err.println(timeNow);

        Random r = new Random();
        int Low = 1;
        int High = 24;
        int Result = r.nextInt(High - Low) + Low;

        for (int j = 0; j < 3; j++) {

            StringBuffer randStr = new StringBuffer();
            char ch;
            Result = r.nextInt(High - Low) + Low;
            for (int i = 0; i < 3; i++) {
                Result = r.nextInt(High - Low) + Low;
                // Result = r.nextInt(High-Low) + Low;
                String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                ch = CHAR_LIST.charAt(Result);
                randStr.append(ch);
            }

            if (j == 0) {
                timeNow = new StringBuffer(timeNow).insert(timeNow.length() - 10, randStr).toString();
            }
            if (j == 1) {
                timeNow = new StringBuffer(timeNow).insert(timeNow.length() - 3, randStr).toString();
            }
            if (j == 2) {
                timeNow = new StringBuffer(timeNow).insert(timeNow.length() - 7, randStr).toString();
            }

        }
        return "viksit-" + timeNow;
    }

}

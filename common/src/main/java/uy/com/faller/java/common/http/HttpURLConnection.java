package uy.com.faller.java.common.http;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

public class HttpURLConnection {

  private final String USER_AGENT = "Mozilla/5.0";

  // HTTP GET request
  public HttpResponse sendGet(String url) throws Exception {


    URL obj = new URL(url);
    java.net.HttpURLConnection con = (java.net.HttpURLConnection) obj.openConnection();

    // optional default is GET
    con.setRequestMethod("GET");

    //add request header
    con.setRequestProperty("User-Agent", USER_AGENT);

    int responseCode = con.getResponseCode();

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer response = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();

    return new HttpResponse(url, "", responseCode, response.toString());

  }

  // HTTP POST request
  public HttpResponse sendPost(String url,Map<String,String> params) throws Exception {

    HttpResponse response;

    URL obj = new URL(url);
    HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

    //add reuqest header
    con.setRequestMethod("POST");
    con.setRequestProperty("User-Agent", USER_AGENT);
    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

    StringBuilder urlParameters = new StringBuilder("");

    if (params != null) {
      int i = 0;
      for (Map.Entry<String, String> entry : params.entrySet()) {
        i++;
        urlParameters.append(entry.getKey()).append("=").append(entry.getValue());
        if (i != params.size()) {
          urlParameters.append("&");
        }
      }
    }

    // Send post request
    con.setDoOutput(true);
    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
    wr.writeBytes(urlParameters.toString());
    wr.flush();
    wr.close();

    int responseCode = con.getResponseCode();

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuilder responseStr = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
      responseStr.append(inputLine);
    }
    in.close();

    return new HttpResponse(url, urlParameters.toString(), responseCode, responseStr.toString());
  }

}


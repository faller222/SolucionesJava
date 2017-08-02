package uy.com.faller.java.common.http;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import uy.com.faller.java.common.exception.CommonException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ConectorRest {

  public static final int HTTP_OK = 200;


  public static void invocarOneWay(List<BasicNameValuePair> urlParameters, String restURL) throws CommonException {
    int codigo = invocar(urlParameters, restURL).getStatusLine().getStatusCode();
    if (codigo != HTTP_OK) {
      throw new RuntimeException("ERROR  - Codigo devuelto :" + codigo);
    }

  }

  public static HttpResponse invocar(List<BasicNameValuePair> urlParameters, String restURL) throws CommonException {
    HttpPost post = new HttpPost(restURL);
      try {
          post.setEntity(new UrlEncodedFormEntity(urlParameters));
          HttpClient client = new DefaultHttpClient();
          HttpResponse response = client.execute(post);

          int codigo = response.getStatusLine().getStatusCode();
          if (codigo != HTTP_OK) {
              throw new RuntimeException("ERROR  - Codigo devuelto :" + codigo);
            }
          return response;

        } catch (Exception e) {
          throw new CommonException("ERROR ", e);
        } finally {
          post.releaseConnection();
        }
    }

  public static String getBody(HttpResponse response) throws CommonException {
    String body = null;
    StringBuilder stringBuilder = new StringBuilder();
    BufferedReader bufferedReader = null;

    try {
      InputStream inputStream = response.getEntity().getContent();

      if (inputStream != null) {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        char[] charBuffer = new char[128];
        int bytesRead = -1;
        while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
          stringBuilder.append(charBuffer, 0, bytesRead);
        }
      } else {
        stringBuilder.append("");
      }
    } catch (IOException e) {
      throw new CommonException("", e);
    }
    return stringBuilder.toString();
  }


}
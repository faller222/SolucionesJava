package uy.com.faller.java.common.http;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import uy.com.faller.java.common.exception.CommonException;

import java.util.List;

public class ConectorRest {

  public static final int HTTP_OK = 200;


  public static void invocar(List<BasicNameValuePair> urlParameters, String restURL) throws CommonException {
    HttpPost post = new HttpPost(restURL);
    try {
      post.setEntity(new UrlEncodedFormEntity(urlParameters));
      HttpClient client = new DefaultHttpClient();
      HttpResponse response = client.execute(post);

      int codigo = response.getStatusLine().getStatusCode();
      if (codigo != HTTP_OK) {
        throw new RuntimeException("ERROR  - Codigo devuelto :" + codigo);
      }
    } catch (Exception e) {
      if (e instanceof CommonException) {
        throw (CommonException) e;
      }
      throw new CommonException("ERROR ", e);
    } finally {
      post.releaseConnection();
    }
  }
}
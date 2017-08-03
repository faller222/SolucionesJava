package uy.com.faller.java.common.http;

import static org.junit.Assert.*;


import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class HttpURLConnectionTest {

  @Test
  public void sistemas() throws Exception {
    HttpURLConnection conector = new HttpURLConnection();

    String url = "https://www.fing.edu.uy/inco/cursos/sistoper/notas.php";
    //state=V&id=4520304
    Map<String,String> params = new HashMap<>();
    params.put("state","V");
    params.put("id","4817448");
    params.put("id","5148956");
    params.put("id","4520304");

    HttpResponse httpResponse = conector.sendPost(url, params);

    String body = httpResponse.getBody();
    int i = body.indexOf("<div id=\"right\">");
    int f = body.indexOf("<!-- id=\"right\" -->");
    String resultDiv = body.substring(i, f).replaceAll("><", ">\n<");



    System.out.println(getH2(resultDiv));
    System.out.println(getP1(resultDiv));
    System.out.println(getP2(resultDiv));
  }

  private String getH2(String body) {
    int i = body.indexOf("<h2>");
    int f = body.indexOf("</h2>");
    return body.substring(i+4, f);
  }
  private String getP1(String body) {
    int i = body.indexOf("<p>");
    int f = body.indexOf("</p>");
    return body.substring(i+3, f);
  }
  private String getP2(String body) {
    String aux = body;
    int f=0;
    int i=0;

    f = aux.indexOf("</p>");
    aux = aux.substring(f);
    i = aux.indexOf("<p>");
    aux = aux.substring(i);
    f = aux.indexOf("</p>");

    return aux.substring(3, f);
  }
}
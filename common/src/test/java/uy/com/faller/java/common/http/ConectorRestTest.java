package uy.com.faller.java.common.http;

import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import uy.com.faller.java.common.exception.CommonException;

import java.util.ArrayList;
import java.util.List;

public class ConectorRestTest {

  @Test
  public void invocar() throws CommonException {

    String url = "http://rest-service.guides.spring.io/greeting";
    List<BasicNameValuePair> urlParameters = new ArrayList<>();


    HttpResponse response = ConectorRest.invocar(urlParameters, url);
    String body = ConectorRest.getBody(response);

    System.out.println(body);
  }

  @Test
  public void invocarParam() throws CommonException {
    String url = "http://date.jsontest.com/";
    List<BasicNameValuePair> urlParameters = new ArrayList<>();
    BasicNameValuePair param = new BasicNameValuePair("service","ip");
    urlParameters.add(param);


    HttpResponse response = ConectorRest.invocar(urlParameters, url);
    String body = ConectorRest.getBody(response);

    System.out.println(body);
  }
  
}
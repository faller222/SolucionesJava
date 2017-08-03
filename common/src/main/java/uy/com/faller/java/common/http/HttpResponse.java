package uy.com.faller.java.common.http;

public class HttpResponse {

  private String url;
  private String urlParameters;
  private Integer responseCode;
  private String body;

  public HttpResponse(String url, String urlParameters, Integer responseCode, String body) {
    this.url = url;
    this.urlParameters = urlParameters;
    this.responseCode = responseCode;
    this.body = body;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUrlParameters() {
    return urlParameters;
  }

  public void setUrlParameters(String urlParameters) {
    this.urlParameters = urlParameters;
  }

  public Integer getResponseCode() {
    return responseCode;
  }

  public void setResponseCode(Integer responseCode) {
    this.responseCode = responseCode;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }
}

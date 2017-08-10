package uy.com.faller.java.common.security.exception;

public class SoapSecurityException extends RuntimeException {

  public SoapSecurityException(String message, Throwable e) {
    super(message, e);
  }
}

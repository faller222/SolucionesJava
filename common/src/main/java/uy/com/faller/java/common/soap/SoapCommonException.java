package uy.com.faller.java.common.soap;

public class SoapCommonException extends RuntimeException {

  public SoapCommonException(Throwable cause) {
    super(cause);
  }

  public SoapCommonException(String message, Throwable cause) {
    super(message, cause);
  }

  public SoapCommonException(String message) {
    super(message);
  }
}

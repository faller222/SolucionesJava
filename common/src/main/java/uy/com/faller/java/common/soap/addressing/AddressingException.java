package uy.com.faller.java.common.soap.addressing;

public class AddressingException extends RuntimeException {

  public AddressingException(Exception e) {
    super(e);
  }


  public AddressingException(String s) {
    super(s);
  }
}

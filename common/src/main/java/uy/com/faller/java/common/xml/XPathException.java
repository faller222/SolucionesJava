package uy.com.faller.java.common.xml;


public class XPathException extends Exception {

    public static final String MENSAJE = "Error en el XPath";

    public XPathException() {
        super(MENSAJE);
    }

    public XPathException(String message) {
        super(message);
    }

    public XPathException(Throwable cause) {
        super(MENSAJE, cause);
    }

    public XPathException(String message, Throwable cause) {
        super(message, cause);
    }


}

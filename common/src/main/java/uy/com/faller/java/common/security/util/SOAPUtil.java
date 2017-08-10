package uy.com.faller.java.common.security.util;

import org.apache.commons.io.IOUtils;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.message.WSSecHeader;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import uy.com.faller.java.common.security.exception.SoapSecurityException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;


public final class SOAPUtil {

  private SOAPUtil() {
  }

  public static SOAPMessage toSOAPMessage(Document doc) {
    return toSOAPMessage(doc, SOAPConstants.SOAP_1_2_PROTOCOL);
  }

  public static SOAPMessage toSOAPMessage(Document doc, String protocol) {
    DOMSource domSource;
    SOAPMessage retorno;
    MessageFactory messageFactory;
    try {
      domSource = new DOMSource(doc);
      messageFactory = MessageFactory.newInstance(protocol);
      retorno = messageFactory.createMessage();
      retorno.getSOAPPart().setContent(domSource);
      return retorno;
    } catch (SOAPException e) {
      throw new SoapSecurityException(e.getMessage(), e);
    }
  }

  public static Document toDocument(String xml) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      InputStream in = new ByteArrayInputStream(xml.getBytes());
      DocumentBuilder builder = null;
      builder = factory.newDocumentBuilder();
      return builder.parse(in);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new IllegalAccessError("Problemas al crear el Document XML a partir de: " + xml); // NOPMD
    }
  }

  public static SOAPMessage toSOAPMessage(InputStream in) {
    try {
      StringWriter writer = new StringWriter();
      IOUtils.copy(in, writer, "UTF-8");
      return toSOAPMessage(writer.toString());
    } catch (Exception e) {
      throw new SoapSecurityException(e.getMessage(), e);
    }
  }

  public static Document toDocument(InputStream xml) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = null;
      builder = factory.newDocumentBuilder();
      return builder.parse(xml);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new IllegalAccessError("Problemas al ir de InputStream to Document: " + e.getMessage()); // NOPMD
    }
  }

  public static String soapMessageToString(SOAPMessage message) {
    String retorno = null;
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      message.writeTo(out);
      out.flush();
      retorno = out.toString();
      out.close();
    } catch (SOAPException | IOException e) {
      throw new IllegalAccessError("Error al hacer el pasaje a String ".concat(e.getMessage())); // NOPMD
    }
    return retorno;
  }

  public static SOAPMessage toSOAPMessage(String message) {
    return toSOAPMessage(message, SOAPConstants.SOAP_1_2_PROTOCOL);
  }

  public static SOAPMessage toSOAPMessage(String message, final String soapProtocol) {
    SOAPMessage retorno = null;
    try {
      ByteArrayInputStream is = new ByteArrayInputStream(message.getBytes("UTF-8"));
      MessageFactory messageFactory = MessageFactory.newInstance(soapProtocol);
      retorno = messageFactory.createMessage(null, is);
    } catch (SOAPException | IOException e) {
      throw new IllegalAccessError("Problemas al crear el SOAP."); // NOPMD
    }
    return retorno;
  }

  public static Document toDocument(SOAPMessage soapMSG) {
    try {
      Source source = soapMSG.getSOAPPart().getContent();
      TransformerFactory factoryTransform = TransformerFactory.newInstance();
      Transformer transform = factoryTransform.newTransformer();
      DOMResult retorno = new DOMResult();
      transform.transform(source, retorno);
      return (Document) retorno.getNode();
    } catch (Exception e) {
      throw new SecurityException(e);
    }
  }

  public static SOAPMessage updateSOAPMessage(Document doc, SOAPMessage message) {
    try {
      WSSecHeader secHeader = new WSSecHeader();
      secHeader.removeSecurityHeader(doc);
      DOMSource domSource = new DOMSource(doc);
      message.getSOAPPart().setContent(domSource);
      ByteArrayOutputStream soapXML = new ByteArrayOutputStream();
      message.writeTo(soapXML);
      return message;
    } catch (WSSecurityException | SOAPException | IOException e) {
      throw new SoapSecurityException(e.getMessage(), e);
    }
  }

  public static SOAPMessage clone(SOAPMessage message) {
    return toSOAPMessage(toDocument(message));
  }

  public static Document loadXMLFrom(String xml) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(false);
      InputStream in = new ByteArrayInputStream(xml.getBytes());
      DocumentBuilder builder = null;
      builder = factory.newDocumentBuilder();
      return builder.parse(in);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new IllegalAccessError("Problemas al crear el Document XML a partir de: " + xml); // NOPMD
    }
  }
}

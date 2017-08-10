package uy.com.faller.java.common.security.file;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import uy.com.faller.java.common.security.exception.SoapSecurityException;
import uy.com.faller.java.common.security.util.SOAPUtil;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;

public class XMLSecurity {


  private String pathKeyStore;

  private String passworKeystore;

  private String alias;


  public XMLSecurity(String pathKeyStore, String passwordKeystore, String alias) {
    this.pathKeyStore = pathKeyStore;
    this.passworKeystore = passwordKeystore;
    this.alias = alias;
  }


  public ByteArrayOutputStream signatureFileXML(URL xml) throws SoapSecurityException {
    try {
      return signatureFileXML(new FileInputStream(new File(xml.toURI())));
    } catch (FileNotFoundException | URISyntaxException e) {
      throw new SoapSecurityException(e.getMessage(), e);
    }
  }

  public ByteArrayOutputStream signatureFileXML(String xml) {
    try {
      return signatureFileXML(new ByteArrayInputStream(xml.getBytes("UTF-8")));
    } catch (UnsupportedEncodingException e) {
      throw new SoapSecurityException("Problemas con UTF-8 para el xml.", e);
    }
  }


  private ByteArrayOutputStream signatureFileXML(InputStream xml) {

    long start = System.currentTimeMillis();
    org.apache.xml.security.Init.init();
    Document doc = SOAPUtil.toDocument(xml);

    try {
      KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
      ks.load(new FileInputStream(pathKeyStore), passworKeystore.toCharArray());
      PrivateKey privateKey = (PrivateKey) ks.getKey(alias, passworKeystore.toCharArray());

      XMLSignature xmlSignature = new XMLSignature(doc, XMLSecurity.class.getResource("/uy").toURI().toString(),
          XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
      doc.getDocumentElement().appendChild(xmlSignature.getElement());

      Transforms transforms = new Transforms(doc);
      transforms.addTransform(org.apache.xml.security.transforms.Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
      xmlSignature.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
      xmlSignature.sign(privateKey);

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      docToOutputStream(doc, out);

      return out;
    } catch (Exception e) {
      throw new SoapSecurityException(e.getMessage(), e);
    }
  }

  private void docToOutputStream(Document doc, ByteArrayOutputStream out) {
    try {
      TransformerFactory factory = TransformerFactory.newInstance();
      Transformer transformer = factory.newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      transformer.transform(new DOMSource(doc), new StreamResult(out));
    } catch (TransformerFactoryConfigurationError | TransformerException e) {
      throw new SoapSecurityException(e.getMessage(), e);
    }
  }

}

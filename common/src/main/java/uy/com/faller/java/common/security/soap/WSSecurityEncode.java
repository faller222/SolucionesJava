package uy.com.faller.java.common.security.soap;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSEncryptionPart;
import org.apache.ws.security.WSSConfig;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSecEncrypt;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecSignature;
import org.w3c.dom.Document;
import uy.com.faller.java.common.security.property.ConfigProperties;
import uy.com.faller.java.common.security.property.SecurityProperties;
import uy.com.faller.java.common.security.util.SOAPUtil;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class WSSecurityEncode extends AbstractSecurity {

  private static final String MSG = "No se encuentra configurada la property {0}";

  private String aliasEncrypt;

  private String soapProtocol = SOAPConstants.SOAP_1_2_PROTOCOL;


  public WSSecurityEncode() {
    config = ConfigProperties.getInstance();
    chekInfoSetting();
    settingProperties();
  }


  public WSSecurityEncode(Properties prop, String soapProtocol) {
    if (soapProtocol != null) {
      this.soapProtocol = soapProtocol;
    }
    config = new ConfigProperties(prop);
    chekInfoSetting();
    settingProperties();
  }

  private void chekInfoSetting() {
    if (config.getProperty(SecurityProperties.PATH_KEYSTORE.getValue()) == null) {
      getErrorProperty(SecurityProperties.PATH_KEYSTORE);
    }

    if (config.getProperty(SecurityProperties.PASSWORD_KEYSTORE.getValue()) == null) {
      getErrorProperty(SecurityProperties.PASSWORD_KEYSTORE);
    }

    if (config.getProperty(SecurityProperties.ALIAS_PRIVATE_KEY.getValue()) == null) {
      getErrorProperty(SecurityProperties.ALIAS_PRIVATE_KEY);
    }
  }


  private void getErrorProperty(SecurityProperties securityProperty) {
    String msg = MessageFormat.format(MSG, securityProperty.getValue());
    throw new IllegalArgumentException(msg);
  }


  public SOAPMessage encrypt(SOAPMessage soap, String aliasEncrypt) throws SecurityException {
    this.aliasEncrypt = aliasEncrypt;
    return encode(soap, OptionWSS.ENCRYPT);
  }


  public SOAPMessage encryptSingnature(SOAPMessage soap, String aliasEncrypt) throws SecurityException {
    this.aliasEncrypt = aliasEncrypt;
    return encode(soap, OptionWSS.ENCRYPT_SIGNATURE);
  }


  public SOAPMessage signature(SOAPMessage soap) throws SecurityException {
    return encode(soap, OptionWSS.SIGNATURE);
  }


  public SOAPMessage encode(SOAPMessage soapMSG, OptionWSS optionWSS) throws SecurityException {
    SOAPMessage retorno = null;

    try {
      Crypto crypto = CryptoFactory.getInstance(prop);
      WSSConfig.init();

      Document doc = SOAPUtil.toDocument(soapMSG);
      WSSecHeader secHeader = new WSSecHeader();
      secHeader.setMustUnderstand(false);
      secHeader.insertSecurityHeader(doc);
      Document documentEncrypt = null;
      Document documentSign = null;

      documentEncrypt = settingEncryption(optionWSS, crypto, doc, secHeader);

      if (documentEncrypt == null) {
        documentEncrypt = doc;
      }

      documentSign = settingSignature(optionWSS, crypto, secHeader, documentEncrypt);

      if (documentSign == null) {
        retorno = SOAPUtil.toSOAPMessage(documentEncrypt, soapProtocol);
      } else {
        retorno = SOAPUtil.toSOAPMessage(documentSign, soapProtocol);
      }

      return retorno;
    } catch (Exception e) {
      throw new SecurityException(e);
    }
  }

  private Document settingSignature(OptionWSS optionWSS, Crypto crypto, WSSecHeader secHeader, Document documentEncrypt)
      throws WSSecurityException {
    WSSecSignature sign;
    Document retorno = null;
    if ((OptionWSS.SIGNATURE == optionWSS) || (OptionWSS.ENCRYPT_SIGNATURE == optionWSS)) {
      sign = getInfoSignature();
      List<WSEncryptionPart> listParts = getPartSign();
      if ((listParts != null) && (!listParts.isEmpty())) {
        sign.setParts(listParts);
      }
      retorno = sign.build(documentEncrypt, crypto, secHeader);
    }
    return retorno;
  }

  private Document settingEncryption(OptionWSS optionWSS, Crypto crypto, Document doc, WSSecHeader secHeader)
      throws WSSecurityException {
    WSSecEncrypt encrypt;
    Document retorno = null;
    // Encrypt
    if ((OptionWSS.ENCRYPT == optionWSS) || (OptionWSS.ENCRYPT_SIGNATURE == optionWSS)) {
      encrypt = getInfoEncrypt();
      List<WSEncryptionPart> listParts = getPartEncryption();
      if ((listParts != null) && (!listParts.isEmpty())) {
        encrypt.setParts(listParts);
      }
      retorno = encrypt.build(doc, crypto, secHeader);
    }
    return retorno;
  }

  private List<WSEncryptionPart> getPartEncryption() {
    List<WSEncryptionPart> listParts = null;

    listParts = new ArrayList<WSEncryptionPart>();
    if (listPartEncrypt != null) {
      for (QName qNamePart : listPartEncrypt) {
        WSEncryptionPart onePart = new WSEncryptionPart(qNamePart.getLocalPart(), qNamePart.getNamespaceURI(), "");
        listParts.add(onePart);
      }
    }
    return listParts;
  }

  private List<WSEncryptionPart> getPartSign() {
    List<WSEncryptionPart> listParts = null;

    if (listPartSign != null) {
      listParts = new ArrayList<WSEncryptionPart>();
      for (QName qNamePart : listPartSign) {
        WSEncryptionPart onePart = new WSEncryptionPart(qNamePart.getLocalPart(), qNamePart.getNamespaceURI(), "");
        listParts.add(onePart);
      }
    }
    return listParts;
  }

  private WSSecSignature getInfoSignature() {
    WSSecSignature retorno = new WSSecSignature();
    retorno.setKeyIdentifierType(WSConstants.X509_KEY_IDENTIFIER);
    retorno.setUserInfo(this.aliasKeyPrivate, this.passwordKeyStoreKeyPrivate);
    retorno.setSignatureAlgorithm(WSConstants.RSA_SHA1);
    retorno.setSigCanonicalization(WSConstants.C14N_EXCL_OMIT_COMMENTS);
    retorno.setDigestAlgo(WSConstants.SHA1);
    return retorno;
  }

  private WSSecEncrypt getInfoEncrypt() {
    WSSecEncrypt retorno = new WSSecEncrypt();
    retorno.setUserInfo(aliasEncrypt);
        /* Setting info Encrypt */
    retorno.setKeyIdentifierType(WSConstants.BST_DIRECT_REFERENCE);
    retorno.setSymmetricEncAlgorithm(WSConstants.AES_128);
    retorno.setKeyEnc(WSConstants.KEYTRANSPORT_RSA15);
    return retorno;
  }
}

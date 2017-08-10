package uy.com.faller.java.common.security.soap;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.WSSConfig;
import org.apache.ws.security.WSSecurityEngine;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.util.WSSecurityUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uy.com.faller.java.common.security.property.ConfigProperties;
import uy.com.faller.java.common.security.property.SecurityProperties;
import uy.com.faller.java.common.security.util.SOAPUtil;
import uy.com.faller.java.common.security.util.Util;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;


public class WSSecurityDecode extends AbstractSecurity implements CallbackHandler {

  private static final String MSG = "No se encuentra configurada la property {0}";


  public WSSecurityDecode() {
    config = ConfigProperties.getInstance();
    chekInfoSetting();
    settingProperties();
  }


  public WSSecurityDecode(Properties prop) {
    config = new ConfigProperties(prop);
    chekInfoSetting();
    settingProperties();
  }


  private void chekInfoSetting() {
    if (config.getProperty(SecurityProperties.PATH_KEYSTORE.getValue()) == null) {
      String msg = MessageFormat.format(MSG, SecurityProperties.PATH_KEYSTORE.getValue());
      throw new IllegalArgumentException(msg);
    }

    if (config.getProperty(SecurityProperties.PASSWORD_KEYSTORE.getValue()) == null) {
      String msg = MessageFormat.format(MSG, SecurityProperties.PASSWORD_KEYSTORE.getValue());
      throw new IllegalArgumentException(msg);
    }

    if (config.getProperty(SecurityProperties.ALIAS_PRIVATE_KEY.getValue()) == null) {
      String msg = MessageFormat.format(MSG, SecurityProperties.ALIAS_PRIVATE_KEY.getValue());
      throw new IllegalArgumentException(msg);
    }

  }

  public SOAPMessage verifiedSignatureAndDecode(SOAPMessage soap) throws SecurityException {
    WSSConfig wssConfig = new WSSConfig();
    wssConfig.setEnableSignatureConfirmation(true);
    return decode(soap, OptionWSS.ENCRYPT_SIGNATURE);
  }


  public SOAPMessage decode(SOAPMessage soap) throws SecurityException {
    return decode(soap, OptionWSS.ENCRYPT);
  }

  public SOAPMessage verifiedSignature(SOAPMessage soap) throws SecurityException {
    return decode(soap, OptionWSS.SIGNATURE);
  }


  private SOAPMessage decode(SOAPMessage msg, OptionWSS op) throws SecurityException {

    try {
      SOAPHeader soapHeader = msg.getSOAPHeader();
      QName secQName = new QName(WSConstants.WSSE_NS, "Security");
      Element secHeaderElement = (soapHeader == null) ? null : Util.findElement(soapHeader, secQName);

      if (secHeaderElement == null) {
        throw new WSSecurityException(WSSecurityException.INVALID_SECURITY);
      }

      Crypto crypto = CryptoFactory.getInstance(prop);
      WSSConfig.init();
      Document doc = SOAPUtil.toDocument(msg);

      List<WSSecurityEngineResult> engineResult = verify(doc, crypto, false, op);
      if (engineResult == null) {
        throw new WSSecurityException(WSSecurityException.FAILED_ENCRYPTION);
      }

      return SOAPUtil.updateSOAPMessage(doc, msg);

    } catch (WSSecurityException e) {
      throw new SecurityException(e.getMessage(), e);
    } catch (SOAPException e) {
      throw new SecurityException(e.getMessage(), e);
    }
  }

  private List<WSSecurityEngineResult> verify(Document doc, Crypto crypto, boolean revocationEnabled, OptionWSS op)
      throws WSSecurityException {
    List<WSSecurityEngineResult> results;
    try {


      WSSecurityEngine secEngine = new WSSecurityEngine();
      RequestData reqData = new RequestData();

      if ((op == OptionWSS.ENCRYPT_SIGNATURE) || (op == OptionWSS.SIGNATURE)) {
        reqData.setSigCrypto(crypto);
      }

      if ((op == OptionWSS.ENCRYPT_SIGNATURE) || (op == OptionWSS.ENCRYPT)) {
        reqData.setDecCrypto(crypto);
      }

      reqData.setEnableRevocation(revocationEnabled);
      reqData.setUseSingleCert(false);
      reqData.setCallbackHandler(this);

      Element securityHeader = WSSecurityUtil.getSecurityHeader(doc, null);

      results = secEngine.processSecurityHeader(securityHeader, reqData);


      return results;
    } catch (Exception e) {
      throw new WSSecurityException(e.getMessage(), e);
    }
  }

  public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
    String claveKeyStore;
    for (Callback callback : callbacks) {
      if (callback instanceof WSPasswordCallback) {
        WSPasswordCallback wsPasswordCall = (WSPasswordCallback) callback;
        try {
          claveKeyStore = passwordKeyStoreKeyPrivate;
          if (wsPasswordCall.getIdentifier() != null) {
            wsPasswordCall.setPassword(claveKeyStore);
          }
        } catch (Exception e) {
          throw new UnsupportedCallbackException(wsPasswordCall,
              "Failure recovering the key in the properties"); // NOPMD
        }
      }
    }
  }
}

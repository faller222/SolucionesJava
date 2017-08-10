package uy.com.faller.java.common.security.soap;


import uy.com.faller.java.common.security.property.ConfigProperties;
import uy.com.faller.java.common.security.property.SecurityProperties;

import javax.xml.namespace.QName;
import java.util.Properties;


public abstract class AbstractSecurity {


  protected String aliasKeyPrivate;

  protected String passwordKeyStoreKeyPrivate;

  protected QName[] listPartEncrypt;

  protected QName[] listPartSign;

  protected Properties prop;

  protected ConfigProperties config;

  /**
   * Constructor
   */
  public AbstractSecurity() {

  }

  protected void settingProperties() {
    String separator = System.getProperties().getProperty("file.separator");

    String pathCacertDefault =
        System.getProperties().getProperty("java.home").concat(separator).concat("lib").concat(separator)
            .concat("security").concat(separator).concat("cacerts");

    prop = new Properties();

    String passwordKeyStore = config.getProperty(SecurityProperties.PASSWORD_KEYSTORE.getValue(), "changeit");
    this.passwordKeyStoreKeyPrivate = passwordKeyStore;
    String pathKeyStore = config.getProperty(SecurityProperties.PATH_KEYSTORE.getValue(), "keys/keystore.jks");
    String alias = config.getProperty(SecurityProperties.ALIAS_PRIVATE_KEY.getValue(), "faller");
    this.aliasKeyPrivate = alias;
    String pathCacert = config.getProperty(SecurityProperties.PATH_TRUSTORE.getValue(), pathCacertDefault);
    String passwordTrustore = config.getProperty(SecurityProperties.PASSWORD_TRUSTORE.getValue(), "changeit");

    /** Seteo variables por defecto para el encriptado */
    prop.setProperty("org.apache.ws.security.crypto.provider", "org.apache.ws.security.components.crypto.Merlin");
    prop.setProperty("org.apache.ws.security.crypto.merlin.keystore.type", "jks");
    prop.setProperty("org.apache.ws.security.crypto.merlin.truststore.type", "jks");
    prop.setProperty("org.apache.ws.security.crypto.merlin.load.cacerts", "true");

    prop.setProperty("org.apache.ws.security.crypto.merlin.keystore.file", pathKeyStore);
    prop.setProperty("org.apache.ws.security.crypto.merlin.keystore.password", this.passwordKeyStoreKeyPrivate);
    prop.setProperty("org.apache.ws.security.crypto.merlin.keystore.alias", this.aliasKeyPrivate);
    prop.setProperty("org.apache.ws.security.crypto.merlin.truststore.file", pathCacert);
    prop.setProperty("org.apache.ws.security.crypto.merlin.truststore.password", passwordTrustore);

  }


  public QName[] getListPartEncrypt() {
    return listPartEncrypt;
  }


  public void setListPartEncrypt(QName[] listPartEncrypt) {
    this.listPartEncrypt = listPartEncrypt == null ? listPartEncrypt : listPartEncrypt.clone();
  }


  public QName[] getListPartSign() {
    return listPartSign;
  }

  public void setListPartSign(QName[] listPartSign) {
    this.listPartSign = listPartSign == null ? listPartSign : listPartSign.clone();
  }

}

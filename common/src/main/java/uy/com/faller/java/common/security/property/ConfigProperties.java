package uy.com.faller.java.common.security.property;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ConfigProperties {

  private static ConfigProperties instance;

  private Properties properties = new Properties();


  private ConfigProperties() {
    try {
      InputStream in = ConfigProperties.class.getResourceAsStream("/config.properties");
      try {
        if (in == null) {
          this.properties = System.getProperties();
        } else {
          properties.load(in);
        }
      } finally {
        if (in != null) {
          in.close();
        }
      }
    } catch (IOException e) {
      properties = System.getProperties();
    }
  }

  public ConfigProperties(Properties prop) {
    this.properties = prop;
  }

  public static ConfigProperties getInstance() {
    if (instance == null) {
      instance = new ConfigProperties();
    }
    return instance;
  }

  public String getProperty(String key, String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  public String getProperty(String key) {
    return properties.getProperty(key);
  }

}

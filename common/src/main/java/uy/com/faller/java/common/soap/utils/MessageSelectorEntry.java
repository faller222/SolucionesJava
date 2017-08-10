package uy.com.faller.java.common.soap.utils;

/**
 * Entrada selector que reune clave valor para los selectores de los MDB
 */
public final class MessageSelectorEntry {

  private String key;

  private String value;

  public MessageSelectorEntry(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public MessageSelectorEntry() {
    value = "";
    key = "";
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}

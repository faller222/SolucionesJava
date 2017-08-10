package uy.com.faller.java.common.soap.addressing;

import java.io.Serializable;

public class AddressingValue implements Serializable {

  private static final long serialVersionUID = 2276158946991822795L;

  private TagsAddressing tag;

  private String value;

  /**
   * Constructor
   */
  public AddressingValue() {
  }

  public AddressingValue(TagsAddressing tag, String value) {
    super();
    this.tag = tag;
    this.value = value;
  }

  public TagsAddressing getTag() {
    return tag;
  }

  public void setTag(TagsAddressing tag) {
    this.tag = tag;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

}

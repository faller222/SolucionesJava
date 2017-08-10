package uy.com.faller.java.common.security.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;


public final class Util {

  private Util() {
  }


  public static Element findElement(Element root, QName name) {
    if (matchNode(root, name)) {
      return root;
    }
    for (Node child = root.getFirstChild(); child != null; child = child.getNextSibling()) {
      if (child.getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }
      Node possibleMatch = findElement((Element) child, name);
      if (possibleMatch != null) {
        return (Element) possibleMatch;
      }
    }
    return null;
  }

  public static boolean equalStrings(String value1, String value2) {
    if ((value1 == null) && (value2 == null)) {
      return true;
    }
    return ((value1 != null) && (value1.equals(value2)));
  }

  public static boolean matchNode(Node node, QName name) {
    return matchNode(node, name, false);
  }


  public static boolean matchNode(Node node, QName name, boolean local) {
    return equalStrings(node.getLocalName(), name.getLocalPart()) && (local || equalStrings(node.getNamespaceURI(),
        name.getNamespaceURI()));
  }

}

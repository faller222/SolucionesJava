package uy.com.faller.java.common.xml;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import java.util.Map;


public class NameSpace implements NamespaceContext {

    Map<String, String> nameSpaces;

    /**
     * Constructor
     */
    public NameSpace(Map<String, String> nameSpaces) {
        this.nameSpaces = nameSpaces;
    }

    @Override
    public String getNamespaceURI(String prefix) {

        if ((nameSpaces != null) &&
           (nameSpaces.containsKey(prefix))) {
            return nameSpaces.get(prefix);
        }

        if ("xml".equals(prefix)) {
            return XMLConstants.XML_NS_URI;
        }


        return XMLConstants.NULL_NS_URI;
    }


    @Override
    public String getPrefix(String namespaceURI) {
        return null;
    }

    @Override
    public Iterator<?> getPrefixes(String namespaceURI) {
        return null;
    }

}

package uy.com.faller.java.common.soap.addressing;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import uy.com.faller.java.common.xml.XPathExecute;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ExtractInfoHead {

    private ExtractInfoHead() {

    }

    public static Map<String, String> extractInfoWSA(String textMessage)  {
        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage messageSoap = messageFactory.createMessage(null, new ByteArrayInputStream(textMessage.getBytes("UTF-8")));
            return extractInfoWSA(messageSoap);
        } catch (Exception ex) {
            throw new AddressingException(ex);
        }
    }

    public static Map<String, String> extractInfoWSA(SOAPMessage messageSoap)  {
        Map<String, String> retorno = null;
        try {

            if (messageSoap.getSOAPHeader() == null) {
                return retorno;
            }

            QName qNameAddressing = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "wsa");
            List<Node> listaNodos = getAllNodeDeNS(messageSoap.getSOAPHeader(), qNameAddressing);

            qNameAddressing = new QName("http://www.w3.org/2005/08/addressing", "wsa");
            listaNodos.addAll(getAllNodeDeNS(messageSoap.getSOAPHeader(), qNameAddressing));
            retorno = new HashMap<>(10);

            for (Node node : listaNodos) {
                retorno.put(node.getLocalName(), node.getTextContent().trim().replace("[\\\t|\\\n|\\\r]", ""));
            }

        } catch (Exception e) {
            throw new AddressingException(e);
        }
        return retorno;
    }

    private static List<Node> getAllNodeDeNS(Element root, QName name) {
        List<Node> list = new ArrayList<Node>();
        if (root == null)
            return list;

        if (nodeIsNS(root, name))
            list.add(root);

        for (Node child = root.getFirstChild(); child != null; child = child
                .getNextSibling()) {
            if ((child.getNodeType() == Node.ELEMENT_NODE) &&
                (nodeIsNS(child, name))){
                    list.add(child);
            }
        }
        return list;
    }

    private static boolean nodeIsNS(Node node, QName name) {
        String object1 = node.getNamespaceURI();
        String object2 = name.getNamespaceURI();

        if (object1 == null && object2 == null) {
            return true;
        }

        return (object1 != null && object1.equals(object2));
    }


    public static String extractWSAValue(String textMessage, TagsAddressing tag)  {
        String retorno = "";
        try {

            MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            SOAPMessage messageSoap = messageFactory.createMessage(null, new ByteArrayInputStream(textMessage.getBytes("UTF-8")));

            if (messageSoap.getSOAPHeader() == null) {
                return retorno;
            }

            InputStream inputStream = getInputStream(messageSoap.getSOAPHeader());


            Map<String, String> nameSpaces = new HashMap<>(3);
            nameSpaces.put("env", "http://www.w3.org/2003/05/soap-envelope");
            nameSpaces.put("wsa", "http://www.w3.org/2005/08/addressing");

            String xPath;
            if (tag.equals(TagsAddressing.TO) ) {
                xPath ="//env:Header/wsa:To/text()";
            } else if (tag.equals(TagsAddressing.ACTION)) {
                xPath = "//env:Header/wsa:Action/text()";
            } else if (tag.equals(TagsAddressing.FROM)) {
                xPath = "//env:Header/wsa:From/wsa:Address/text()";
            }else if (tag.equals(TagsAddressing.MESSAGE_ID)) {
                xPath = "//env:Header/wsa:MessageID/text()";
            } else if (tag.equals(TagsAddressing.REPLY_TO)) {
                xPath = "//env:Header/wsa:ReplyTo/wsa:Address/text()";
            } else if (tag.equals(TagsAddressing.FAULT_TO)) {
                xPath = "//env:Header/wsa:FaultTo/wsa:Address/text()";
            } else if (tag.equals(TagsAddressing.RELATES_TO)) {
                xPath = "//env:Header/wsa:RelatesTo/text()";
            } else {
                throw new AddressingException("Error al obtener valor del WSA");
            }

            retorno = XPathExecute.getSingleValue(xPath, inputStream, nameSpaces);

        } catch (Exception e) {
            throw new AddressingException(e);
        }
        return retorno;
    }


    private static InputStream getInputStream(SOAPHeader soapHeader) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(soapHeader), new StreamResult(out));
        return new ByteArrayInputStream(out.toByteArray());
    }
}

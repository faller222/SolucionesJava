package uy.com.faller.java.common.soap.addressing;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public final class Addressing {

    public static final String WSA_SOAP_NS = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
    public static final String WSA_SOAP_NS_2005 = "http://www.w3.org/2005/08/addressing";

    public static final String WSA_PREFIX = "wsa";


    private Addressing() {

    }


    public static void validarAddressing(SOAPHeader soapHead, TagsAddressing... tagsOmitir) throws AddressingFault {

        QName qNameAddressing = new QName(Addressing.WSA_SOAP_NS_2005, "wsa");
        List<Node> listaNodos = getAllNodeDeNS(soapHead, qNameAddressing);
        Map<String, String> tagsEnSoap = new HashMap<String, String>(10);

        for (Node node : listaNodos) {
            tagsEnSoap.put(node.getLocalName(), node.getLocalName());
        }

        Map<String, String> tagsValidar = new HashMap<String, String>(10);

        Arrays.sort(tagsOmitir);

        for (TagsAddressing value : TagsAddressing.values()) {
            int resultado = Arrays.binarySearch(tagsOmitir, value);
            boolean retorno = (resultado > -1);
            if (!retorno) {
                tagsValidar.put(value.getValue(), value.getValue());
            }
        }

        for (String value : tagsValidar.keySet()) {
            if (!tagsEnSoap.containsKey(value)) {
                throw generateMsgInfoHeaderRequieredFault();
            }
        }

    }

    public static void addAddressing(SOAPMessage soapMessage, AddressingValue... valuesList) {
        addAddressing(soapMessage, null, valuesList);
    }

    public static void addAddressing(SOAPMessage soapMessage, String soapProtocol, AddressingValue... valuesList) {
        try {
            if (soapProtocol == null) {
                soapProtocol = SOAPConstants.SOAP_1_2_PROTOCOL;
            }

            SOAPFactory soapFactory = SOAPFactory.newInstance(soapProtocol);
            SOAPHeader soapHeader = soapMessage.getSOAPHeader();
            if (soapHeader == null) {
                SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
                soapHeader = envelope.addHeader();
            }

            soapHeader.addNamespaceDeclaration(WSA_PREFIX, WSA_SOAP_NS_2005);

            for (AddressingValue addressingValue : valuesList) {

                if ((addressingValue.getTag() == TagsAddressing.FAULT_TO) || (addressingValue.getTag()
                    == TagsAddressing.FROM) || (addressingValue.getTag() == TagsAddressing.REPLY_TO)) {
                    setAddress(soapHeader, soapFactory, addressingValue);
                } else {
                    SOAPElement element = soapFactory
                        .createElement(addressingValue.getTag().getValue(), "wsa", soapHeader.getNamespaceURI("wsa"));
                    element.setTextContent(addressingValue.getValue());
                    soapHeader.addChildElement(element);
                }
            }

            soapMessage.saveChanges();
        } catch (SOAPException e) {
            throw new AddressingException(e);
        }
    }

    private static void setAddress(SOAPHeader soapHeader, SOAPFactory soapFactory, AddressingValue value) throws SOAPException {
        SOAPElement element = soapFactory.createElement(value.getTag().getValue(), "wsa", soapHeader.getNamespaceURI("wsa"));

        SOAPElement childElement = soapFactory.createElement("Address", WSA_PREFIX, soapHeader.getNamespaceURI(WSA_PREFIX));
        childElement.setTextContent(value.getValue());
        element.addChildElement(childElement);

        soapHeader.addChildElement(element);

    }


    private static List<Node> getAllNodeDeNS(Element root, QName name) {
        List<Node> list = new ArrayList<Node>();
        if (root == null)
            return list;

        if (nodeIsNS(root, name))
            list.add(root);

        for (Node child = root.getFirstChild(); child != null; child = child.getNextSibling()) {
            if ((child.getNodeType() == Node.ELEMENT_NODE) &&
                    ((nodeIsNS(child, name)))){
                    list.add(child);
            }
        }

        return list;
    }


    private static boolean nodeIsNS(Node node, QName name) {
        String object1 = node.getNamespaceURI();
        String object2 = name.getNamespaceURI();

        if (object1 == null && object2 == null)
            return true;

        return (object1 != null && object1.equals(object2));
    }

    public static AddressingFault generateMsgInfoHeaderRequieredFault() {

        AddressingFault fault = new AddressingFault();
        fault.setFaultCode("MessageInformationHeaderRequired");
        fault.setFaultString("A required message information header To, From, MessageID, or Action, is not present.");

        return fault;
    }

    public static AddressingFault generateActionNotSupportedFault(String wsaAction) {
        AddressingFault fault = new AddressingFault();
        fault.setFaultCode("ActionNotSupported");
        fault.setFaultString("The action \"" + wsaAction + "\" cannot be processed at the receiver.");

        return fault;
    }

    public static AddressingFault generateEndpointUnavailableFault() {

        AddressingFault fault = new AddressingFault();
        fault.setFaultCode("EndpointUnavailable");
        fault.setFaultString("The endpoint is unable to process the message at this time.");

        return fault;
    }

}

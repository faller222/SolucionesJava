package uy.com.faller.java.common.soap.utils;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import uy.com.faller.java.common.soap.SoapCommonException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

public class RequestSOAP<T> {


    private static final String WS = "ws";

    private String wsURL;

    public RequestSOAP(String wsURL) {
        this.wsURL = wsURL;
    }


    public static String getMessage(SOAPMessage smsg) throws IOException, SOAPException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        smsg.writeTo(out);
        String result = out.toString();
        out.close();
        return result.replaceAll("\\>\\<", ">\n<");
    }

    public static String getBody(SOAPBody soapBody) throws TransformerException {
        DOMSource source = new DOMSource(soapBody);
        StringWriter stringResult = new StringWriter();
        TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
        return stringResult.toString();
    }

    public String dtoToSOAP(T dto, String nombreOperacion) throws Exception {
        return dtoToSOAP(dto, nombreOperacion, null);
    }

    public String dtoToSOAP(T dto, String nombreOperacion, Map<String, String> params) throws Exception {
        SOAPMessage msjSOAP = construirMensajeSOAP(dto, nombreOperacion, params);
        String result = getMessage(msjSOAP);
        return result;
    }

    public SOAPMessage construirMensajeSOAP(T dto, String nombreOperacion) throws Exception {
        return construirMensajeSOAP(dto, nombreOperacion, null);
    }

    public SOAPMessage construirMensajeSOAP(T dto, String nombreOperacion, Map<String, String> params)  {

        SOAPMessage smsg = null;
        SOAPBody body;
        SOAPEnvelope envelope;
        SOAPPart soapPart;
        SOAPElement elementBody;
        SOAPElement elementChild;

        String xml = null;
        Element elementXML = null;

		/* Este crea el elemento para agregar el xml del dto */
        try {
            xml = dtoToXML(dto);
            elementXML = getElement(xml);
        } catch (Exception e) {
            throw new SoapCommonException("Problemas al convertir el DTO a Element", e);
        }

        try {
            smsg = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL)
                    .createMessage();

            soapPart = smsg.getSOAPPart();
            envelope = soapPart.getEnvelope();

            envelope.addNamespaceDeclaration(WS, wsURL);

            body = envelope.getBody();
            elementBody = body.addChildElement(nombreOperacion, WS);

			/* Este crea el elemento para agregar el xml del dto */
            elementChild = SOAPFactory.newInstance().createElement(elementXML);

            elementBody.addChildElement(elementChild);

            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    elementChild = SOAPFactory.newInstance().createElement(entry.getKey());
                    elementChild = elementChild.addTextNode(entry.getValue());
                    elementBody.addChildElement(elementChild);
                }
            }

            smsg.saveChanges();

        } catch (Exception e) {
            throw new SoapCommonException("Problemas al crear el SOAPMessage", e);
        }
        return smsg;
    }

    private Element getElement(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            factory.setNamespaceAware(true);
            InputStream in = new ByteArrayInputStream(xml.getBytes());
            DocumentBuilder builder = null;
            builder = factory.newDocumentBuilder();
            return builder.parse(in).getDocumentElement();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalAccessError("Problemas al crear el Document XML a partir de: " + xml); // NOPMD
        }
    }

    private String dtoToXML(T dto) throws JAXBException {
        Marshaller marshaller;
        String xml = null;
        final StringWriter xmlWriter = new StringWriter();


        marshaller = JAXBContext.newInstance(dto.getClass()).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

        marshaller.marshal(dto, xmlWriter);

        xml = xmlWriter.toString();

        return xml;
    }

}
package uy.com.faller.java.common.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;


public final class XMLUtil {

    /**
     * Metodos estaticos
     */
    private XMLUtil() {
    }

    /**
     * Devuelve el valor del atributo con nombre <<tagElementName>> en el documento xml <<xmlDocument>>
     *
     * @param tagElementName - nombre del atributo que se quiere obtener
     * @param xmlDocument    - documento xml en el cual se busca el atributo
     * @return - el texto valor del atributo o null en caso que no se encuentre
     * @throws XMLException
     */
    public static String getFirstElementTextContent(String tagElementName, Document xmlDocument) throws XMLException {

        String textContent;
        NodeList nodeList;
        Node nNode;
        Element eElement;
        nodeList = xmlDocument.getElementsByTagName(tagElementName);

        if (nodeList.getLength() > 0) {
            nNode = nodeList.item(0);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                eElement = (Element) nNode;
                textContent = eElement.getTextContent();
            } else {
                throw new XMLException("La estructura xml relacionada al elemento '" + tagElementName + "' es incorrecta para el documento " + xmlDocument);
            }
        } else {
            return null;
        }
        return textContent.trim();
    }

    /**
     * Devuelve el valor del atributo con nombre <<tagElementName>> en el documento xml <<xmlDocument>>
     *
     * @param s
     * @param namespace
     * @param xmlMessage
     * @return
     * @throws XMLException
     */
    public static  String getFirstElementTextContent(String s, String namespace,Document xmlMessage) throws XMLException {
        NodeList nodeList = xmlMessage.getElementsByTagNameNS(namespace,s);
        if(nodeList.getLength() > 0) {
            Node nNode = nodeList.item(0);
            if(nNode.getNodeType() == 1) {
                Element eElement = (Element)nNode;
                String textContent = eElement.getTextContent();
                return textContent.trim();
            } else {
                throw new XMLException("La estructura xml relacionada al elemento \'" + s + "\' es incorrecta para el documento " + xmlMessage);
            }
        } else {
            return null;
        }
    }


    public static Collection<String> getAllElementsTextContent(String tagElementName, Document xmlDocument) {
        Collection<String> out = new ArrayList<>();
        NodeList nodeList = xmlDocument.getElementsByTagName(tagElementName);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node nNode = nodeList.item(i);
            out.add(nNode.getTextContent());
        }
        return out;
    }

    public static void setFirstElementTextContent(String tagElementName, String textContent, Document xmlDocument) throws XMLException {

        NodeList nodeList;
        Node nNode;
        Element eElement;
        nodeList = xmlDocument.getElementsByTagName(tagElementName);

        if (nodeList.getLength() > 0) {
            nNode = nodeList.item(0);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                eElement = (Element) nNode;
                eElement.setTextContent(textContent);
            } else {
                throw new XMLException("Error al intentar leer el documento XML");
            }
        } else {
            Element rootElement = xmlDocument.getDocumentElement();
            if (rootElement == null) {
                throw new XMLException("Error al intentar leer el documento XML");
            }
            eElement = xmlDocument.createElement(tagElementName);
            eElement.setTextContent(textContent);
            rootElement.appendChild(eElement);
        }
    }

    /**
     * Transforma el documento <<doc>> a XML y lo retorna como String
     *
     * @param doc - documento a transformar a XML
     * @return - un String con el doc transformado a XML
     * @throws javax.xml.transform.TransformerException
     */
    public static String getXmlDocumentStringSinIndentar(Document doc) throws TransformerException {
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.METHOD, "xml");
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(doc.getDocumentElement());

        trans.transform(source, result);

        return sw.toString();
    }

    /**
     * Transforma el documento <<doc>> a XML identado correctamente y lo retorna como String
     *
     * @param doc - documento a transformar a XML
     * @return - un String con el doc transformado a XML identado
     * @throws javax.xml.transform.TransformerException
     */
    public static String getXmlDocumentStringIndentado(Document doc) throws TransformerException {
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.METHOD, "xml");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(2));
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(doc.getDocumentElement());

        trans.transform(source, result);

        return sw.toString();
    }

    /**
     * Devuelve el contenido de un Node como un String
     *
     * @param soapElement
     * @return
     * @throws Exception
     */
    public static  String writeBody(Node soapElement) throws TransformerException {
        DOMSource source = new DOMSource(soapElement);
        StringWriter stringResult = new StringWriter();
        TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
        return stringResult.toString();
    }

    /**
     * Devuelve el objeto que corresponde al xml pasado
     *
     * @param c   el tipo de objeto a devolver
     * @param xml el xml a transformar en objeto
     * @return el objeto que corresponde al xml pasado
     * @throws JAXBException
     */
    public static  Object getObjectWithXML(Class<?> c, String xml) throws JAXBException {
        Unmarshaller unmarsh = JAXBContext.newInstance(c).createUnmarshaller();
        return  unmarsh.unmarshal(new StringReader(xml.trim()));
    }
}

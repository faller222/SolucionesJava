package uy.com.faller.java.common.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utilidad para ejecutar expresiones XPath sobre documentos XML
 */
public final class XPathExecute {

    private static final XPathFactory FACTORY = XPathFactory.newInstance();

    private static XPath xpath = null;

    static {
        xpath = FACTORY.newXPath();
    }

    /**
     * Constructor privado para evitar que se construyan instancias de esta clase utilitaria.
     */
    private XPathExecute() {
    }

    public static String getSingleValue(final String xPath, final File input, final Map<String, String> nameSpaces)
        throws XPathException {
        try {
            return getSingleValue(xPath, new FileInputStream(input), nameSpaces);
        } catch (FileNotFoundException e) {
            throw new XPathException("Error al ejecutar la expresión XPath", e);
        }
    }


    public static String getSingleValue(final String xPath, final InputStream input,
        final Map<String, String> nameSpaces) throws XPathException {
        return getSingleValue(xPath, new InputSource(input), nameSpaces);
    }


    public static String getSingleFN(final String xPath, final InputSource input, final Map<String, String> nameSpaces)
        throws XPathException {

        Object result = null;
        try {
            final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            final DocumentBuilder builder = domFactory.newDocumentBuilder();
            final Document doc = builder.parse(input);

            xpath.setNamespaceContext(new NameSpace(nameSpaces));
            final XPathExpression expr = xpath.compile(xPath);

            result = expr.evaluate(doc, XPathConstants.STRING);

        } catch (ParserConfigurationException | IOException | SAXException | XPathExpressionException exception) {
            throw new XPathException("Error al ejecutar la expresión XPath", exception);

        }
        return result == null ? null : result.toString();
    }

    public static String getSingleValue(final String xPath, final InputSource input,
        final Map<String, String> nameSpaces) throws XPathException {

        String resultado = "";
        try {
            final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);

            final DocumentBuilder builder = domFactory.newDocumentBuilder();

            final Document doc = builder.parse(input);

            final NodeList nodes = getNodeList(xPath, doc, nameSpaces);

            if (nodes.getLength() > 1) {
                throw new XPathException(
                    "Se esperaba un solo nodo como resultado de la evaluación de la expresión: " + xPath
                        + " sobre el documento: " + doc.toString());
            } else {
                resultado = nodes.item(0) == null ? null : nodes.item(0).getNodeValue();
            }

        } catch (ParserConfigurationException | IOException | SAXException | XPathExpressionException exception) {
            throw new XPathException("Error al ejecutar la expresión XPath: " + xPath, exception);
        }

        return resultado;
    }

    public static String getSingleValue(final String xPath, final Document doc, final Map<String, String> nameSpaces)
        throws XPathException {

        String resultado = "";
        try {
            final NodeList nodes = getNodeList(xPath, doc, nameSpaces);

            if (nodes.getLength() == 0) {
                return null;
            }

            if (nodes.getLength() > 1) {
                throw new XPathException(
                    "Se esperaba un solo nodo como resultado de la evaluación de la expresión: " + xPath
                        + " sobre el documento: " + doc.toString(), null);
            } else {
                resultado = nodes.item(0).getNodeValue();
            }


        } catch (XPathExpressionException exception) {
            throw new XPathException("Error al ejecutar la expresión XPath: " + xPath, exception);
        }
        return resultado;
    }

    public static List<String> getValues(final String xPath, final Document doc, final Map<String, String> nameSpaces)
        throws XPathException {

        final List<String> resultado = new ArrayList<>();
        try {
            final NodeList nodes = getNodeList(xPath, doc, nameSpaces);
            for (int i = 0; i < nodes.getLength(); i++) {
                resultado.add(nodes.item(i).getNodeValue());
            }

        } catch (XPathExpressionException exception) {
            throw new XPathException("Error al ejecutar la expresión XPath: " + xPath, exception);
        }

        return resultado;
    }

    public static Integer getCantValues(final String xPath, final Document doc, final Map<String, String> nameSpaces)
        throws XPathException {

        try {
            final NodeList nodes = getNodeList(xPath, doc, nameSpaces);
            return nodes.getLength();
        } catch (XPathExpressionException exception) {
            throw new XPathException("Error al ejecutar la expresión XPath: " + xPath, exception);

        }
    }

    public static NodeList getNodeList(final String exp, final Document doc, final Map<String, String> nameSpaces)
        throws XPathExpressionException {

        xpath.setNamespaceContext(new NameSpace(nameSpaces));
        final XPathExpression expr = xpath.compile(exp);
        final Object result = expr.evaluate(doc, XPathConstants.NODESET);
        return (NodeList) result;
    }

    public static String nodeToString(final Node node) throws TransformerException {

        final StringWriter buf = new StringWriter();
        final Transformer xform = TransformerFactory.newInstance().newTransformer();
        xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        xform.transform(new DOMSource(node), new StreamResult(buf));
        return (buf.toString());
    }
}

package uy.com.faller.java.common.soap.utils;


import uy.com.faller.java.common.security.soap.WSSecurityEncode;
import uy.com.faller.java.common.soap.SoapCommonException;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.text.MessageFormat;
import java.util.Properties;


public class InvocadorSOAPWS {

    private static int connectTimeout = 20000; // 20 sec

    private static int readTimeout = 60000; // 1 min


    /**
     * Descripcion: Metodo para realizar la invoacion a WSDL<p>
     *
     * @param endpoint
     * @param soapAction
     * @param soapMessage
     * @return
     * @throws SoapCommonException <p>
     */
    public static SOAPMessage invocarWS(String endpoint, String soapAction, SOAPMessage soapMessage)  {
        SOAPMessage soapResultado = null;


        try {
            SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = factory.createConnection();

            URL endpointSettingTimeOut = settingTimeOut(endpoint);

            setSOAPAction(soapAction, soapMessage);

            settingSSL(endpoint);

            // Envio SOAP message.
            soapResultado = soapConnection.call(soapMessage, endpointSettingTimeOut);

            return soapResultado;
        } catch (UnsupportedOperationException | SOAPException | MalformedURLException e) {
            throw new SoapCommonException("Problemas al realizar la invoacion a: " + endpoint, e);
        }
    }

    public SOAPMessage invocarWS(String endpoint, String soapAction, SOAPMessage soapMessage, String soapProtocol, String aliasEncrypt, Properties prop) {
        SOAPMessage soapEnc = null;
        try {

            // Encripto SOAP message
            WSSecurityEncode ws = new WSSecurityEncode(prop, soapProtocol);

            soapEnc = ws.encryptSingnature(soapMessage, aliasEncrypt);
            return invocarWS(endpoint, soapAction, soapEnc);
        } catch (SecurityException e) {
            throw new SoapCommonException(e.getMessage(), e);
        }
    }


    /**
     * Descripcion: realiza el setteo del trustore donde se encuentra el certificado del SSL <p>
     *
     * @param endpoint <p>
     */
    private static void settingSSL(String endpoint) {
        if (endpoint.startsWith("https")) {
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            String pathCacerts = System.getProperties().getProperty("java.home").concat("/lib/security/cacerts");
            pathCacerts = "C:\\Program Files\\Java\\jdk1.8.0_144\\jre\\lib\\security\\cacerts";

            System.setProperty("javax.net.ssl.trustStore", pathCacerts);
            System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        }
    }

    /**
     * Descripcion: realiza el setting al endpoint del Timeout y ConnectionTimeout<p>
     *
     * @param endpoint
     * @return
     * @throws java.net.MalformedURLException <p>
     */
    private static URL settingTimeOut(String endpoint) throws MalformedURLException {
        URL endpointSettingTimeOut = new URL(new URL(endpoint), endpoint, new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(URL u) throws IOException {
                    URL target = new URL(u.toString());
                    URLConnection connection = target.openConnection();
                    // Connection settings
                    connection.setConnectTimeout(connectTimeout); // 20 sec
                    connection.setReadTimeout(readTimeout); // 1 min
                    return(connection);
                }
            });
        return endpointSettingTimeOut;
    }


    /**
     * Descripcion: Realiza el setting del SOAPAction. <p>
     *
     * @param soapAction
     * @param soapMessage <p>
     */
    private static void setSOAPAction(String soapAction, SOAPMessage soapMessage) throws SOAPException {
        String settingSOAPAction = (soapAction == null ? "" : soapAction);

        // Setting SOAPAction header line
        MimeHeaders headers = soapMessage.getMimeHeaders();

        //WorkArround para soportar invocación con estandar SOAP 1.1
        headers.addHeader("SOAPAction", settingSOAPAction);

        final String[] header = headers.getHeader("Content-Type");
        if (header == null) {
            headers.addHeader("Content-Type", "application/soap+xml; charset=UTF-8; action=\"" + settingSOAPAction + "\"");
        } else {
            headers.setHeader("Content-Type", "application/soap+xml; charset=UTF-8; action=\"" + settingSOAPAction + "\"");
        }
        soapMessage.saveChanges();

    }

}
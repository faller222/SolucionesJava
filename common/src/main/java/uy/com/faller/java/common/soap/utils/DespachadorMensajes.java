package uy.com.faller.java.common.soap.utils;

//import javax.jms.*;


public final class DespachadorMensajes {

    private DespachadorMensajes(){}

    /*
    public static void enviarMensaje(String soapMessage, MessageSelectorEntry messageSelector, QueueConnectionFactory qConnectionFactory, Destination inQueue) throws JMSException {
        enviarMensajePriv(soapMessage, messageSelector.getKey(), messageSelector.getValue(), qConnectionFactory, inQueue);
    }

    private static void enviarMensajePriv(String soapMessage, String nombreSelector, String selector, QueueConnectionFactory qConnectionFactory, Destination inRuteadorQueue) throws JMSException {
        QueueConnection connection = null;
        QueueSession session = null;
        try {

            //inicializo la conexion y el mensaje para ser enviado
            connection = qConnectionFactory.createQueueConnection();
            connection.start();

            session = connection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
            final MessageProducer producer = session.createProducer(inRuteadorQueue);

            TextMessage mensaje = session.createTextMessage();
            mensaje.setText(soapMessage);

            // Seteo el destino para que selector sepa a que donde va
            mensaje.setStringProperty(nombreSelector, selector);

            producer.send(mensaje);

        } finally {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }*/
}

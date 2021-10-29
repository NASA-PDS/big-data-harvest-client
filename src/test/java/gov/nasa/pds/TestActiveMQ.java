package gov.nasa.pds;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;


public class TestActiveMQ
{

    public static void main(String[] args) throws Exception
    {
    }
    
    
    public static void send() throws Exception
    {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection con = factory.createConnection();
        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        Destination destination = session.createQueue("harvest.jobs");        
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        
        TextMessage message = session.createTextMessage("test");
        
        producer.send(message);
        
        session.close();
        con.close();
    }

}

package gov.nasa.pds.harvest.mq;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import gov.nasa.pds.harvest.Constants;
import gov.nasa.pds.harvest.cfg.ActiveMQCfg;
import gov.nasa.pds.harvest.util.Logger;


/**
 * ActiveMQ publisher
 * @author karpenko
 */
public class ActiveMQPublisher implements MQPublisher
{
    private Connection con;
    private Session session;
    private Destination destination;
    private MessageProducer producer;
    
    /**
     * Constructor
     * @param cfg ActiveMQ configuration
     * @throws Exception an exception
     */
    public ActiveMQPublisher(ActiveMQCfg cfg) throws Exception
    {
        Logger.info("Connecting to ActiveMQ at " + cfg.url);
        
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(cfg.url);
        if(cfg.userName != null)
        {
            factory.setUserName(cfg.userName);
            factory.setPassword(cfg.password);
        }
        
        con = factory.createConnection();
        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        destination = session.createQueue(Constants.MQ_JOBS);        
        producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
    }

    
    @Override
    public void publish(String message) throws Exception
    {
        TextMessage mqMsg = session.createTextMessage(message);
        producer.send(mqMsg);
    }

    
    @Override
    public void close()
    {
        try
        {
            session.close();
        }
        catch(Exception ex)
        {
            // Ignore
        }
        
        try
        {
            con.close();
        }
        catch(Exception ex)
        {
            // Ignore
        }
    }
}

package gov.nasa.pds.harvest.mq;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import gov.nasa.pds.harvest.cfg.ActiveMQCfg;
import gov.nasa.pds.harvest.util.Logger;
import gov.nasa.pds.registry.common.mq.msg.MQConstants;


/**
 * ActiveMQ publisher
 * @author karpenko
 */
public class ActiveMQPublisher implements MQPublisher
{
    private Connection con;
    private Session session;

    private Destination harvestDestination;
    private MessageProducer harvestJobProducer;
    
    private Destination managerDestination;
    private MessageProducer managerCommandProducer;

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
        
        // Connection & session
        con = factory.createConnection();
        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        // Harvest destination
        harvestDestination = session.createQueue(MQConstants.MQ_JOBS);        
        harvestJobProducer = session.createProducer(harvestDestination);
        harvestJobProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
        
        // Manager destination
        managerDestination = session.createQueue(MQConstants.MQ_MANAGER_COMMANDS);        
        managerCommandProducer = session.createProducer(managerDestination);
        managerCommandProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
    }

    
    @Override
    public void publishHarvestJob(String message) throws Exception
    {
        TextMessage mqMsg = session.createTextMessage(message);
        harvestJobProducer.send(mqMsg);
    }

    
    @Override
    public void publishManagerCommand(String message) throws Exception
    {
        TextMessage mqMsg = session.createTextMessage(message);
        managerCommandProducer.send(mqMsg);
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

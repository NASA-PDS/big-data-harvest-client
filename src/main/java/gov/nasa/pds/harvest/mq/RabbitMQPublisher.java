package gov.nasa.pds.harvest.mq;

import java.util.ArrayList;
import java.util.List;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import gov.nasa.pds.harvest.Constants;
import gov.nasa.pds.harvest.cfg.IPAddress;
import gov.nasa.pds.harvest.cfg.RabbitMQCfg;
import gov.nasa.pds.harvest.util.CloseUtils;
import gov.nasa.pds.harvest.util.Logger;

/**
 * RabbitMQ publisher
 * @author karpenko
 */
public class RabbitMQPublisher implements MQPublisher
{
    private Connection con;
    private Channel channel;
    
    /**
     * Constructor
     * @param cfg RabbitMQ configuration
     * @throws Exception an exception
     */
    public RabbitMQPublisher(RabbitMQCfg cfg) throws Exception
    {
        con = connect(cfg);
        channel = con.createChannel();
    }

    
    @Override
    public void publishHarvestJob(String message) throws Exception
    {
        channel.txSelect();
        channel.basicPublish("", Constants.MQ_HARVEST_JOBS, 
                MessageProperties.MINIMAL_PERSISTENT_BASIC, message.getBytes());
        channel.txCommit();
    }

    
    @Override
    public void publishManagerCommand(String message) throws Exception
    {
        channel.txSelect();
        channel.basicPublish("", Constants.MQ_MANAGER_COMMANDS, 
                MessageProperties.MINIMAL_PERSISTENT_BASIC, message.getBytes());
        channel.txCommit();
    }

    
    @Override
    public void close()
    {
        CloseUtils.close(con);
    }

    
    private Connection connect(RabbitMQCfg cfg) throws Exception
    {
        Logger.info("Connecting to RabbitMQ");

        ConnectionFactory factory = new ConnectionFactory();
        if(cfg.userName != null)
        {
            factory.setUsername(cfg.userName);
            factory.setPassword(cfg.password);
        }
        
        List<Address> rmqAddr = new ArrayList<>();
        for(IPAddress ipa: cfg.addresses)
        {
            rmqAddr.add(new Address(ipa.getHost(), ipa.getPort()));
        }

        Connection con = factory.newConnection(rmqAddr);
        return con;
    }

}

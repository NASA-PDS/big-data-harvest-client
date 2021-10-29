package gov.nasa.pds.harvest.cfg;


/**
 * Harvest client configuration
 * @author karpenko
 */
public class Configuration
{
    /**
     * Message server type
     */
    public MQType mqType;
    
    /**
     * ActiveMQ configuration
     */
    public ActiveMQCfg amqCfg = new ActiveMQCfg();
    
    /**
     * RabbitMQ configuration
     */
    public RabbitMQCfg rmqCfg = new RabbitMQCfg();
}

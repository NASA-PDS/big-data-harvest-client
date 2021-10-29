package gov.nasa.pds.harvest.cfg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import gov.nasa.pds.harvest.util.CloseUtils;
import gov.nasa.pds.harvest.util.Logger;


/**
 * Reads Harvest client configuration file.
 * @author karpenko
 */
public class ConfigurationReader
{
    private static final String PROP_MQ_TYPE = "mq.type";
    
    private static final String PROP_RMQ_HOST = "rmq.host";
    private static final String PROP_RMQ_USER = "rmq.user";
    private static final String PROP_RMQ_PASS = "rmq.password";

    private static final String PROP_AMQ_URL = "amq.url";
    private static final String PROP_AMQ_USER = "amq.user";
    private static final String PROP_AMQ_PASS = "amq.password";
    
    private static final IPAddress DEFAULT_RMQ_HOST = new IPAddress("localhost", 5672);
    private static final String DEFAULT_AMQ_URL = "tcp://localhost:61616";

    
    /**
     * Constructor
     */
    public ConfigurationReader()
    {
    }


    /**
     * Read configuration file (Java properties / key-value file)
     * @param file a configuration file
     * @return parsed configuration
     * @throws Exception an exception
     */
    public Configuration read(File file) throws Exception
    {
        Configuration cfg = parseConfigFile(file);
        validate(cfg);
        
        return cfg;
    }
    
    
    private void validate(Configuration cfg) throws Exception
    {
        if(cfg.mqType == null)
        {
            String msg = String.format("Invalid configuration. Property '%s' is not set.", PROP_MQ_TYPE);
            throw new Exception(msg);
        }
        
        switch(cfg.mqType)
        {
        case ActiveMQ:
            validateAMQ(cfg.amqCfg);
            break;
        case RabbitMQ:
            validateRMQ(cfg.rmqCfg);
            break;
        }
    }
    
    
    private void validateRMQ(RabbitMQCfg cfg) throws Exception
    {
        // Validate MQ address
        if(cfg.addresses.isEmpty())
        {
            cfg.addresses.add(DEFAULT_RMQ_HOST);
            String msg = String.format("'%s' property is not set. Will use default value: %s", 
                    PROP_RMQ_HOST, DEFAULT_RMQ_HOST.toString());
            Logger.warn(msg);
        }
    }

    
    private void validateAMQ(ActiveMQCfg cfg) throws Exception
    {
        if(cfg.url == null || cfg.url.isBlank())
        {
            cfg.url = DEFAULT_AMQ_URL;
            String msg = String.format("'%s' property is not set. Will use default value: %s", 
                    PROP_AMQ_URL, cfg.url);
            Logger.warn(msg);
        }
    }
    
    
    private Configuration parseConfigFile(File file) throws Exception
    {
        Configuration cfg = new Configuration();
        
        BufferedReader rd = null;
        try
        {
            rd = new BufferedReader(new FileReader(file));
            String line;
            while((line = rd.readLine()) != null)
            {
                line = line.trim();
                if(line.startsWith("#") || line.isEmpty()) continue;
                
                String[] tokens = line.split("=");
                if(tokens.length != 2) throw new Exception("Invalid property line: " + line);
                String key = tokens[0].trim();
                String value = tokens[1].trim();
                
                switch(key)
                {
                case PROP_MQ_TYPE:
                    cfg.mqType = parseMQType(value);
                    break;
                    
                // RabbitMQ
                case PROP_RMQ_HOST:
                    cfg.rmqCfg.addresses.add(parseMQAddresses(value));
                    break;
                case PROP_RMQ_USER:
                    cfg.rmqCfg.userName = value;
                    break;
                case PROP_RMQ_PASS:
                    cfg.rmqCfg.password = value;
                    break;

                // ActiveMQ
                case PROP_AMQ_URL:
                    cfg.amqCfg.url = value;
                    break;
                case PROP_AMQ_USER:
                    cfg.amqCfg.userName = value;
                    break;
                case PROP_AMQ_PASS:
                    cfg.amqCfg.password = value;
                    break;
                    
                default:
                    throw new Exception("Invalid property '" + key + "'");
                }
            }
        }
        finally
        {
            CloseUtils.close(rd);
        }
        
        return cfg;
    }

    
    private MQType parseMQType(String str) throws Exception
    {
        if("ActiveMQ".equalsIgnoreCase(str)) return MQType.ActiveMQ;
        if("RabbitMQ".equalsIgnoreCase(str)) return MQType.RabbitMQ;
        
        String msg = String.format("Invalid '%s' property value: '%s'. Expected 'ActiveMQ' or 'RabbitMQ'.", 
                PROP_MQ_TYPE, str);
        throw new Exception(msg);
    }
    
    
    private IPAddress parseMQAddresses(String str) throws Exception
    {
        String[] tokens = str.split(":");
        if(tokens.length != 2) 
        {
            String msg = String.format("Invalid '%s' property value: '%s'. Expected 'host:port'.", PROP_RMQ_HOST, str);
            throw new Exception(msg);
        }
        
        String host = tokens[0];
        int port = 0;
        
        try
        {
            port = Integer.parseInt(tokens[1]);
        }
        catch(Exception ex)
        {
            String msg = String.format("Invalid port in '%s' property: '%s'", PROP_RMQ_HOST, str);
            throw new Exception(msg);
        }
            
        return new IPAddress(host, port);
    }
}

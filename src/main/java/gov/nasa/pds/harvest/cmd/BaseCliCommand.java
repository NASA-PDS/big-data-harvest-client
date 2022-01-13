package gov.nasa.pds.harvest.cmd;

import java.io.File;

import org.apache.commons.cli.CommandLine;

import gov.nasa.pds.harvest.cfg.Configuration;
import gov.nasa.pds.harvest.cfg.ConfigurationReader;
import gov.nasa.pds.harvest.mq.ActiveMQPublisher;
import gov.nasa.pds.harvest.mq.MQPublisher;
import gov.nasa.pds.harvest.mq.RabbitMQPublisher;
import gov.nasa.pds.harvest.util.Logger;


public abstract class BaseCliCommand implements CliCommand
{
    protected Configuration clientCfg;

    
    protected void loadConfigurationFile(CommandLine cmdLine) throws Exception
    {
        String fileName = cmdLine.getOptionValue("c");
        clientCfg = readConfigFile(fileName);
    }
    
    
    private static Configuration readConfigFile(String fileName) throws Exception
    {
        File file = getConfigFile(fileName);
        if(!file.exists()) 
        {
            throw new Exception("Configuration file " + file.getAbsolutePath() + " does not exist");
        }

        Logger.info("Reading configuration from " + file.getAbsolutePath());        
        
        ConfigurationReader reader = new ConfigurationReader();
        return reader.read(file);
    }

    
    private static File getConfigFile(String fileName) throws Exception
    {
        File file;
        
        if(fileName == null)
        {
            String home = System.getenv("HARVEST_CLIENT_HOME");
            if(home == null) 
            {
                String msg = "HARVEST_CLIENT_HOME environment variable is not set. Could not get default configuration file.";
                throw new Exception(msg);
            }
    
            file = new File(home, "conf/harvest-client.cfg");
        }
        else
        {
            file = new File(fileName);
        }

        return file;
    }

    
    protected static MQPublisher createPublisher(Configuration cfg) throws Exception
    {
        if(cfg == null || cfg.mqType == null)
        {
            throw new Exception("Invalid configuration. Message server type is not set.");
        }
        
        switch(cfg.mqType)
        {
        case ActiveMQ:
            return new ActiveMQPublisher(cfg.amqCfg);
        case RabbitMQ:
            return new RabbitMQPublisher(cfg.rmqCfg);
        }
        
        throw new Exception("Invalid message server type: " + cfg.mqType);
    }

}

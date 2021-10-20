package gov.nasa.pds.harvest.cfg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import gov.nasa.pds.harvest.util.CloseUtils;
import gov.nasa.pds.harvest.util.Logger;


public class ConfigurationReader
{
    private static final String PROP_MQ_HOST = "mq.host";
    private static final IPAddress DEFAULT_MQ_HOST = new IPAddress("localhost", 5672);
    
    
    public ConfigurationReader()
    {
    }


    public Configuration read(File file) throws Exception
    {
        Configuration cfg = parseConfigFile(file);
    
        // Validate MQ address
        if(cfg.mqAddresses.isEmpty())
        {
            cfg.mqAddresses.add(DEFAULT_MQ_HOST);
            String msg = String.format("'%s' property is not set. Will use default value: %s", 
                    PROP_MQ_HOST, DEFAULT_MQ_HOST.toString());
            Logger.warn(msg);
        }
        
        return cfg;
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
                case PROP_MQ_HOST:
                    cfg.mqAddresses.add(parseMQAddresses(value));
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

    
    private IPAddress parseMQAddresses(String str) throws Exception
    {
        String[] tokens = str.split(":");
        if(tokens.length != 2) 
        {
            String msg = String.format("Invalid '%s' property: '%s'. Expected 'host:port' value.", PROP_MQ_HOST, str);
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
            String msg = String.format("Invalid port in '%s' property: '%s'", PROP_MQ_HOST, str);
            throw new Exception(msg);
        }
            
        return new IPAddress(host, port);
    }
}

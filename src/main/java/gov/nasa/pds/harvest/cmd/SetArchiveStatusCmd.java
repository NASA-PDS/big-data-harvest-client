package gov.nasa.pds.harvest.cmd;

import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.cli.CommandLine;

import gov.nasa.pds.harvest.mq.MQPublisher;
import gov.nasa.pds.harvest.mq.msg.ManagerMessageBuilder;
import gov.nasa.pds.harvest.util.Logger;


/**
 * A CLI command to submit request to Harvest Server to set 
 * PDS label archive status in Elasticsearch registry index.
 * 
 * @author karpenko
 */
public class SetArchiveStatusCmd extends BaseCliCommand
{
    private Set<String> statusNames; 

    private String lidvid;
    private String status;
    private String requestId;
    
    /**
     * Constructor
     */
    public SetArchiveStatusCmd()
    {
        statusNames = new TreeSet<>();
        statusNames.add("staged");
        statusNames.add("archived");
    }
    
    /**
     * Run this command.
     * @param cmdLine Apache Commons CLI library's class 
     * containing parsed command line parameters.
     * @throws Exception Generic exception
     */
    @Override
    public void run(CommandLine cmdLine) throws Exception
    {
        if(cmdLine.hasOption("help"))
        {
            printHelp();
            return;
        }

        configure(cmdLine);
        publish();
    }

    
    /**
     * Print help screen
     */
    public void printHelp()
    {
        System.out.println("Usage: harvest-client set-archive-status <options>");
        
        System.out.println();
        System.out.println("Set product archive status by LIDVID.");
        System.out.println("- If a product is a collection, also set status of primary product references");
        System.out.println("  listed in the collection inventory file.");
        System.out.println();
        System.out.println("Required parameters:");
        System.out.println("  -status <status>   One of the following values:");

        for(String name: statusNames)
        {
            System.out.println("     " + name);
        }
        
        System.out.println("  -lidvid <id>       Update archive status of a product with given LIDVID");
        System.out.println("Optional parameters:");
        System.out.println("  -c <path>   Harvest Client configuration file.");
        System.out.println("              Default is CLIENT_HOME/conf/harvest-client.cfg");
        System.out.println();
    }

    
    private void configure(CommandLine cmdLine) throws Exception
    {
        loadConfigurationFile(cmdLine);

        // Status
        status = getStatus(cmdLine);
        
        // LIDVID
        lidvid = cmdLine.getOptionValue("lidvid");
        if(lidvid == null) throw new Exception("Missing required parameter '-lidvid'");
    }

    
    private String getStatus(CommandLine cmdLine) throws Exception
    {
        String tmp = cmdLine.getOptionValue("status");
        if(tmp == null) 
        {
            throw new Exception("Missing required parameter '-status'");
        }

        String status = tmp.toLowerCase();
        if(!statusNames.contains(status))
        {
            throw new Exception("Invalid '-status' parameter value: '" + tmp + "'");
        }
        
        return status;
    }

    
    /**
     * Publish new message to RabbitMQ
     * @throws Exception an exception
     */
    private void publish() throws Exception
    {
        requestId = UUID.randomUUID().toString();
        Logger.info("Creating new 'set-archive-status' request...");
    
        // Create new job message
        String msg = createNewMessage();
        
        MQPublisher pub = createPublisher(clientCfg);
        
        try
        {
            pub.publishManagerCommand(msg);
        }
        finally
        {
            pub.close();
        }

        Logger.info("Created request " + requestId);
    }

    
    private String createNewMessage() throws Exception
    {
        ManagerMessageBuilder bld = new ManagerMessageBuilder(Logger.getLevel() == Logger.LEVEL_DEBUG);
        String json = bld.createSetArchiveStatusMessage(requestId, lidvid, status);
             
        Logger.debug("Job message:\n" + json);
        
        return json;
    }

}

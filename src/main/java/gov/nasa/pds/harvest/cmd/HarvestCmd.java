package gov.nasa.pds.harvest.cmd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.cli.CommandLine;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import gov.nasa.pds.harvest.Constants;
import gov.nasa.pds.harvest.cfg.Configuration;
import gov.nasa.pds.harvest.cfg.ConfigurationReader;
import gov.nasa.pds.harvest.cfg.IPAddress;
import gov.nasa.pds.harvest.job.JobReader;
import gov.nasa.pds.harvest.job.model.Job;
import gov.nasa.pds.harvest.mq.JobMessageBuilder;
import gov.nasa.pds.harvest.util.CloseUtils;
import gov.nasa.pds.harvest.util.Logger;


public class HarvestCmd implements CliCommand
{
    private Configuration cfg;
    private Job job;
    private boolean overwriteFlag;
    private String jobId;
    
    
    public HarvestCmd()
    {
    }

    
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


    private void publish() throws Exception
    {
        jobId = UUID.randomUUID().toString();
        Logger.info("Creating new job...");
    
        // Create new job message
        String msg = createNewJobMessage();
        
        // Connect to RabbitMQ
        Connection con = null;
        
        try
        {
            Logger.info("Connecting to RabbitMQ");
            con = connectToRabbitMQ();
            Channel channel = con.createChannel();
            
            channel.txSelect();
            channel.basicPublish("", Constants.MQ_JOBS, 
                    MessageProperties.MINIMAL_PERSISTENT_BASIC, msg.getBytes());
            channel.txCommit();
        }
        finally
        {
            CloseUtils.close(con);
        }

        Logger.info("Created job " + jobId);
    }
    
    
    private Connection connectToRabbitMQ() throws Exception
    {
        ConnectionFactory factory = new ConnectionFactory();
        
        List<Address> rmqAddr = new ArrayList<>();
        for(IPAddress ipa: cfg.mqAddresses)
        {
            rmqAddr.add(new Address(ipa.getHost(), ipa.getPort()));
        }

        Connection con = factory.newConnection(rmqAddr);
        return con;
    }
    
    
    private String createNewJobMessage() throws Exception
    {
        JobMessageBuilder bld = new JobMessageBuilder(Logger.getLevel() == Logger.LEVEL_DEBUG);
        bld.setJob(jobId, job);
        bld.setOverwriteFlag(overwriteFlag);
        String json = bld.build();
             
        Logger.debug("Job message:\n" + json);
        
        return json;
    }
    
    
    /**
     * Print help screen.
     */
    public void printHelp()
    {
        System.out.println("Usage: harvest-client harvest <options>");

        System.out.println();
        System.out.println("Submit new harvest job");
        System.out.println();
        System.out.println("Required parameters:");
        System.out.println("  -j <path>   Harvest job file");
        System.out.println();
        System.out.println("Optional parameters:");
        System.out.println("  -c <path>    Harvest Client configuration file. Default is $HARVEST_CLIENT_HOME/conf/harvest.cfg");
        System.out.println("  -overwrite   Overwrite registered products");
        System.out.println();
    }

    
    private void configure(CommandLine cmdLine) throws Exception
    {
        // Job file
        String fileName = cmdLine.getOptionValue("j");
        if(fileName == null) throw new Exception("Missing required parameter '-j'");
        job = readJobFile(fileName);
                
        // Configuration file
        fileName = cmdLine.getOptionValue("c");
        cfg = readConfigFile(fileName);
        
        overwriteFlag = cmdLine.hasOption("overwrite");
    }
    
    
    private Configuration readConfigFile(String fileName) throws Exception
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
    
    
    private Job readJobFile(String fileName) throws Exception
    {
        File file = new File(fileName);
        if(!file.exists()) 
        {
            throw new Exception("Job file " + file.getAbsolutePath() + " does not exist");
        }
        
        Logger.info("Reading job from " + file.getAbsolutePath());        
        
        JobReader reader = new JobReader();
        return reader.read(file);
    }
    
    
    private File getConfigFile(String fileName) throws Exception
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
    
}

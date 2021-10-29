package gov.nasa.pds.harvest.cmd;

import java.io.File;
import java.util.UUID;

import org.apache.commons.cli.CommandLine;

import gov.nasa.pds.harvest.cfg.Configuration;
import gov.nasa.pds.harvest.cfg.ConfigurationReader;
import gov.nasa.pds.harvest.job.JobReader;
import gov.nasa.pds.harvest.job.model.Job;
import gov.nasa.pds.harvest.mq.ActiveMQPublisher;
import gov.nasa.pds.harvest.mq.MQPublisher;
import gov.nasa.pds.harvest.mq.RabbitMQPublisher;
import gov.nasa.pds.harvest.mq.msg.JobMessageBuilder;
import gov.nasa.pds.harvest.util.Logger;


/**
 * A CLI command to submit (publish to RabbitMQ) new harvest job. 
 * @author karpenko
 */
public class HarvestCmd implements CliCommand
{
    private Configuration cfg;
    private Job job;
    private boolean overwriteFlag;
    private String jobId;
    
    
    /**
     * Constructor
     */
    public HarvestCmd()
    {
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
     * Publish new job message to RabbitMQ
     * @throws Exception an exception
     */
    private void publish() throws Exception
    {
        jobId = UUID.randomUUID().toString();
        Logger.info("Creating new job...");
    
        // Create new job message
        String msg = createNewJobMessage();
        
        MQPublisher pub = createPublisher(cfg);
        
        try
        {
            pub.publish(msg);
        }
        finally
        {
            pub.close();
        }

        Logger.info("Created job " + jobId);
    }
    
    
    private MQPublisher createPublisher(Configuration cfg) throws Exception
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

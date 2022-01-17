package gov.nasa.pds.harvest.cmd;

import java.io.File;
import java.util.UUID;

import org.apache.commons.cli.CommandLine;

import gov.nasa.pds.harvest.job.JobReader;
import gov.nasa.pds.harvest.job.model.Job;
import gov.nasa.pds.harvest.mq.MQPublisher;
import gov.nasa.pds.harvest.mq.msg.HarvestJobMessageBuilder;
import gov.nasa.pds.harvest.util.Logger;


/**
 * A CLI command to submit (publish to RabbitMQ) new harvest job. 
 * @author karpenko
 */
public class HarvestCmd extends BaseCliCommand
{
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
        Logger.info("Creating new harvest job...");
    
        // Create new job message
        String msg = createNewJobMessage();
        
        MQPublisher pub = createPublisher(clientCfg);
        
        try
        {
            pub.publishHarvestJob(msg);
        }
        finally
        {
            pub.close();
        }

        Logger.info("Created job " + jobId);
    }
    
    
    private String createNewJobMessage() throws Exception
    {
        HarvestJobMessageBuilder bld = new HarvestJobMessageBuilder(Logger.getLevel() == Logger.LEVEL_DEBUG);
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
        System.out.println("  -j <path>    Harvest job file");
        System.out.println();
        System.out.println("Optional parameters:");
        System.out.println("  -c <path>    Harvest Client configuration file.");
        System.out.println("               Default is CLIENT_HOME/conf/harvest-client.cfg");
        System.out.println("  -overwrite   Overwrite registered products");
        System.out.println();
    }

    
    private void configure(CommandLine cmdLine) throws Exception
    {
        loadConfigurationFile(cmdLine);
        
        // Job file
        String fileName = cmdLine.getOptionValue("j");
        if(fileName == null) throw new Exception("Missing required parameter '-j'");
        job = readJobFile(fileName);

        // Overwrite flag
        overwriteFlag = cmdLine.hasOption("overwrite");
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
    
}

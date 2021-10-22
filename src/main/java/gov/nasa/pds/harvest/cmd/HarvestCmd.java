package gov.nasa.pds.harvest.cmd;

import java.io.File;
import java.util.UUID;

import org.apache.commons.cli.CommandLine;

import gov.nasa.pds.harvest.cfg.Configuration;
import gov.nasa.pds.harvest.cfg.ConfigurationReader;
import gov.nasa.pds.harvest.job.JobReader;
import gov.nasa.pds.harvest.job.model.Job;
import gov.nasa.pds.harvest.mq.JobMessageBuilder;
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
        Logger.info("Creating job " + jobId);
    
        // Create new job message
        String msg = createNewJobMessage();
        
        
        Logger.info("Done");
    }
    
    
    private String createNewJobMessage() throws Exception
    {
        JobMessageBuilder bld = new JobMessageBuilder(false);
        bld.setJob(jobId, job);
        bld.setOverwriteFlag(overwriteFlag);
        String json = bld.build();
             
        Logger.debug("Job message: " + json);
        
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

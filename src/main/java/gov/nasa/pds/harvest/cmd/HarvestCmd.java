package gov.nasa.pds.harvest.cmd;

import java.io.File;
import java.util.UUID;
import java.util.logging.LogManager;

import org.apache.commons.cli.CommandLine;

import gov.nasa.pds.harvest.util.Logger;


public class HarvestCmd implements CliCommand
{
    public HarvestCmd()
    {
    }

    
    @Override
    public void run(CommandLine cmdLine) throws Exception
    {
        String runId = UUID.randomUUID().toString();

        if(cmdLine.hasOption("help"))
        {
            printHelp();
            return;
        }

        configure(cmdLine);

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
        System.out.println("  -c <path>   Harvest Client configuration file. Default is $HARVEST_CLIENT_HOME/conf/harvest.cfg");
        System.out.println();
    }

    
    private void configure(CommandLine cmdLine) throws Exception
    {
        // Job file
        String fileName = cmdLine.getOptionValue("j");
        if(fileName == null) throw new Exception("Missing required parameter '-j'");
        readJobFile(fileName);
        
        
        // Configuration file
        fileName = cmdLine.getOptionValue("c");
        readConfigFile(fileName);
        
        //ConfigReader cfgReader = new ConfigReader();        
        //cfg = cfgReader.read(cfgFile);
        
        
        
        //DataPublisher pub = new DataPublisher(cfg, runId);                
        
        //Logger.info("Run (Package) ID: " + runId);        
    }
    
    
    private void readConfigFile(String fileName) throws Exception
    {
        File file = getConfigFile(fileName);
        if(!file.exists()) 
        {
            throw new Exception("Configuration file " + file.getAbsolutePath() + " does not exist");
        }

        Logger.info("Reading configuration from " + file.getAbsolutePath());        

        
    }
    
    
    private void readJobFile(String fileName) throws Exception
    {
        File file = new File(fileName);
        if(!file.exists()) 
        {
            throw new Exception("Job file " + file.getAbsolutePath() + " does not exist");
        }
        
        
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

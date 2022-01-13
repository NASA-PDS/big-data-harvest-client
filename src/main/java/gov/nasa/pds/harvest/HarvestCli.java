package gov.nasa.pds.harvest;

import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import gov.nasa.pds.harvest.cmd.CliCommand;
import gov.nasa.pds.harvest.cmd.HarvestCmd;
import gov.nasa.pds.harvest.util.ExceptionUtils;
import gov.nasa.pds.harvest.util.Logger;
import gov.nasa.pds.harvest.util.ManifestUtils;


/**
 * Main CLI (Command-Line Interface) manager / dispatcher.
 * CrawlerCli.run() method parses command-line parameters and 
 * calls different CLI commands, such as "crawl", etc.
 *   
 * @author karpenko
 */
public class HarvestCli
{
    private Options options;
    private CommandLine cmdLine;
    private Map<String, CliCommand> commands;
    private CliCommand command;
    
    
    /**
     * Constructor
     */
    public HarvestCli()
    {
        initOptions();
    }

    
    /**
     * Parse command line arguments and run commands.
     * @param args command line arguments passed from the main() function.
     */
    public void run(String[] args)
    {
        if(args.length == 0)
        {
            printHelp();
            System.exit(1);
        }

        // Version
        if(args.length == 1 && ("-V".equals(args[0]) || "--version".equals(args[0])))
        {
            printVersion();
            System.exit(0);
        }        

        if(!parse(args))
        {
            System.out.println();
            printHelp();
            System.exit(1);
        }
        
        initLogger();

        if(!runCommand())
        {
            System.exit(1);
        }        
    }

    
    /**
     * Parse command line parameters
     * @param args
     * @return
     */
    private boolean parse(String[] pArgs)
    {
        try
        {
            CommandLineParser parser = new DefaultParser();
            this.cmdLine = parser.parse(options, pArgs);
            
            String[] args = cmdLine.getArgs();
            if(args == null || args.length == 0)
            {
                System.out.println("[ERROR] Missing command.");
                return false;
            }

            if(args.length > 1)
            {
                System.out.println("[ERROR] Invalid command: " + String.join(" ", args)); 
                return false;
            }
            
            initCommands();
            
            this.command = commands.get(args[0]);
            if(this.command == null)
            {
                System.out.println("[ERROR] Invalid command: " + args[0]);
                return false;
            }
            
            return true;
        }
        catch(ParseException ex)
        {
            System.out.println("[ERROR] " + ex.getMessage());
            return false;
        }
    }

    
    /**
     * Run commands based on command line parameters.
     * @return
     */
    private boolean runCommand()
    {
        try
        {
            command.run(cmdLine);
            return true;
        }
        catch(Exception ex)
        {
            String msg = ExceptionUtils.getMessage(ex);
            Logger.error(msg);
            return false;
        }
    }

    
    /**
     * Print help screen.
     */
    public void printHelp()
    {
        System.out.println("Usage: harvest-client <command> <options>");

        System.out.println();
        System.out.println("Commands:");
        System.out.println("  harvest              Submit new harvest job");
        System.out.println("  set-archive-status   Set product archive status");
        System.out.println("  -V, --version        Print Harvest Client version");
        
        System.out.println();
        System.out.println("Optional parameters:");
        System.out.println("  -v <value>   Log verbosity: DEBUG, INFO, WARN, ERROR. Default is INFO.");
        System.out.println("  -help        Pass -help after any command to see command-specific usage information, for example,");
        System.out.println("               harvest-client harvest -help");
    }
    
    
    /**
     * Initialize all CLI commands
     */
    private void initCommands()
    {
        commands = new HashMap<>();
        commands.put("harvest", new HarvestCmd());
    }
    
    /**
     * Initialize Apache Commons CLI library.
     */
    private void initOptions()
    {
        options = new Options();
        
        Option.Builder bld;

        bld = Option.builder("help");
        options.addOption(bld.build());

        bld = Option.builder("c").hasArg().argName("file");
        options.addOption(bld.build());
        
        bld = Option.builder("j").hasArg().argName("file");
        options.addOption(bld.build());

        bld = Option.builder("overwrite");
        options.addOption(bld.build());
        
        bld = Option.builder("v").hasArg().argName("level");
        options.addOption(bld.build());
    }
    
    
    /**
     * Print Harvest version
     */
    public static void printVersion()
    {
        String version = HarvestCli.class.getPackage().getImplementationVersion();
        System.out.println("Harvest Client version: " + version);
        Attributes attrs = ManifestUtils.getAttributes();
        if(attrs != null)
        {
            System.out.println("Build time: " + attrs.getValue("Build-Time"));
        }
    }

    
    private void initLogger()
    {
        String verbosity = cmdLine.getOptionValue("v", "INFO");
        int level = parseLogLevel(verbosity);
        Logger.setLevel(level);
    }

    
    private static int parseLogLevel(String verbosity)
    {
        // Logger is not setup yet. Print to console.
        if(verbosity == null)
        {
            System.out.println("[WARN] Log verbosity is not set. Will use 'INFO'.");
            return Logger.LEVEL_INFO;
        }
        
        switch(verbosity.toUpperCase())
        {
        case "DEBUG": return Logger.LEVEL_DEBUG;
        case "INFO": return Logger.LEVEL_INFO;
        case "WARN": return Logger.LEVEL_WARN;
        case "ERROR": return Logger.LEVEL_ERROR;
        }

        // Logger is not setup yet. Print to console.
        System.out.println("[WARN] Invalid log verbosity '" + verbosity + "'. Will use 'INFO'.");
        return Logger.LEVEL_INFO;
    }

}

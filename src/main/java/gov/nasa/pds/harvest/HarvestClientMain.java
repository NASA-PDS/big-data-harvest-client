package gov.nasa.pds.harvest;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Crawler main class.
 *  
 * @author karpenko
 */
public class HarvestClientMain
{
    public static void main(String[] args)
    {
        // We don't use "java.util" logger.
        Logger log = Logger.getLogger("");
        log.setLevel(Level.OFF);
        
        HarvestCli cli = new HarvestCli();
        cli.run(args);
    }

}

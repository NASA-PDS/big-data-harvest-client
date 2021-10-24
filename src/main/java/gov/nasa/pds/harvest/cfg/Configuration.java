package gov.nasa.pds.harvest.cfg;

import java.util.ArrayList;
import java.util.List;


/**
 * Harvest client configuration
 * @author karpenko
 */
public class Configuration
{
    /**
     * List of RabbitMQ addresses (host and port tuples)
     */
    public List<IPAddress> mqAddresses = new ArrayList<>();
}

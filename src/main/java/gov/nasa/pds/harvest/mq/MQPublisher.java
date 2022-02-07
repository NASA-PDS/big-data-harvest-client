package gov.nasa.pds.harvest.mq;

public interface MQPublisher
{
    public void publishHarvestJob(String message) throws Exception;
    public void publishManagerCommand(String message) throws Exception;
    public void close();
}

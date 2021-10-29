package gov.nasa.pds.harvest.mq;

public interface MQPublisher
{
    public void publish(String message) throws Exception;
    public void close();
}

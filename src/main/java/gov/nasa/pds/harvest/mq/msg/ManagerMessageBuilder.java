package gov.nasa.pds.harvest.mq.msg;

import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Creates manager messages to be sent to RabbitMQ.
 * @author karpenko
 */
public class ManagerMessageBuilder
{
    private Gson gson;

    /**
     * Constructor
     * @param pretty Pretty print JSON for debugging.
     */
    public ManagerMessageBuilder(boolean pretty)
    {
        if(pretty)
        {
            gson = new GsonBuilder().setPrettyPrinting().create();
        }
        else
        {
            gson = new Gson();
        }        
    }
    
    
    /**
     * Creates SET_ARCHIVE_STATUS message
     * @param requestId request / message id
     * @param lidvid product LIDVID
     * @param status new status
     * @return JSON formatted string
     */
    public String createSetArchiveStatusMessage(String requestId, String lidvid, String status)
    {
        if(requestId == null || requestId.isBlank()) throw new IllegalArgumentException("Missing Request ID");
        if(lidvid == null || lidvid.isBlank()) throw new IllegalArgumentException("Missing LIDVID");
        if(status == null || status.isBlank()) throw new IllegalArgumentException("Missing status");
        
        ManagerMessage msg = new ManagerMessage();
        msg.requestId = requestId;        
        msg.command = "SET_ARCHIVE_STATUS";
        
        msg.params = new TreeMap<>();
        msg.params.put("lidvid", lidvid);
        msg.params.put("status", status);
        
        return gson.toJson(msg);
    }
}

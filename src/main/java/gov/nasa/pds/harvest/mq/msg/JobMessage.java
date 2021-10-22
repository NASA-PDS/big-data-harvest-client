package gov.nasa.pds.harvest.mq.msg;

import java.util.List;
import java.util.Set;


public class JobMessage
{
    public String jobId;
    public String nodeName;
    
    public List<String> dirs;
    
    public Set<String> prodClassInclude;
    public Set<String> prodClassExclude;
    
    public List<String> fileRefs;

    public boolean overwrite = false;
}

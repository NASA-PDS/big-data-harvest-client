package gov.nasa.pds.harvest.job.model;

import java.util.List;
import java.util.Set;

public class Job
{
    public String nodeName;
    
    public List<String> bundles;
    public List<String> dirs;
    
    public List<FileRefCfg> fileRefs;

    public Set<String> prodClassInclude;
    public Set<String> prodClassExclude;

}

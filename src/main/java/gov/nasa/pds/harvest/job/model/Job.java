package gov.nasa.pds.harvest.job.model;

import java.util.List;
import java.util.Set;

/**
 * New harvest job model class. 
 * JobReader parses XML job file and then creates this class.
 * 
 * @author karpenko
 */
public class Job
{
    public String nodeName;
    
    public List<String> bundles;
    public List<String> dirs;
    public List<String> manifests;
    
    public List<FileRefCfg> fileRefs;

    public Set<String> prodClassInclude;
    public Set<String> prodClassExclude;
    public Set<String> dateFields;
}

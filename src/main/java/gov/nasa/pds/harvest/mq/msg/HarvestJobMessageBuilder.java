package gov.nasa.pds.harvest.mq.msg;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gov.nasa.pds.harvest.job.model.FileRefCfg;
import gov.nasa.pds.harvest.job.model.Job;


/**
 * Creates job messages to be sent to RabbitMQ.
 * 
 * @author karpenko
 */
public class HarvestJobMessageBuilder
{
    private Gson gson;
    
    private String jobId;
    private Job job;
    private boolean overwriteFlag = false;
    
    
    /**
     * Constructor
     * @param pretty Generate pretty-formatted JSON files. Used for debugging.
     */
    public HarvestJobMessageBuilder(boolean pretty)
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
     * Set job information
     * @param jobId job ID. Usually this is auto-generated UUID.
     * @param job job information extracted from XML configuration file.
     */
    public void setJob(String jobId, Job job)
    {
        this.jobId = jobId;
        this.job = job;
    }
    
    
    /**
     * Set to true to overwrite existing documents in the Registry
     * @param flag boolean flag.
     */
    public void setOverwriteFlag(boolean flag)
    {
        this.overwriteFlag = flag;
    }
    

    /**
     * Build new harvest job message to be sent to RabbitMQ.
     * @return JSON string.
     * @throws Exception an exception
     */
    public String build() throws Exception
    {
        // Validation
        if(jobId == null) throw new Exception("Job ID is not set.");
        if(job == null) throw new Exception("Job is not set.");
        
        // Create the message
        HarvestJobMessage msg = new HarvestJobMessage();
        msg.jobId = jobId;
        msg.nodeName = job.nodeName;
        msg.overwrite = overwriteFlag;
        
        // Directories
        msg.dirs = new ArrayList<>();
        if(job.dirs != null) msg.dirs.addAll(job.dirs);
        if(job.bundles != null) msg.dirs.addAll(job.bundles);
        if(msg.dirs.isEmpty()) msg.dirs = null;
        
        // Manifests
        if(job.manifests != null && !job.manifests.isEmpty())
        {
            msg.manifests = job.manifests;
        }
        
        if(msg.dirs == null && msg.manifests == null)
        {
            throw new Exception("Job must have directories, bundles, or manifest files.");
        }
        
        // Product filters
        msg.prodClassInclude = job.prodClassInclude;
        msg.prodClassExclude = job.prodClassExclude;
        
        // File refs
        if(job.fileRefs != null && !job.fileRefs.isEmpty())
        {
            msg.fileRefs = new ArrayList<>(job.fileRefs.size());
            for(FileRefCfg frc: job.fileRefs)
            {
                String msgItem = frc.prefix + "|" + frc.replacement;
                msg.fileRefs.add(msgItem);
            }
        }
        
        // Date Fields (/autogenFields/dateFields/field)
        msg.dateFields = job.dateFields;
        
        return gson.toJson(msg);
    }
}

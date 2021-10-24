package gov.nasa.pds.harvest.mq;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gov.nasa.pds.harvest.job.model.FileRefCfg;
import gov.nasa.pds.harvest.job.model.Job;
import gov.nasa.pds.harvest.mq.msg.JobMessage;


public class JobMessageBuilder
{
    private Gson gson;
    
    private String jobId;
    private Job job;
    private boolean overwriteFlag = false;
    
    
    public JobMessageBuilder(boolean pretty)
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

    
    public void setJob(String jobId, Job job)
    {
        this.jobId = jobId;
        this.job = job;
    }
    
    
    public void setOverwriteFlag(boolean flag)
    {
        this.overwriteFlag = flag;
    }
    

    public String build() throws Exception
    {
        // Validation
        if(jobId == null) throw new Exception("Job ID is not set.");
        if(job == null) throw new Exception("Job is not set.");
        
        // Create the message
        JobMessage msg = new JobMessage();
        msg.jobId = jobId;
        msg.nodeName = job.nodeName;
        msg.overwrite = overwriteFlag;
        
        // Directories
        msg.dirs = new ArrayList<>();
        if(job.dirs != null) msg.dirs.addAll(job.dirs);
        if(job.bundles != null) msg.dirs.addAll(job.bundles);
        if(msg.dirs.isEmpty()) throw new Exception("Job must have directories or bundles.");
        
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

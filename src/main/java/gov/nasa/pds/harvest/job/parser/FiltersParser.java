package gov.nasa.pds.harvest.job.parser;

import org.w3c.dom.Document;

import gov.nasa.pds.harvest.job.model.Job;
import gov.nasa.pds.registry.common.util.xml.XPathUtils;


/**
 * Harvest configuration file parser. Parses "/harvest/productFilter" section.
 * 
 * @author karpenko
 */
public class FiltersParser
{
    /**
     * Parse "/harvest/productFilter" section of harvest job configuration file.
     * @param doc
     * @param job
     * @throws Exception
     */
    public static void parseFilters(Document doc, Job job) throws Exception
    {
        parseProductFilter(doc, job);
    }

    
    private static void parseProductFilter(Document doc, Job job) throws Exception
    {
        XPathUtils xpu = new XPathUtils();
        
        int count = xpu.getNodeCount(doc, "/harvest/productFilter");
        if(count > 1) throw new Exception("Could not have more than one '/harvest/productFilter' element.");

        job.prodClassInclude = xpu.getStringSet(doc, "/harvest/productFilter/includeClass");
        job.prodClassExclude = xpu.getStringSet(doc, "/harvest/productFilter/excludeClass");
        
        if(job.prodClassInclude != null && job.prodClassInclude.size() > 0 
                && job.prodClassExclude != null && job.prodClassExclude.size() > 0)
        {
            throw new Exception("<productFilter> could not have both <include> and <exclude> at the same time.");
        }
    }

}

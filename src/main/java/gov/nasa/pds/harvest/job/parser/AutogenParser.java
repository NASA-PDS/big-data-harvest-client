package gov.nasa.pds.harvest.job.parser;

import java.util.Set;

import org.w3c.dom.Document;

import gov.nasa.pds.registry.common.util.xml.XPathUtils;


/**
 * Harvest configuration file parser. Parses "/harvest/autogenFields" section.
 *  
 * @author karpenko
 */
public class AutogenParser
{
    /**
     * Parse '/harvest/autogenFields/dateFields/field' section of harvest job file
     * @param doc XML DOM
     * @return a collection of date field names
     * @throws Exception an exception
     */
    public static Set<String> parseDateFields(Document doc) throws Exception
    {
        XPathUtils xpu = new XPathUtils();

        int count = xpu.getNodeCount(doc, "/harvest/autogenFields");
        if(count == 0) return null;
        if(count > 1) throw new Exception("Could not have more than one '/harvest/autogenFields' element.");

        // Date fields
        return xpu.getStringSet(doc, "/harvest/autogenFields/dateFields/field");
    }
}

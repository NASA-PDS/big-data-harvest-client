package gov.nasa.pds.harvest.job.parser;

import java.util.Set;

import org.w3c.dom.Document;
import gov.nasa.pds.harvest.util.xml.XPathUtils;


/**
 * Harvest configuration file parser. Parses "/harvest/autogenFields" section.
 *  
 * @author karpenko
 */
public class AutogenParser
{
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

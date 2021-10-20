package gov.nasa.pds.harvest.job.parser;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import gov.nasa.pds.harvest.job.model.FileRefCfg;
import gov.nasa.pds.harvest.util.xml.XPathUtils;
import gov.nasa.pds.harvest.util.xml.XmlDomUtils;


/**
 * Harvest configuration file parser. Parses "/harvest/fileInfo" section.
 * 
 * @author karpenko
 */
public class FileInfoParser
{
    /**
     * Parse &lt;fileInfo&gt; section of Harvest configuration file
     * @param doc Parsed Harvest configuration file (XMl DOM)
     * @return File info model object
     * @throws Exception an exception
     */
    public static List<FileRefCfg> parseFileInfo(Document doc) throws Exception
    {
        XPathUtils xpu = new XPathUtils();
        
        int count = xpu.getNodeCount(doc, "/harvest/fileInfo");
        if(count == 0) return null;
        if(count > 1) throw new Exception("Could not have more than one '/harvest/fileInfo' element.");

        return parseFileRef(doc);
    }
    
    
    public static List<FileRefCfg> parseFileRef(Document doc) throws Exception
    {
        XPathUtils xpu = new XPathUtils();
        
        NodeList nodes = xpu.getNodeList(doc, "/harvest/fileInfo/fileRef");
        if(nodes == null || nodes.getLength() == 0) return null;
        
        List<FileRefCfg> list = new ArrayList<>();
        for(int i = 0; i < nodes.getLength(); i++)
        {
            FileRefCfg rule = new FileRefCfg();
            rule.prefix = XmlDomUtils.getAttribute(nodes.item(i), "replacePrefix");
            rule.replacement = XmlDomUtils.getAttribute(nodes.item(i), "with");
            
            if(rule.prefix == null) throw new Exception("'/harvest/fileInfo/fileRef' missing 'replacePrefix' attribute");
            if(rule.replacement == null) throw new Exception("'/harvest/fileInfo/fileRef' missing 'with' attribute");
            
            list.add(rule);
        }

        return list;
    }
    
}

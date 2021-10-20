package gov.nasa.pds.harvest.job.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import gov.nasa.pds.harvest.job.model.BundleCfg;
import gov.nasa.pds.harvest.util.xml.XmlDomUtils;


/**
 * Harvest configuration file parser. Parses "/harvest/bundles" section.
 *  
 * @author karpenko
 *
 */
public class BundleConfigParser
{
    public static List<String> parseBundles(Node root) throws Exception
    {
        int count = XmlDomUtils.countChildNodes(root, "bundles");
        if(count == 0) return null;
        if(count > 1) throw new Exception("Could not have more than one '/harvest/bundles' element");

        Node bundlesNode = XmlDomUtils.getFirstChild(root, "bundles");
        List<Node> bundleNodeList = XmlDomUtils.getChildNodes(bundlesNode, "bundle");
        if(bundleNodeList.size() == 0) 
        {
            throw new Exception("Provide at least one '/harvest/bundles/bundle' element");
        }

        List<String> list = new ArrayList<>();
        
        for(Node bundleNode: bundleNodeList)
        {
            BundleCfg cfg = parseBundle(bundleNode);
            
            if(cfg.dir == null || cfg.dir.isEmpty())
            {
                throw new Exception("'/harvest/bundles/bundle' is missing 'dir' attribute");
            }
            
            list.add(cfg.dir);
        }
        
        return list;
    }

    
    private static BundleCfg parseBundle(Node bundleNode) throws Exception
    {
        BundleCfg cfg = new BundleCfg();
        parseBundleAttributes(bundleNode, cfg);
        
        return cfg;
    }
    
    
    private static void parseBundleAttributes(Node bundleNode, BundleCfg cfg) throws Exception
    {
        NamedNodeMap atts = XmlDomUtils.getAttributes(bundleNode);
        for(int i = 0; i < atts.getLength(); i++)
        {
            Node attr = atts.item(i);
            String attrName = attr.getNodeName();
            
            if("dir".equals(attrName))
            {
                cfg.dir = attr.getNodeValue().trim();
            }
            else if("versions".equals(attrName))
            {
                Set<String> versions = new TreeSet<>();
                String val = attr.getNodeValue().trim();
                StringTokenizer tkz = new StringTokenizer(val, ",; \t");
                while(tkz.hasMoreTokens())
                {
                    String token = tkz.nextToken();
                    if("all".equalsIgnoreCase(token)) continue;
                    versions.add(token);
                }

                if(!versions.isEmpty())
                {
                    cfg.versions = versions;
                }
            }
            else
            {
                throw new Exception("Unknown attribute '" + attrName 
                        + "' in '/harvest/bundles/bundle' element");
            }
        }
    }
}

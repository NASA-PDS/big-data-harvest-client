package gov.nasa.pds.harvest.job.parser;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import gov.nasa.pds.registry.common.util.xml.XmlDomUtils;


/**
 * Harvest configuration file parser. Parses "/harvest/directories" section.
 * 
 * @author karpenko
 */
public class DirsParser
{
    /**
     * Parses "/harvest/directories" section.
     * @param root root DOM element
     * @return a list of directories
     * @throws Exception an exception
     */
    public static List<String> parseDirectories(Node root) throws Exception
    {
        int count = XmlDomUtils.countChildNodes(root, "directories");
        if(count == 0) return null;
        if(count > 1) throw new Exception("Could not have more than one '/harvest/directories' element");

        Node dirsNode = XmlDomUtils.getFirstChild(root, "directories");
        return parsePaths(dirsNode);
    }
    
    
    private static List<String> parsePaths(Node root) throws Exception
    {
        List<String> list = new ArrayList<>();
        
        NodeList nl = root.getChildNodes();
        for(int i = 0; i < nl.getLength(); i++)
        {
            Node node = nl.item(i);
            if(node.getNodeType() != Node.ELEMENT_NODE) continue;
            
            if(node.getNodeName().equals("path"))
            {
                String val = node.getTextContent().trim();
                if(!val.isBlank()) list.add(val);
            }
            else
            {
                throw new Exception("Invalid element '/harvest/directories/" + node.getNodeName() + "'");
            }
        }
        
        if(list.isEmpty())
        {
            throw new Exception("At least one '/harvest/directories/path' element is required.");
        }
        
        return list;
    }
}

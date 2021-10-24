package gov.nasa.pds.harvest.job;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import gov.nasa.pds.harvest.job.model.Job;
import gov.nasa.pds.harvest.job.parser.AutogenParser;
import gov.nasa.pds.harvest.job.parser.BundleConfigParser;
import gov.nasa.pds.harvest.job.parser.DirsParser;
import gov.nasa.pds.harvest.job.parser.FileInfoParser;
import gov.nasa.pds.harvest.job.parser.FiltersParser;
import gov.nasa.pds.harvest.job.parser.NodeNameValidator;
import gov.nasa.pds.harvest.util.xml.XmlDomUtils;


/**
 * Harvest configuration file reader.
 * 
 * @author karpenko
 */
public class JobReader
{
    private static final String ERROR = "Invalid Harvest configuration: ";
    
    private int bundlesCount = 0;
    private int dirsCount = 0;
    

    /**
     * Constructor
     */
    public JobReader()
    {
    }
    
    
    /**
     * Read Harvest job configuration file.
     * @param file Configuration file
     * @return Configuration model object
     * @throws Exception Generic exception
     */
    public Job read(File file) throws Exception
    {
        resetCounters();
        
        Document doc = XmlDomUtils.readXml(file);
        Element root = doc.getDocumentElement();
        if(!"harvest".equals(root.getNodeName()))
        {
            throw new Exception(ERROR + "Invalid root element '" + root.getNodeName() + "'. Expected 'harvest'.");
        }

        Job job = new Job();
        job.nodeName = XmlDomUtils.getAttribute(root, "nodeName");
        NodeNameValidator nnValidator = new NodeNameValidator();
        nnValidator.validate(job.nodeName);
        
        validate(root);
        
        // Bundles (<bundles>)
        if(bundlesCount > 0) job.bundles = BundleConfigParser.parseBundles(root);
        // Directories (<directories>)
        if(dirsCount > 0) job.dirs = DirsParser.parseDirectories(root);
        
        // Product filters (<includeClass> / <excludeClass>)
        FiltersParser.parseFilters(doc, job);
        // File info (<FileRef replacePrefix.../>
        job.fileRefs = FileInfoParser.parseFileInfo(doc);
        
        // Autogen fields (/autogenFields/dateFields/field)
        job.dateFields = AutogenParser.parseDateFields(doc);

        return job;
    }

    
    private void resetCounters()
    {
        bundlesCount = 0;
        dirsCount = 0;
    }
    
    
    private void validate(Element root) throws Exception
    {
        NodeList nodes = root.getChildNodes();
        for(int i = 0; i < nodes.getLength(); i++)
        {
            Node node = nodes.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE)
            {
                String name = node.getNodeName();
                switch(name)
                {
                case "registry":
                    break;
                case "directories":
                    dirsCount++;
                    break;
                case "bundles":
                    bundlesCount++;
                    break;
                case "productFilter":
                    break;
                case "fileInfo":
                    break;
                case "autogenFields":
                    break;
                case "references":
                    break;
                default:
                    throw new Exception(ERROR + "Invalid XML element '/harvest/" + name + "'");
                }
            }
        }
        
        if(bundlesCount == 0 && dirsCount == 0)
        {
            throw new Exception(ERROR + "Either '/harvest/bundles' or '/harvest/directories' element is required.");
        }
    }
}

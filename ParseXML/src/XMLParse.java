
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.management.DynamicMBean;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.DOMBuilder;

public class XMLParse {
    private static void visitChildNodes(NodeList nList)
    {
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println("Node Name = " + node.getNodeName() + "; Value = " + node.getTextContent());
                //Check all attributes
                if (node.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = node.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node tempNode = nodeMap.item(i);
                        System.out.println("Attr name : " + tempNode.getNodeName() + "; Value = " + tempNode.getNodeValue());
                    }
                    if (node.hasChildNodes()) {
                        //We got more childs; Let's visit them as well
                        visitChildNodes(node.getChildNodes());
                    }
                }
            }
        }
    }
    private static void getChildNodes(Element ele)
    {
        List<Element> children= ele.getChildren();
        //scan all elements inside seq, all or choice
        for(int i=0;i<children.size();i++)
        {
            if(children.get(i).getChildren().size() > 0) //it has complex type or simple type
            {
             //   ComplexType
                //getChildNodes();
            }

        }
    }
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse("src/schema.xsd");
        DOMBuilder domBuilder = new DOMBuilder();
        org.jdom2.Document document = domBuilder.build(doc);
        //Get Document Builder
        Element root = document.getRootElement();

        //System.out.println(root.getName());
        List<Element> elements = root.getChildren();
        //global ele
        //List<Element> ele=elements.get(0).getChildren().get(0).getChildren();//at element

        String table="CREATE TABLE "+elements.get(0).getAttributeValue("name")+" (\n ";
        Element refCT=elements.get(0).getChildren().get(0);
        ComplexType c=new ComplexType(refCT, elements.get(0).getAttributeValue("name"));
        table+=c.createTable()+" );"+"\n---------------------------------------";

        System.out.println(table);
      //  System.out.println(elements.get(0).getChildren().get(0).getName());
    }
}

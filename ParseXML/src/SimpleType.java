import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

public class SimpleType {
    Element ele;
    String name;
    SimpleType()
    {}
    SimpleType(Element e, String name)//pass reference to complexType  tag
    {
        this.ele=e;
        this.name = name;
    }
    private String restriction(Element restrict)
    {
        //pass tree root , with restriction as root
        //get base name
        //inside restriction, check
        // for(all its children)
        //      {as per the facet, convert to sql, and place AND in between them}
        //Note: Even inside, restriction there can be simpletype, complextype content and all,
        //here, i just took restriction with facets directly.
        List<Element> children=restrict.getChildren();
        List<String> sql_cons = new ArrayList<>(); //it holds sql conversion, for each facet, on given element
        String base=restrict.getAttributeValue("base");
            sql_cons.add(base); //first you add type of the restriction
        for(int i=0;i<children.size();i++)
        {
            switch(children.get(i).getName())
            {
                case "minExclusive": sql_cons.add("check("+name+" > "+children.get(i).getAttributeValue("value")+")");
                                        break;
                case "maxExclusive": sql_cons.add("check("+name+" < "+children.get(i).getAttributeValue("value")+")");
                    break;
                case "minInclusive": sql_cons.add("check("+name+" >= "+children.get(i).getAttributeValue("value")+")");
                    break;
                case "maxInclusive": sql_cons.add("check("+name+" <= "+children.get(i).getAttributeValue("value")+")");
                    break;
                case "totalDigits": sql_cons.add("check("+name+") > "+children.get(i).getAttributeValue("value"));
                    break;
                case "fractionDigits": sql_cons.add("CAST("+name+" AS DECIMAL("+children.get(i).getAttributeValue("value")+"))");
                    break;
                case "length": sql_cons.add("("+children.get(i).getAttributeValue("value")+")");//varchar(10), varchar will be appended , you just specify, follo up bracket here.
                    break;
                case "minLength": sql_cons.add("check("+name+") > "+children.get(i).getAttributeValue("value"));
                    break;
                case "maxLength": sql_cons.add("check("+name+") > "+children.get(i).getAttributeValue("value"));
                    break;
                case "enumeration"://process all enums here only
                    String enum_str=new String();
                    enum_str+=" enum  (";
                    int j=i;
                    while(++j<children.size() && children.get(j).equals("enumeration")) {
                        sql_cons.add(" ' "+ children.get(j++).getAttributeValue("value")+" '");
                    }
                    if(j == i+1)//if only one enum, then while loop exits afer, j++. This child must not be missed, therefore make i point to before.
                        i=j-1;
                    break;
                case "pattern": sql_cons.add("check("+name+") > "+children.get(i).getAttributeValue("value"));
                    break;
            }
        }
        //process sql_cons array, and add AND in between facets if req, send all strings in sql_cons array, as one string in return.
        String result= sql_cons.get(0)+" ";
        int n=sql_cons.size();
        for(int i=1;i<n;i++)
        {
            result+=sql_cons.get(i);
            if(i!=n-1)
                result+=" AND ";
        }
        //result.substring(result.lastIndexOf("AND"));
        return result;
    }
    private String list(Element l)
    {
        //consider as multivalued attr
        String table="CREATE TABLE "+name+" ( \n"+name+"_id int PRIMARY KEY,\n ";
        String type= l.getAttributeValue("itemType");
        table+=name+" "+type+" ); \n";
        return table;
    }
    private void union()
    {

    }
    public String createColumn()
    {
        Element type=ele.getChildren().get(0);
        if(type.getName().equals("list"))
            return list(type);
        else if(type.getName().equals("restriction"))
            return restriction(type);
        else if(type.getName().equals("union"))
            union();
        return "";
    }
}

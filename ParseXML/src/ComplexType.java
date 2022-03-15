import org.jdom2.Element;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.List;

public class ComplexType
{
    Element ele; //ref to <complexType>
    String name; //name of element, which is complex
    ArrayList<String> tables=new ArrayList<>();
    ComplexType()
    {}
    ComplexType(Element e, String name)//pass reference to complexType  tag
    {

        this.ele=e;
        this.name = name;
    }
    private ArrayList<String> sequence(Element s)
    {
        //take attributes of sequence
        String minOccurs=s.getAttributeValue("minOccurs");
        String maxOccurs=s.getAttributeValue("maxOccurs");
        String Add="";
        if(minOccurs !=null)
        {
            if(minOccurs.equals("0"))
            {}
            else
                Add+="NOT NULL";
        }
        if(maxOccurs !=null)
        {
            if(maxOccurs.equals("1"))
            {}
            else
            {
                //thinking to create table
            }
            Add+="NOT NULL";
        }
        //children
        ArrayList<String> columns=new ArrayList<>();

        List<Element> children = s.getChildren();
        for(int i=0;i<children.size();i++)
        {
            Element e=children.get(i);
            String attname=e.getAttributeValue("name");

            List<Element> subchildren=e.getChildren();
            if(subchildren.size() > 0)//children exists, complex type or simple type. when ele is not simple
            {
                if(subchildren.get(0).getName().equals("complexType"))
                {
                    ComplexType ct=new ComplexType(subchildren.get(0), attname);
                    columns.add(attname+"_id int FOREIGN KEY REFERENCES ( "+attname+"_id)");
                    String tab="CREATE TABLE "+attname+" (\n ";
                    tab+=ct.createTable()+" );\n";
                    tables.add(tab);
                }
                else if(subchildren.get(0).getName().equals("simpleType"))
                {
                    SimpleType col=new SimpleType(subchildren.get(0), attname);
                    String result=col.createColumn();
                    if(result.substring(0,6).equals("CREATE"))
                    {
                        tables.add(result);
                        columns.add(attname+"_id int FOREIGN KEY REFERENCES "+attname+"("+attname+"_id)");
                    }
                    else
                        columns.add(attname+" "+result);
                }
                //create one col here, it is  foreign key, to new table created
            }
            else
            {
                //normal column
                String name=e.getAttributeValue("name");
                String type=e.getAttributeValue("type");
                columns.add(name+" "+type);
            }
        }
        return columns;
    }
    private  ArrayList<String> all(Element a)
    {
        String minOccurs=a.getAttributeValue("minOccurs");

        String Add="";
        if(minOccurs !=null)
        {
            if(minOccurs.equals("1"))
            {
                Add+="NOT NULL";
            }
        }
        System.out.println("Entered all() ADD value= "+Add);
        //children
        ArrayList<String> columns=new ArrayList<>();

        List<Element> children = a.getChildren();
        for(int i=0;i<children.size();i++)
        {
            Element e=children.get(i);
            String attname=e.getAttributeValue("name");

            List<Element> subchildren=e.getChildren();
            if(subchildren.size() > 0)//children exists, complex type or simple type
            {
                if(subchildren.get(0).getName().equals("complexType"))
                {
                    ComplexType ct=new ComplexType(subchildren.get(0), attname);
                    columns.add(attname+"_id int FOREIGN KEY REFERENCES ( "+attname+"_id)");
                    String tab="CREATE TABLE "+attname+" (\n ";
                    tab+=ct.createTable()+" );\n";
                    tables.add(tab);
                }
                else if(subchildren.get(0).getName().equals("simpleType"))
                {
                    SimpleType col=new SimpleType(subchildren.get(0), attname);
                    String result=col.createColumn();
                    if(result.substring(0,6).equals("CREATE"))
                    {
                        tables.add(result);
                        columns.add(attname+"_id int FOREIGN KEY REFERENCES "+attname+"("+attname+"_id)");
                    }
                    else
                        columns.add(attname+" "+result+" "+Add);
                }
                //create one col here, it is  foreign key, to new table created
            }
            else
            {
                //normal column
                String name=e.getAttributeValue("name");
                String type=e.getAttributeValue("type");
                columns.add(name+" "+type+" "+Add);
            }
        }
        return columns;
    }
    private void choice(Element c)
    {
    //can be converted to enum?
    }
    private void group()
    {

    }
    public String createTable()
    {
        List<Element> type=ele.getChildren();
        ArrayList<String> columns=new ArrayList<>();
        columns.add(name+"_id int PRIMARY KEY");
        for(int i=0;i<type.size();i++)
        {
            if(type.get(i).getName().equals("sequence"))
                columns.addAll(sequence(type.get(i)));
            else if(type.get(i).getName().equals("all"))
            {
                System.out.println("Entered if");
                columns.addAll(all(type.get(i)));
            }
            else if(type.get(i).getName().equals("choice"))
                choice(type.get(i));
            else if(type.get(i).getName().equals("attribute"))
            {
                String name=type.get(i).getAttributeValue("name");
                String datatype=type.get(i).getAttributeValue("type");
                columns.add(name+" "+datatype);
            }
        }
        for(String tab:tables)
            System.out.println(tab+"\n---------------------------------------");
        String res="";
        int n=columns.size();
        for(int i=0;i<n;i++)
        {
            res+=columns.get(i);
            if(i!=n-1)
                res+=" ,\n ";
        }
        return res;
    }
}
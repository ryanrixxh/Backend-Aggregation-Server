package xml;

import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;


public class XMLPrinter {
  public static void main(String[] args) {

  }

  public static void print(String xml_string, int num) {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();

      Document doc = db.parse(new ByteArrayInputStream(xml_string.getBytes("UTF-8")));
      doc.getDocumentElement().normalize();

      NodeList nList = doc.getElementsByTagName("*");
      String name = "";

      for(int temp = 0; temp < nList.getLength(); temp++) {
        Node node = nList.item(temp);
        Element e = (Element) node;
        name = e.getNodeName();

        if(name.equals("feed")) {
          System.out.println("");
          System.out.println(name + " " + num + " from ContentServer " + e.getAttribute("id"));
          System.out.println("-------");
        } else if (name.equals("entry")) {
          System.out.println("");
          System.out.println(name);
        } else {
          System.out.println(name + ": " + e.getTextContent());
        }
      }



    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }
}

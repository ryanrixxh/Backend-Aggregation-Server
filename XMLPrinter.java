package xml;

import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;


public class XMLPrinter {
  public static void main(String[] args) {

  }

  public static void print(String xml_string) {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();

      Document doc = db.parse(new ByteArrayInputStream(xml_string.getBytes("UTF-8")));
      doc.getDocumentElement().normalize();

      Element root = doc.getDocumentElement();
      System.out.println(doc.getDocumentElement().getNodeName() + " " + root.getAttribute("id"));



    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }
}

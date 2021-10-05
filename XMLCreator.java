package xml;

import java.io.*;
import java.util.*;
import java.text.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

public class XMLCreator {

  static String idString = null;
  static Document doc = null;
  static String new_string = null;

  public static void main(String[] args) {

  }

  //Takes an input and builds that input into XML format
  public static String build(String inputfile, String contentId) {

    try {
      Scanner sc = new Scanner(new FileReader(inputfile));
      String idString = contentId;

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      doc = db.newDocument();

      Element rootElement = doc.createElement("feed");
      doc.appendChild(rootElement);
      Element first_entry = doc.createElement("entry");
      rootElement.appendChild(first_entry);
      Attr first_atr = doc.createAttribute("id");
      Attr xmlLang = doc.createAttribute("xml:lang");
      Attr xmlns = doc.createAttribute("xmlns");
      first_atr.setValue(idString);
      xmlLang.setValue("en-US");
      xmlns.setValue("http://www.w3.org/2005/Atom");
      rootElement.setAttributeNode(first_atr);
      rootElement.setAttributeNode(xmlLang);
      rootElement.setAttributeNode(xmlns);

      Element current_entry = first_entry;

      while (sc.hasNextLine()) {
        String line = sc.nextLine();
        if (line.equals("entry")) {
          Element entry = doc.createElement("entry");
          rootElement.appendChild(entry);
          current_entry = entry;
        }
        else {
          String[] split = line.split(":");
          Element entryElement = doc.createElement(split[0]);
          entryElement.appendChild(doc.createTextNode(split[1]));
          current_entry.appendChild(entryElement);
        }
      }

      //Write to string
      TransformerFactory tsf = TransformerFactory.newInstance();
      Transformer ts = tsf.newTransformer();
      DOMSource source = new DOMSource(doc);
      StringWriter sw = new StringWriter();
      StreamResult result = new StreamResult(sw);
      ts.transform(source,result);
      new_string = sw.toString();
      ts.transform(source, result);

    }
    catch (IOException e) {
      System.out.println("Broken or missing file.");
    }
    finally {
      return new_string;
    }

  }

}

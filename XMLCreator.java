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

  public static void main(String[] args) {
    build("input_file.txt","feed.xml",1);
  }

  //Takes an input and builds that input into XML format
  public static void build(String inputfile, String outputfile, int contentId) {

    try {
      Scanner sc = new Scanner(new FileReader(inputfile));
      String
      idString = Integer.toString(contentId);

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.newDocument();

      Element rootElement = doc.createElement("feed");
      doc.appendChild(rootElement);
      Element first_entry = doc.createElement("entry");
      rootElement.appendChild(first_entry);
      Attr first_atr = doc.createAttribute("id");
      first_atr.setValue(idString);
      first_entry.setAttributeNode(first_atr);

      Element current_entry = first_entry;

      while (sc.hasNextLine()) {
        String line = sc.nextLine();
        if (line.equals("entry")) {
          Element entry = doc.createElement("entry");
          rootElement.appendChild(entry);
          Attr atr = doc.createAttribute("id");
          atr.setValue(idString);
          entry.setAttributeNode(atr);
          current_entry = entry;
        }
        else {
          String[] split = line.split(":");
          Element entryElement = doc.createElement(split[0]);
          entryElement.appendChild(doc.createTextNode(split[1]));
          current_entry.appendChild(entryElement);
        }
      }

      //Write to xml
      TransformerFactory tsf = TransformerFactory.newInstance();
      Transformer ts = tsf.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(new File(outputfile));
      ts.setOutputProperty(OutputKeys.INDENT, "yes");
      ts.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,"yes");
      ts.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "1");
      ts.transform(source, result);


      //Output
      StreamResult console = new StreamResult(System.out);
      ts.transform(source, console);

    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

}

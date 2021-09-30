import java.io.*;
import java.util.*;
import java.text.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

public class XMLCreator {

  static int id = 001;
  static String idString = null;

  public static void main(String[] args) {

    try {
      idString = Integer.toString(id);

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.newDocument();

      Element rootElement = doc.createElement("feed");
      doc.appendChild(rootElement);

      Element entry = doc.createElement("entry");
      rootElement.appendChild(entry);

      Attr atr = doc.createAttribute("id");
      atr.setValue(idString);
      entry.setAttributeNode(atr);

      Element entryTitle = doc.createElement("title");
      entryTitle.appendChild(doc.createTextNode("My example feed"));
      entry.appendChild(entryTitle);

      //Write to xml
      TransformerFactory tsf = TransformerFactory.newInstance();
      Transformer ts = tsf.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(new File("feed.xml"));
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

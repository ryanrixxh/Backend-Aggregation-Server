package xml;

import java.io.*;
import java.util.*;
import java.text.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

public class XMLSender {

 public static void send(Document tosend, OutputStream channel) throws Exception {
     XMLOutputStream out = new XMLOutputStream(channel);

     StreamResult sr = new StreamResult(out);
     DOMSource ds = new DOMSource(tosend);
     Transformer tf = TransformerFactory.newInstance().newTransformer();

     try {
         tf.transform(ds, sr);
     } catch (Exception e) {
         e.printStackTrace();
     }

     out.send();
 }
}

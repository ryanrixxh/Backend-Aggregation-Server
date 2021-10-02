package xml;

import java.io.*;
import java.util.*;
import java.text.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

public class XMLReceiver {

 public static Document receive(InputStream channel) throws Exception {

     DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
     DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
     Document request = null;


     XMLInputStream xmlin = new XMLInputStream(channel);

     xmlin.recive();

     request = docBuilder.parse(xmlin);

     return request;
 }
}

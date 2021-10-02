package xml;

import java.io.*;
import java.util.*;
import java.text.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

public class XMLOutputStream extends  ByteArrayOutputStream {

 private DataOutputStream outchannel;

 public XMLOutputStream(OutputStream outchannel) {
     super();
     this.outchannel = new DataOutputStream(outchannel);
 }

 public void send() throws IOException {
     byte[] data = toByteArray();
     outchannel.writeInt(data.length);
     outchannel.write(data);
     reset();
 }
}

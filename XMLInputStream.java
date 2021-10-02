package xml;

import java.io.*;
import java.util.*;
import java.text.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

public class XMLInputStream extends ByteArrayInputStream {

 private DataInputStream inchannel;

 public XMLInputStream(InputStream inchannel) {
     super(new byte[2]);
     this.inchannel = new DataInputStream(inchannel);
 }

 public void recive() throws IOException {
     int i = inchannel.readInt();
     byte[] data = new byte[i];
     inchannel.read(data, 0, i);
     this.buf = data;
     this.count = i;
     this.mark = 0;
     this.pos = 0;
 }
}

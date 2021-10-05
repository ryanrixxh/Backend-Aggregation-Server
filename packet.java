package xml;

import java.io.Serializable;

public class Packet implements Serializable {
  public String xml;
  public int timestamp;

  public Packet (String xml_in, int timestamp_in) {
    xml = xml_in;
    timestamp = timestamp_in;
  }
}

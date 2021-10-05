package xml;

public class Packet {
  String xml;
  int timestamp;

  public Packet (String xml_in, int timestamp_in) {
    xml = xml_in;
    timestamp = timestamp_in;
  }
}

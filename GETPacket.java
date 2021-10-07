package xml;

import java.io.Serializable;
import java.util.*;

public class GETPacket implements Serializable {
  public LinkedList<String> xml_list;
  public int timestamp;

  public GETPacket (LinkedList<String> xml_list_in, int timestamp_in) {
    xml_list = xml_list_in;
    timestamp = timestamp_in;
  }
}

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.math.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;


import xml.XMLCreator;
import xml.Packet;

class ContentServer {
  public static String id = null;
  public static String inputfile = null;
  public static int lamport_timestamp = 0;

  public static void main(String[] args) {

    System.out.println("Initial timestamp: " + lamport_timestamp);

    //ContentServer input handling
    Scanner input = new Scanner(System.in);
    id = input.nextLine();
    inputfile = input.nextLine();
    String str = input.nextLine();
    String cutName = str.replace("https://","");
    String[] split = cutName.split(":");
    String servername = split[0];

    int port = Integer.parseInt(split[1]);


    try (Socket socket = new Socket(servername, port)) {

      //write to AtomServer
      PrintWriter out_w = new PrintWriter(socket.getOutputStream(), true);

      //Read from AtomServer
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


      Scanner sc = new Scanner(System.in);
      String line = null;

      out_w.println("PUT / HTTP/1.1");
      String server_response = in.readLine();
      System.out.println(server_response);

      //XML Output to Server
      buildThenSend(out_w, inputfile, id, socket);
      ObjectInputStream inObj = new ObjectInputStream(socket.getInputStream());
      Packet response_packet = (Packet) inObj.readObject();

      String response = response_packet.xml;
      int response_stamp = response_packet.timestamp;
      lamport_timestamp =  Math.max(response_stamp, lamport_timestamp) + 1;
      System.out.println(response);
      System.out.println("Current Timestamp: " + lamport_timestamp);

      if(response.equals("200 - Success"))
        while (true) {
          String new_input = null;
          new_input = input.nextLine();
          Thread.sleep(12000);
          if(new_input != null) {
            out_w.println("PUT");
          } else {
            out_w.println("1");
          }
        }
    }
    catch (IOException e) {
      System.out.println("Broken or missing file.");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void buildThenSend(PrintWriter out_channel, String input, String id, Socket socket_channel) {
    try {
    TransformerFactory tsf = TransformerFactory.newInstance();
    Transformer ts = tsf.newTransformer();
    XMLCreator creator = new XMLCreator();
    String toSend = creator.build(input,id);


    ObjectOutputStream obj = new ObjectOutputStream(socket_channel.getOutputStream());
    lamport_timestamp++;
    Packet packet = new Packet(toSend, lamport_timestamp);
    // List<Packet> packetlist = new LinkedList<>();
    // packetlist.add(packet);
    System.out.println("Sending: " + packet.xml);
    obj.writeObject(packet);

    }
    catch (DOMException e) {
      System.out.println("Error: XML cannot build. Input source is empty or not formatted.");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}

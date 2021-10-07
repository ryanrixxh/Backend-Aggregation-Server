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
  public static int retry_count = 0;
  public static String input = null;
  public static String servername = null;
  public static int port = 0;

  public static void main(String[] args) {
    //ContentServer input handling
    Scanner input = new Scanner(System.in);
    id = input.nextLine();
    inputfile = input.nextLine();
    String str = input.nextLine();
    String cutName = str.replace("https://","");
    String[] split = cutName.split(":");
    servername = split[0];
    port = Integer.parseInt(split[1]);

    run(servername, port, input);
  }

  public static void run(String servername, int port, Scanner input) {
    System.out.println("Initial timestamp: " + lamport_timestamp);

    try (Socket socket = new Socket(servername, port)) {

      //write to AtomServer
      PrintWriter out_w = new PrintWriter(socket.getOutputStream(), true);

      //Read from AtomServer
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      String line = null;
      //Get the length of the xml file to be sent
      int length = getLength(inputfile, id);

      //PUT request header through socket
      System.out.println("PUT /atom.xml HTTP/1.1\r\nUser-Agent: " + id + "\r\nContent Type: application/atom+xml\r\nContent Length: " + length);
      out_w.println("PUT /atom.xml HTTP/1.1\r\nUser-Agent: " + id + "\r\nContent Type: application/atom+xml\r\nContent Length: " + length);

      //PUT request body through socket
      lamport_timestamp++;
      buildThenSend(inputfile, id, socket);
      ObjectInputStream inObj = new ObjectInputStream(socket.getInputStream());
      Packet response_packet = (Packet) inObj.readObject();

      //Process the response
      String response = response_packet.xml;
      int response_stamp = response_packet.timestamp;
      lamport_timestamp =  Math.max(response_stamp, lamport_timestamp) + 1;
      System.out.println(response);
      System.out.println("Current Timestamp: " + lamport_timestamp);

      //If response is successful proceed to send heartbeat
      if(response.equals("200 - Success") || response.equals("201 - HTTP Created")) {
        while (true) {
          String new_input = null;
          new_input = input.nextLine();
          if(new_input.equals("exit")) {
            System.exit(1);
          } else {
            out_w.println("1");
            Thread.sleep(12000);
          }
        }
      } else if(response.equals("204 - No Content")) {
        System.out.println("oops");
      }
    }
    catch (ConnectException e) {
      System.out.println("Error: Server is offline. Retry in 12 seconds");
      try {
        Thread.sleep(12000);
      } catch (InterruptedException ir) {
        ir.printStackTrace();
      }
      System.out.println("Retrying ...");
      retry_count++;
      if(retry_count < 3)
        run(servername, port, input);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static int getLength(String input, String id) {
    int length = 0;
    try {
      TransformerFactory tsf = TransformerFactory.newInstance();
      Transformer ts = tsf.newTransformer();
      XMLCreator creator = new XMLCreator();
      String toSend = creator.build(input,id);

      length = toSend.length();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      return length;
    }
  }

  private static void buildThenSend(String input, String id, Socket socket_channel) {
    try {
    TransformerFactory tsf = TransformerFactory.newInstance();
    Transformer ts = tsf.newTransformer();
    XMLCreator creator = new XMLCreator();
    String toSend = creator.build(input,id);


    ObjectOutputStream obj = new ObjectOutputStream(socket_channel.getOutputStream());
    Packet packet = new Packet(toSend, lamport_timestamp);
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

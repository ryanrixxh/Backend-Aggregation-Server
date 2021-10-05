import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
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
  public static int lamport_timestamp = 1;

  public static void main(String[] args) {

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

      System.out.println(in.readLine());

      while (true) {
        Thread.sleep(12000);
        out_w.println(1);
      }
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
    System.out.println("Sending: " + toSend);
    out_channel.println(toSend);

    ObjectOutputStream obj = new ObjectOutputStream(socket_channel.getOutputStream());
    Packet packet = new Packet(toSend, lamport_timestamp);
    }
    catch (DOMException e) {
      System.out.println("Error: XML cannot build. Input source is empty or not formatted.");
    }
    catch (Exception e) {
      System.out.println("Error");
    }
  }
}

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

class ContentServer {
  public static String id = null;
  public static String inputfile = null;

  public static void main(String[] args) {

    //ContentServer input handling
    Scanner input = new Scanner(System.in);
    System.out.println("Choose any integer for id: ");
    id = input.nextLine();
    System.out.println("State the input file you wish to upload: ");
    inputfile = input.nextLine();
    System.out.println("Enter <connection address>:<port> to make a connection: ");
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
      System.out.println(in.readLine());

      //XML Output to Server
      buildThenSend(out_w, inputfile, id);

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

  private static void buildThenSend(PrintWriter out_channel, String input, String id) {
    try {
    TransformerFactory tsf = TransformerFactory.newInstance();
    Transformer ts = tsf.newTransformer();
    XMLCreator creator = new XMLCreator();
    String toSend = creator.build(input,id);
    System.out.println("Sending: " + toSend);
    out_channel.println(toSend);
    }
    catch (DOMException e) {
      System.out.println("Error: XML cannot build. Input source is empty or not formatted.");
      in.readLine();
    }
    catch (Exception e) {
      System.out.println("Error");
    }
  }
}

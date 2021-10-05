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

  public static void main(String[] args) {

    //ContentServer input handling
    Scanner input = new Scanner(System.in);
    System.out.println("Choose any integer for id: ");
    id = input.nextLine();
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
      TransformerFactory tsf = TransformerFactory.newInstance();
      Transformer ts = tsf.newTransformer();
      XMLCreator creator = new XMLCreator();
      String toSend = creator.build("input_file.txt",id);

      System.out.println("Sending: " + toSend);
      out_w.println(toSend);
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


}

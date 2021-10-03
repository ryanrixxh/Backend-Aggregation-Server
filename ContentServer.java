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
  public static int id = 001;

  public static void main(String[] args) {

    //ContentServer input handling
    Scanner input = new Scanner(System.in);
    String str = input.nextLine();
    String[] split = str.split(":");
    String servername = split[1];
    String cutName = servername.replace("/", "");
    int port = Integer.parseInt(split[2]);


    try (Socket socket = new Socket(cutName, port)) {

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
      String toSend = creator.build("input_file.txt","feed.xml",id);

      System.out.println(toSend);

      // DataOutputStream out = new DataOutputStream(socket.getOutputStream());
      //out.close();

      // out_w.println("Am I alive?");
      // in.readLine();

      while (true) {
        line = sc.nextLine();


        if (line.equalsIgnoreCase("exit")) {
          sc.close();
          socket.close();
          break;
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }


}

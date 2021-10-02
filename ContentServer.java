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
import xml.XMLInputStream;
import xml.XMLOutputStream;
import xml.XMLReceiver;
import xml.XMLSender;

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

    XMLCreator creator = new XMLCreator();
    creator.build("input_file.txt","feed.xml",id);
    File fileSend = new File("feed.xml");


    try (Socket socket = new Socket(cutName, port)) {

      //write to AtomServer
      //PrintWriter out_w = new PrintWriter(socket.getOutputStream(), true);

      //XML transfer to ATOM
      XMLOutputStream os = new XMLOutputStream(socket.getOutputStream());
      XMLSender out = new XMLSender();

      //Read from AtomServer
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      Scanner sc = new Scanner(System.in);
      String line = null;

      //out_w.println("PUT / HTTP/1.1");
      System.out.println(in.readLine());

      Scanner fromFile = new Scanner(new FileReader("feed.xml"));
      String toSend = fromFile.nextLine();
      out_w.println(toSend);
      System.out.println(in.readLine());

      while (true) {
        line = sc.nextLine();

        if (line.equalsIgnoreCase("exit")) {
          sc.close();
          socket.close();
          break;
        }
      }

      sc.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }


}

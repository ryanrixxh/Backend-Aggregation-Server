import java.io.*;
import java.net.*;
import java.util.*;

import xml.XMLPrinter;
import xml.GETPacket;
import xml.Packet;

class GETClient {
  public static int id = 001;
  public static int lamport_timestamp = 0;

  public static void main(String[] args) {

    //Client input handling
    Scanner input = new Scanner(System.in);
    String str = input.nextLine();
    String cutName = str.replace("https://","");
    String[] split = cutName.split(":");
    String servername = split[0];
    int port = Integer.parseInt(split[1]);

    //Socket Connection using input
    try (Socket socket = new Socket(servername, port)) {

      //write to AtomServer
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

      //Increment local counter and send request
      lamport_timestamp++;
      System.out.println("Current timestamp: " + lamport_timestamp);
      System.out.println("GET /atom.xml HTTP/1.1\r\nHost: localhost\r\nUser-Agent: " + id + "\r\nAccept: application/atom+xml");
      out.println("GET /atom.xml HTTP/1.1\r\nHost: localhost\r\nUser-Agent: " + id + "\r\nAccept: application/atom+xml");

      ObjectOutputStream outObj = new ObjectOutputStream(socket.getOutputStream());
      Packet sendPacket = new Packet("GET",lamport_timestamp);
      lamport_timestamp++;
      outObj.writeObject(sendPacket);

      //Recieve xml data and lamport timestamp from server
      ObjectInputStream inObj = new ObjectInputStream(socket.getInputStream());
      GETPacket packet = (GETPacket) inObj.readObject();
      LinkedList<String> recieved_feed = packet.xml_list;
      lamport_timestamp = Math.max(packet.timestamp, lamport_timestamp) + 1;
      System.out.println("Current timestamp: " + lamport_timestamp);

      //Use XML Printer to print the data in readable format
      if (recieved_feed instanceof List) {
      } else {
        System.out.println("Error: Object is not a feed");
        System.exit(1);
      }

      XMLPrinter printer = new XMLPrinter();
      for(int i = 0; i < recieved_feed.size(); i++) {
        printer.print(recieved_feed.get(i), i + 1);
      }
    }
    catch (ConnectException e) {
      System.out.println("Error: Server is offline");
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }
}

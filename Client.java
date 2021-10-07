import java.io.*;
import java.net.*;
import java.util.*;

import xml.XMLPrinter;
import xml.GETPacket;

class Client {
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

      //Read from AtomServer
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      Scanner sc = new Scanner(System.in);
      String line = null;

      //Increment local counter and send data
      lamport_timestamp++;
      System.out.println("Current timestamp: " + lamport_timestamp);
      System.out.println("GET /atom.xml HTTP/1.1\r\nHost: localhost\r\nUser-Agent: " + id + "\r\nAccept: application/atom+xml");
      out.println("GET /atom.xml HTTP/1.1\r\nHost: localhost\r\nUser-Agent: " + id + "\r\nAccept: application/atom+xml");
      System.out.println(in.readLine());

      ObjectInputStream inObj = new ObjectInputStream(socket.getInputStream());

      //Recieve xml data and lamport timestamp from server
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

      while (!"exit".equalsIgnoreCase(line)) {
        //Read from user
        line = sc.nextLine();

        //Send input to AtomServer
        out.println(line);
        out.flush();

        //Server reply
        System.out.println("Server replied: " + in.readLine());
      }

      sc.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }
}

import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.charset.Charset;


import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

import xml.XMLPrinter;
import xml.Packet;

public class AtomServer extends Thread {

  public static List<String> feed = new LinkedList<>();
  public static int lamport_timestamp = 0;

  public static void main(String[] args) {
    ServerSocket server = null;

    try {
      File ATOMFeed = new File("ATOMfeed.xml");

      //Server listens to ServerSocket port
      server = new ServerSocket(4567);
      server.setReuseAddress(true);

      //Loop for recieving ContentServer requests
      while (true) {
        //Socket recieving requests
        Socket socket = server.accept();

        //Print connection confirmation
        System.out.println("New Connection: " + socket.getInetAddress().getHostAddress() + socket.getInetAddress().getHostName());

        BufferedReader type_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter init = new PrintWriter(socket.getOutputStream(), true);
        String type = type_in.readLine();

        //Send initial successful connection response
        init.println("201 - HTTP CREATED");
        init.flush();

        //Create new thread.
        if(type.equals("PUT / HTTP/1.1")) {
          System.out.println("type is server");
          ContentHandler s_handler = new ContentHandler(socket);
          new Thread(s_handler).start();
        }
        else if (type.equals("GET / HTTP/1.1")) {
          System.out.println("type is client");
          ClientHandler c_handler = new ClientHandler(socket);
          new Thread(c_handler).start();
        }

      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      if (server != null) {
        try {
          //Prints out the final feed
          for(String str:feed) {
            System.out.println(str);
          }

          server.close();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  //ContentHandler
  private static class ContentHandler implements Runnable {
    private final Socket c_serverSocket;

    public ContentHandler(Socket socket) {
      this.c_serverSocket = socket;
    }

    public void run() {
      PrintWriter out = null;
      BufferedReader in = null;
      ObjectInputStream inObj = null;
      ObjectOutputStream outObj = null;
      Packet packet = null;


      try {
        System.out.println("Starting ContentHandler Thread ...");


        out = new PrintWriter(c_serverSocket.getOutputStream(), true);

        //Get input stream of ContentServer
        in = new BufferedReader(new InputStreamReader(c_serverSocket.getInputStream()));
        inObj = new ObjectInputStream(c_serverSocket.getInputStream());
        outObj = new ObjectOutputStream(c_serverSocket.getOutputStream());
        packet = (Packet) inObj.readObject();
        lamport_timestamp = Math.max(packet.timestamp, lamport_timestamp) + 1;
        System.out.println("Current timestamp: " + lamport_timestamp);
        //Recieve XML Input from ContentServer
        String xml_string = packet.xml;

        if (xml_string != null) {
        feed.add(xml_string);

        //Parse into XML to find ContentServer ID
        XMLPrinter printer = new XMLPrinter();
        Document doc = printer.parse_string(xml_string);
        NodeList nList = doc.getElementsByTagName("feed");
        Node node = nList.item(0);
        Element e = (Element) node;
        String content_id = e.getAttribute("id");

        lamport_timestamp++;
        Packet response_packet = new Packet("200 - Success", lamport_timestamp);
        outObj.writeObject(response_packet);

        //Reads heartbeat until disconnect occurs
        while (true) {
          Thread.sleep(12000);
          String i = in.readLine();
          if (i.equals("PUT")) {
            System.out.println(i);
          }

          //If heartbeat does not occur find all associated feeds and delete
          if(i == null) {
            System.out.println("Content Server " + content_id + " Disconnected");
            String tester = "id=\"" + content_id + "\"";

            for(int x = 0; x < feed.size(); x++) {
              if (feed.get(x).contains(tester)) {
                System.out.println("Feed " + x + " is from ContentServer " + content_id + ". Removing.");
                feed.remove(feed.get(x));
                x = x-1;
              }
            }
            break;
          }
        }
      } else {
        System.out.println("Error: Recieved empty XML. Sending 204");
        out.println("204 - No Content");
      }

      }
      catch (Exception e) {
        e.printStackTrace();
      }
      finally {
        try {
          if (out != null) {
            out.close();
          }
          if (in != null) {
            in.close();
            c_serverSocket.close();
          }
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  //ClientHandler
  private static class ClientHandler implements Runnable {
    private final Socket client_socket;

    public ClientHandler(Socket socket) {
      this.client_socket = socket;
    }

    public void run() {
      PrintWriter out = null;
      BufferedReader in = null;
      ObjectOutputStream obj = null;

      try {
        System.out.println("Starting Client Thread ...");
        //Get the output of ContentServer
        out = new PrintWriter(client_socket.getOutputStream(), true);

        //Create an object stream to send the feed (or some other object in future)
        obj = new ObjectOutputStream(client_socket.getOutputStream());
        obj.writeObject(feed);

        //Get input stream of ContentServer
        in = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));

        String line;
        while ((line = in.readLine()) != null) {
          System.out.printf("From Client: %s\n", line);
          out.println(line);
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      finally {
        try {
          if (out != null) {
            out.close();
          }
          if (in != null) {
            in.close();
            client_socket.close();
          }
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private static void recieve()
}

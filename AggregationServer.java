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
import xml.GETPacket;

public class AggregationServer extends Thread {

  public static LinkedList<String> feed = new LinkedList<>();
  public static int lamport_timestamp = 0;

  public static void main(String[] args) {
    ServerSocket server = null;

    try {
      //Input Handling
      System.out.println("Would you like to use a custom port y/n: ");
      Scanner scIn = new Scanner(System.in);
      String selection = scIn.nextLine();
      int p = 4567;
      if(selection.equals("y")) {
        System.out.println("Port number: ");
        String p_in = scIn.nextLine();
        p = Integer.parseInt(p_in);
      }

      //Retrieve prior feed if it exists
      feed = get_feed("ATOMFeed.txt");
      System.out.println("Starting server with ATOMFeed file ...\r\nThe server had " + feed.size() + " entries previously stored.");

      //Server listens to ServerSocket on selected port
      server = new ServerSocket(p);
      server.setReuseAddress(true);

      //Loop for recieving ContentServer requests
      while (true) {
        //Socket recieving requests
        Socket socket = server.accept();

        //Print connection confirmation
        System.out.println("New Connection: " + socket.getInetAddress().getHostAddress() + socket.getInetAddress().getHostName());

        BufferedReader request_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter init = new PrintWriter(socket.getOutputStream(), true);
        String request = request_in.readLine();
        for (int i = 0; i < 3; i++) {
          request = request + "\n\r" + request_in.readLine();
        }

        //Create new thread.
        if(request.contains("PUT /atom.xml HTTP/1.1")) {
          System.out.println(request);
          ContentHandler s_handler = new ContentHandler(socket);
          new Thread(s_handler).start();
        }
        else if (request.contains("GET /atom.xml HTTP/1.1")) {
          System.out.print(request);
          ClientHandler c_handler = new ClientHandler(socket);
          new Thread(c_handler).start();
        } else {
          AlternateHandler a_handler = new AlternateHandler(socket);
          new Thread(a_handler).start();
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

  //ContentHandler. Handles any put requests. If it recieves XML data that it can parse it returns either
  //200 or 201 depending on whether the connection comes from a new ID. It then stores the XML as a String in a list
  //ready to serve on a GET request. It returns 204 if the XML it recieved is empty.
  private static class ContentHandler implements Runnable {
    private final Socket c_serverSocket;

    public ContentHandler(Socket socket) {
      this.c_serverSocket = socket;
    }

    public void run()  {
      BufferedReader in = null;
      ObjectInputStream inObj = null;
      ObjectOutputStream outObj = null;
      Packet packet = null;


      try {
        System.out.println("Starting ContentHandler Thread ...");

        //Get input stream of ContentServer
        inObj = new ObjectInputStream(c_serverSocket.getInputStream());
        packet = (Packet) inObj.readObject();
        lamport_timestamp = Math.max(packet.timestamp, lamport_timestamp) + 1;
        System.out.println("Current timestamp: " + lamport_timestamp);
        //Recieve XML Input from ContentServer
        String xml_string = packet.xml;


        if (xml_string != null) {
          //Parse into XML to find ContentServer ID
          XMLPrinter printer = new XMLPrinter();
          Document doc = printer.parse_string(xml_string);
          NodeList nList = doc.getElementsByTagName("feed");
          Node node = nList.item(0);
          Element e = (Element) node;
          String content_id = e.getAttribute("id");

        //If a feed from this id is already here, replace it
          boolean not_new = false;
          for(int x = 0; x < feed.size(); x++) {
            String tester = "id=\"" + content_id + "\"";
            if (feed.get(x).contains(tester)) {
              System.out.println("ContentServer " + content_id + " already has a feed. Replacing it.");
              feed.set(x,xml_string);
              not_new = true;
            }
          }
          //If a feed is from a new content server add it
          if(not_new == false) {
            if(feed.size() < 20) {
              feed.add(xml_string);
              store_feed(feed,"ATOMFeed.txt");
            } else {
              feed.removeFirst();
              feed.add(xml_string);
              store_feed(feed,"ATOMFeed.txt");
            }
          }

            outObj = new ObjectOutputStream(c_serverSocket.getOutputStream());
            lamport_timestamp++;
            Packet response_packet = new Packet("200 - Success", lamport_timestamp);
            if(not_new == false) {
              response_packet.xml = "201 - HTTP Created";
            }
            outObj.writeObject(response_packet);

            //Reads heartbeat until disconnect occurs
            in = new BufferedReader(new InputStreamReader(c_serverSocket.getInputStream()));
            while (true) {
              Thread.sleep(12000);
              String new_input = in.readLine();
              //If heartbeat does not occur find all associated feeds and delete
              if (new_input == null) {
                System.out.println("Content Server " + content_id + " Disconnected");
                String tester = "id=\"" + content_id + "\"";

                for(int x = 0; x < feed.size(); x++) {
                  if (feed.get(x).contains(tester)) {
                    System.out.println("Feed " + x + " is from ContentServer " + content_id + ". Removing.");
                    feed.remove(feed.get(x));
                    store_feed(feed,"ATOMFeed.txt");
                    x = x-1;
                  }
                }
                break;
              }
            }
          } else {
            System.out.println("Error: Recieved empty XML. 204 Sent");
            outObj = new ObjectOutputStream(c_serverSocket.getOutputStream());
            lamport_timestamp++;
            Packet response_packet = new Packet("204 - No Content", lamport_timestamp);
            outObj.writeObject(response_packet);
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      finally {
        try {
          c_serverSocket.close();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  //ClientHandler. Handles GET request. Sends a GET packet object containing the list of XML strings
  //and a lamport timestamp for synchronization.
  private static class ClientHandler implements Runnable {
    private final Socket client_socket;

    public ClientHandler(Socket socket) {
      this.client_socket = socket;
    }

    public void run() {
      PrintWriter out = null;
      BufferedReader in = null;
      ObjectInputStream inObj = null;
      ObjectOutputStream outObj = null;


      try {
        System.out.println("Starting Client Thread ...");

        inObj = new ObjectInputStream(client_socket.getInputStream());
        Packet request = (Packet) inObj.readObject();
        lamport_timestamp = Math.max(request.timestamp, lamport_timestamp) + 1;

        //Send a GETPacket with the xml data and lamport timestamp to the client\
        lamport_timestamp++;
        outObj = new ObjectOutputStream(client_socket.getOutputStream());
        GETPacket toSend = new GETPacket(feed,lamport_timestamp);
        outObj.writeObject(toSend);
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
            client_socket.close();
          }
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  //AlternateHandler. Handles bad requests and sends back a packet containing the timestamp along with an error code 400
  private static class AlternateHandler implements Runnable {
    private final Socket aSocket;

    public AlternateHandler(Socket socket) {
      this.aSocket = socket;
    }

    public void run()  {
      try {
        System.out.println("Bad Request. Sending 400.");
        Packet response_packet = new Packet("400 - Bad Request", lamport_timestamp);
        ObjectOutputStream outObj = new ObjectOutputStream(aSocket.getOutputStream());
        outObj.writeObject(response_packet);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  //Utility function to store a data structure in a seperate file
  private static void store_feed(LinkedList<String> list, String file) {
    try {
      OutputStream fileOut = new FileOutputStream(file);
      ObjectOutputStream fileObjOut = new ObjectOutputStream(fileOut);
      fileObjOut.writeObject(list);
    }
    catch (Exception e){
      e.printStackTrace();
    }
  }

  //Utility function to recieve a feed from any stored location.
  private static LinkedList<String> get_feed(String file) throws NullPointerException {
    LinkedList<String> toReturn = new LinkedList<>();
    try {
      File fileCheck = new File(file);
      if(fileCheck.length() > 0) {
        FileInputStream fileIn = new FileInputStream(file);
        ObjectInputStream objIn = new ObjectInputStream(fileIn);
        @SuppressWarnings("unchecked")
        LinkedList<String> stored_feed = (LinkedList<String>) objIn.readObject();
        toReturn = stored_feed;
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return toReturn;
  }
}

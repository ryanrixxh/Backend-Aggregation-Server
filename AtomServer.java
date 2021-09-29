import java.net.*;
import java.io.*;
import java.util.*;

public class AtomServer extends Thread {

  public static Queue<String> feed = new LinkedList<>();

  public static void main(String[] args) {
    ServerSocket server = null;

    try {

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

        //Create new thread.
        if(type.equals("ContentServer")) {
          System.out.println("type is server");
          ContentHandler s_handler = new ContentHandler(socket);
          new Thread(s_handler).start();
        }
        else if (type.equals("Client")) {
          System.out.println("type is client");
          ClientHandler c_handler = new ClientHandler(socket);
          new Thread(c_handler).start();
        }

      }
    }
    catch (IOException e) {
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

      try {
        System.out.println("Starting ContentHandler Thread ...");


        out = new PrintWriter(c_serverSocket.getOutputStream(), true);

        //Get input stream of ContentServer
        in = new BufferedReader(new InputStreamReader(c_serverSocket.getInputStream()));

        String line;
        while ((line = in.readLine()) != null) {
          //Show recieved ContentServer message
          System.out.printf("From ContentServer: %s\n", line);
          feed.add(line);
          out.println("200 - Success");
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

}

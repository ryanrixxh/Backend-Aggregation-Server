import java.net.*;
import java.io.*;

public class AtomServer extends Thread {

  public static void main(String[] args) {
    ServerSocket server = null;

    try {

      //Server listens to ServerSocket port
      server = new ServerSocket(1234);
      server.setReuseAddress(true);

      //Loop for recieving ContentServer requests
      while (true) {
        //Socket recieving requests
        Socket c_server = server.accept();

        //Send confirmation to ContentServer
        System.out.println("New ContentServer connected" + c_server.getInetAddress().getHostAddres());

        //Create new thread
        ContentHandler c_handler = new ContentHandler(c_server);

        new Thread(c_handler).start();
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    finally {
      if (server != null) {
        try {
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

    public ClientHandler(Socket socket) {
      this.c_serverSocket = socket;
    }

    public void run() {
      PrintWriter out = null;
      BufferedReader in = null;

      try {

        //Get the output of ContentServer
        out = new PrintWriter(c_serverSocket.getOutputStream(), true);

        //Get input stream of ContentServer
        in = new BufferedReader(new InputStreamReader(c_serverSocket.getInputStream()));

        String line;
        while ((line = in.readLine()) != null) {
          //Show recieved ContentServer message
          System.out.printf("From ContentServer: %s\n", line);
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
            c_serverSocket.close();
          }
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}

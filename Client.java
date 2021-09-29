import java.io.*;
import java.net.*;
import java.util.*;


class Client {
  public static void main(String[] args) {

    Scanner input = new Scanner(System.in);
    String servername = input.nextLine();
    int port = Integer.parseInt(input.nextLine());

    try (Socket socket = new Socket(servername, port)) {

      //write to AtomServer
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

      //Read from AtomServer
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      Scanner sc = new Scanner(System.in);
      String line = null;

      out.println("Client");
      System.out.println(in.readLine());

      ObjectInputStream inObj = new ObjectInputStream(socket.getInputStream());

      @SuppressWarnings("unchecked")
      Queue<String> currentFeed = (Queue<String>) inObj.readObject();

      if (currentFeed instanceof Queue) {
        System.out.println(currentFeed);
      } else {
        System.out.println("Error: Object is not a feed");
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
